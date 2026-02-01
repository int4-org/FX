package org.int4.fx.builders.common;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import org.int4.fx.builders.context.BuildContext;
import org.int4.fx.builders.context.BuildContexts;
import org.int4.fx.builders.internal.ShowingStateListener;

/**
 * Fluent builder for constructing JavaFX {@link Scene} instances.
 * <p>
 * A {@code SceneBuilder} finalizes a scene graph rooted at a given object,
 * applying all registered configuration options and activating build-time
 * context handling during construction.
 * <p>
 * The root value may be a {@link Node}, a nested builder, or any object that
 * resolves to a {@link Parent}. Nested builders are finalized automatically
 * when the scene is built.
 */
public class SceneBuilder extends AbstractOptionBuilder<Scene, SceneBuilder> {
  private final Object root;

  /**
   * Creates a new scene builder with the given root.
   * <p>
   * The root must ultimately resolve to a {@link Parent} when the scene is
   * constructed.
   *
   * @param root the root node or root builder of the scene, cannot be {@code null}
   * @throws NullPointerException if {@code root} is {@code null}
   */
  public SceneBuilder(Object root) {
    this.root = Objects.requireNonNull(root, "root");
  }

  /**
   * Builds the scene using the currently active build context.
   *
   * @return the constructed {@link Scene}, never {@code null}
   * @throws IllegalArgumentException if the root does not resolve to a {@link Parent}
   */
  public Scene build() {
    return build(null);
  }

  /**
   * Builds the scene using the given parent build context.
   * <p>
   * If {@code parentContext} is {@code null}, the currently active context is
   * used. During construction, the provided context becomes active for the
   * duration of the build operation.
   *
   * @param parentContext the build context to use, or {@code null} to use the active context
   * @return the constructed {@link Scene}, never {@code null}
   * @throws IllegalArgumentException if the root does not resolve to a {@link Parent}
   */
  public Scene build(BuildContext parentContext) {
    return BuildContexts.with(parentContext == null ? BuildContexts.activeContext() : parentContext, () -> {
      // TODO tough to use content stategy here, as scene is not a node...
      //parentContext.resolve(ContentStrategy::base).apply()
      Object node = root instanceof NodeBuilder<?> nb ? nb.build() : root;

      if(node instanceof Parent p) {
        Scene scene = create(p);

        initialize(scene);

        return scene;
      }

      throw new IllegalArgumentException("root node must be a Parent, but was: " + root);
    });
  }

  private static Scene create(Parent root) {

    /*
     * Create a scene with a dummy node first, as a property should be set on the Scene
     * before all nodes have their sceneProperty callbacks called:
     */

    Region dummy = new Region();
    Scene scene = new Scene(dummy);

    /*
     * Install listener to automatically subscribe/unsubscribe listeners on all UI components
     * when visibility changes. This means that once a window is hidden (in order to discard
     * it completely), listeners managed in this way will no longer contribute to keeping the
     * scene and the entire scene graph alive (preventing garbage collection) without relying
     * on weak listeners.
     *
     * A WINDOW_SHOWING handler is used as it is called prior to the Window becoming visible
     * allowing any interested UI components to prepare before the Window becomes visible to
     * avoid noticable UI jumps.
     */

    scene.windowProperty()
      .flatMap(Window::showingProperty)
      .orElse(false)
      .subscribe(v -> notifyShowStatusChange(scene.getRoot(), v));

    // Mark this scene as one that will do showing state listener callbacks:
    scene.getProperties().put(ShowingStateListener.SHOW_STATE_MANAGING_SCENE, null);

    /*
     * Now that the Scene has the correct property in place, set the actual root:
     */

    scene.setRoot(root);

    return scene;
  }

  private static void notifyShowStatusChange(Node control, boolean showing) {

    /*
     * Efficiently propagate the show status change through-out the scene graph
     * without requiring potentially thousands of listeners on a single property
     * of Scene or Window for large graphs:
     */

    if(control instanceof Parent p) {
      for(Node child : p.getChildrenUnmodifiable()) {
        notifyShowStatusChange(child, showing);
      }
    }

    if(control.hasProperties() && control.getProperties().get(ShowingStateListener.KEY) instanceof ShowingStateListener listener) {
      listener.showStatusChanged(showing);
    }
  }
}
