package org.int4.fx.builders.common;

import java.util.Objects;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import org.int4.fx.builders.context.BuildContext;
import org.int4.fx.builders.context.BuildContexts;
import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.builders.internal.ShowingStateListener;
import org.int4.fx.scene.event.Broadcasts;

/**
 * Fluent builder for constructing JavaFX {@link Scene} instances. Scenes
 * created with this builder support broadcasting of {@link WindowEvent}s
 * to all nodes when the window is shown or hidden.
 * <p>
 * When a scene is showing, interested nodes can listen to these events to set
 * up essential bindings when the window shows, and remove them when the window is
 * hidden. This allows for deterministic management of listeners and bindings
 * adding and/or removing them just in time without the need for weak listeners.
 * <p>
 * A {@code SceneBuilder} finalizes a scene graph rooted at a given object,
 * applying all registered configuration options and activating build-time
 * context handling during construction.
 * <p>
 * The root value may be a {@link Node}, a nested builder, or any object that
 * resolves to a {@link Parent}. Nested builders are finalized automatically
 * when the scene is built.
 *
 * @see WindowEvent
 * @see Broadcasts
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
     * avoid noticable UI jumps. This is better than listening to the showing property, which
     * only becomes true after the Window is already visible.
     */

    EventHandler<javafx.stage.WindowEvent> showingHandler = e -> Broadcasts.broadcast(scene, WindowEvent.showing(scene.getWindow()));
    EventHandler<javafx.stage.WindowEvent> hiddenHandler = e -> Broadcasts.broadcast(scene, WindowEvent.hidden(scene.getWindow()));

    scene.windowProperty().subscribe((ov, nv) -> {
      if(ov != null) {
        ov.removeEventHandler(javafx.stage.WindowEvent.WINDOW_SHOWING, showingHandler);
        ov.removeEventHandler(javafx.stage.WindowEvent.WINDOW_HIDDEN, hiddenHandler);
      }
      if(nv != null) {
        nv.addEventHandler(javafx.stage.WindowEvent.WINDOW_SHOWING, showingHandler);
        nv.addEventHandler(javafx.stage.WindowEvent.WINDOW_HIDDEN, hiddenHandler);
      }
    });

    // Mark this scene as one that supports broadcasting window events:
    scene.getProperties().put(ShowingStateListener.SHOW_STATE_MANAGING_SCENE, null);

    /*
     * Now that the Scene has the correct property in place, set the actual root:
     */

    scene.setRoot(root);

    // TODO alternative show state management?
    // it may be possible to do this even simpler; if a scene that detects it isn't
    // showing, it could null out its root (or set it to a Label); that should trigger
    // all scene properties of the old node graph to be updated; once showing again,
    // the scene can set its root back to what it was
    // There are some advantages to this approach:
    // - No need to store things in Properties for interested Nodes
    // - No need to walk the graph ourselves (Node will do this and null out all
    //   scenes)
    // - Nodes can just bind to their local Scene property to manage listeners
    // - No special marking of the Scene is needed as the procedure is the same
    //   for the standard and show state managing scene
    // Disadvantage:
    // - A lot heavier, as lots of things get cleaned up

    return scene;
  }
}
