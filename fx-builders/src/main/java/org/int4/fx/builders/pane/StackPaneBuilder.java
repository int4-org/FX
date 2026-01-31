package org.int4.fx.builders.pane;

import javafx.scene.layout.StackPane;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link StackPane} instances.
 */
public final class StackPaneBuilder extends AbstractRegionBuilder<StackPane, StackPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public StackPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Configures the stack pane with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see StackPane#getChildren()
   */
  public StackPaneBuilder nodes(Object... nodes) {
    return apply(node -> node.getChildren().addAll(NodeBuilder.toNodes(nodes)));
  }

  /**
   * Creates the stack pane with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created node, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see StackPane#getChildren()
   */
  public StackPane with(Object... nodes) {
    nodes(nodes);

    return build();
  }

  @Override
  public StackPane build() {
    return initialize(new StackPane());
  }
}
