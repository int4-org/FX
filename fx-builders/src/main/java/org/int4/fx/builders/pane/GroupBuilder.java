package org.int4.fx.builders.pane;

import javafx.scene.Group;

import org.int4.fx.builders.common.AbstractNodeBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link Group} instances.
 */
public final class GroupBuilder extends AbstractNodeBuilder<Group, GroupBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public GroupBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Creates the group with the given items.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created {@link Group}, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see Group#getChildren()
   */
  public Group nodes(Object... nodes) {
    return initialize(new Group(NodeBuilder.toNodes(nodes)));
  }

  @Override
  public Group build() {
    return initialize(new Group());
  }
}
