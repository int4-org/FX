package org.int4.fx.builders;

import javafx.scene.Parent;
import javafx.scene.Scene;

import org.int4.fx.builders.common.SceneBuilder;
import org.int4.fx.builders.context.BuildContext;
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
