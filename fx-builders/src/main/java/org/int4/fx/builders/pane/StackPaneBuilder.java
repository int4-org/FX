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
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public StackPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Creates the stack pane with the given items.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created {@link StackPane}, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see StackPane#getChildren()
   */
  public StackPane nodes(Object... nodes) {
    return initialize(new StackPane(NodeBuilder.toNodes(nodes)));
  }

  @Override
  public StackPane build() {
    return initialize(new StackPane());
  }
}
