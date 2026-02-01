package org.int4.fx.builders.pane;

import java.util.Objects;

import javafx.scene.Group;

import org.int4.fx.builders.common.AbstractNodeBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Builder for {@link Group} instances.
 */
public final class GroupBuilder extends AbstractNodeBuilder<Group, GroupBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public GroupBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Configures the group with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see Group#getChildren()
   */
  public GroupBuilder nodes(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return applyChildrenStrategy(nodes, (n, v) -> n.getChildren().setAll(v));
  }

  @Override
  public Group build(BuildContext context) {
    return initialize(context, new Group());
  }
}
