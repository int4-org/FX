package org.int4.fx.builders.pane;

import javafx.scene.layout.VBox;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link VBox} instances.
 */
public final class VBoxBuilder extends AbstractRegionBuilder<VBox, VBoxBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public VBoxBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Configures the vbox with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see VBox#getChildren()
   */
  public VBoxBuilder nodes(Object... nodes) {
    return apply(node -> node.getChildren().addAll(NodeBuilder.toNodes(nodes)));
  }

  /**
   * Creates the vbox with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created node, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see VBox#getChildren()
   */
  public VBox with(Object... nodes) {
    nodes(nodes);

    return build();
  }

  @Override
  public VBox build() {
    return initialize(new VBox());
  }
}
