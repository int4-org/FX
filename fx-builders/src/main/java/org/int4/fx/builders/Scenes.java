package org.int4.fx.builders;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

import org.int4.fx.builders.common.NodeBuilder;
import org.int4.fx.builders.internal.Builders;
import org.int4.fx.builders.internal.ShowingStateListener;

/**
 * Creates Scenes that will send signals to {@link ShowingStateListener}s when
 * the show state of the scene changes.
 * <p>
 * When a scene is showing, such listeners can set up essential bindings, while
 * when the scene is hidden (and perhaps will no longer be used) bindings can be
 * removed. This allows for deterministic management of listeners and bindings
 * adding and/or removing them just in time without the need for weak listeners.
 */
public class Scenes {

  /**
   * Creates a {@link Scene} with the given root.
   * <p>
   * This method accepts any object, as long as it is or can be converted to a {@link Parent}
   * node. If conversion fails, a place holder node is created with a tooltip explaining
   * the problem. See {@link NodeBuilder} for more information.
   *
   * @param root a object that is a {@link Parent} or can be converted to one, cannot be {@code null}
   * @return a {@link Scene}, never {@code null}
   * @throws IllegalArgumentException if root node is not a {@link Parent} node
   * @throws NullPointerException when any argument is {@code null}
   */
  public static Scene create(Object root) {
    if(Builders.toNode(Objects.requireNonNull(root, "root")) instanceof Parent p) {
      Scene scene = new Scene(p);

      /*
       * Install listener to automatically subscribe/unsubscribe listeners on all UI components
       * when visibility changes. This means that once a window is hidden (in order to discard
       * it completely), listeners managed in this way will no longer contribute to keeping the
       * scene and the entire scene graph alive (preventing garbage collection) without relying
       * on weak listeners.
       */

      scene.windowProperty()
        .flatMap(Window::showingProperty)
        .orElse(false)
        .subscribe(v -> notifyShowStatusChange(scene.getRoot(), v));

      // Mark this scene as one that will do showing state listener callbacks:
      scene.getProperties().put(ShowingStateListener.SHOW_STATE_MANAGING_SCENE, null);

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

    throw new IllegalArgumentException("root node must be a Parent node: " + root);
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
