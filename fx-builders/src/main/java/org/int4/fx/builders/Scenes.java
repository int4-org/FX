package org.int4.fx.builders;

import javafx.scene.Parent;
import javafx.scene.Scene;

import org.int4.fx.builders.common.SceneBuilder;
import org.int4.fx.builders.context.BuildContext;
import org.int4.fx.builders.event.WindowEvent;

/**
 * Creates {@link Scene scenes} which support broadcasting of {@link WindowEvent}s
 * to all nodes when the window is shown or hidden.
 * <p>
 * When a scene is showing, interested nodes can listen to these events to set
 * up essential bindings when the window shows, and remove them when the window is
 * hidden. This allows for deterministic management of listeners and bindings
 * adding and/or removing them just in time without the need for weak listeners.
 *
 * @see WindowEvent
 * @see org.int4.fx.scene.event.Broadcasts
 */
public class Scenes {

  /**
   * Creates a {@link Scene} with the given root.
   * <p>
   * This method accepts any object, as long as it is or can be converted to a {@link Parent}
   * node.
   *
   * @param root a root node or supported type, cannot be {@code null}
   * @return a {@link Scene}, never {@code null}
   * @throws IllegalArgumentException if root node is not a {@link Parent} node
   * @throws NullPointerException when any argument is {@code null}
   */
  public static Scene create(Object root) {
    return create(null, root);
  }

  /**
   * Creates a {@link Scene} with the given root.
   * <p>
   * This method accepts any object, as long as it is or can be converted to a {@link Parent}
   * node.
   *
   * @param context a {@link BuildContext}, can be {@code null}
   * @param root a root node or supported type, cannot be {@code null}
   * @return a {@link Scene}, never {@code null}
   * @throws IllegalArgumentException if root node is not a {@link Parent} node
   * @throws NullPointerException when any argument is {@code null}
   */
  public static Scene create(BuildContext context, Object root) {
    return new SceneBuilder(root).build(context);
  }

  private Scenes() {}
}
