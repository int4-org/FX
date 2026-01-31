package org.int4.fx.builders.pane;

import java.util.Objects;

import javafx.scene.layout.BorderPane;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link BorderPane} instances.
 */
public final class BorderPaneBuilder extends AbstractRegionBuilder<BorderPane, BorderPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public BorderPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Sets the node for the left region.
   *
   * @param node the node or other supported object to place in the left region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code node} is {@code null}
   * @see BorderPane#setLeft(javafx.scene.Node)
   */
  public BorderPaneBuilder left(Object node) {
    Objects.requireNonNull(node, "node");

    return apply(c -> c.setLeft(NodeBuilder.toNode(node)));
  }

  /**
   * Sets the node for the right region.
   *
   * @param node the node or other supported object to place in the right region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code node} is {@code null}
   * @see BorderPane#setRight(javafx.scene.Node)
   */
  public BorderPaneBuilder right(Object node) {
    Objects.requireNonNull(node, "node");

    return apply(c -> c.setRight(NodeBuilder.toNode(node)));
  }

  /**
   * Sets the node for the top region.
   *
   * @param node the node or other supported object to place in the top region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code node} is {@code null}
   * @see BorderPane#setTop(javafx.scene.Node)
   */
  public BorderPaneBuilder top(Object node) {
    Objects.requireNonNull(node, "node");

    return apply(c -> c.setTop(NodeBuilder.toNode(node)));
  }

  /**
   * Sets the node for the bottom region.
   *
   * @param node the node or other supported object to place in the bottom region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code node} is {@code null}
   * @see BorderPane#setBottom(javafx.scene.Node)
   */
  public BorderPaneBuilder bottom(Object node) {
    Objects.requireNonNull(node, "node");

    return apply(c -> c.setBottom(NodeBuilder.toNode(node)));
  }

  /**
   * Sets the node for the center region.
   *
   * @param node the node or other supported object to place in the center region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code node} is {@code null}
   * @see BorderPane#setCenter(javafx.scene.Node)
   */
  public BorderPaneBuilder center(Object node) {
    Objects.requireNonNull(node, "node");

    return apply(c -> c.setCenter(NodeBuilder.toNode(node)));
  }

  @Override
  public BorderPane build() {
    return initialize(new BorderPane());
  }
}
