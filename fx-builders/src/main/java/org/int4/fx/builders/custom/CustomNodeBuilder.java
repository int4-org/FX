package org.int4.fx.builders.custom;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.scene.Node;

import org.int4.fx.builders.common.AbstractNodeBuilder;

/**
 * Builder for custom {@link Node} instances created via a supplied factory.
 * <p>
 * This builder allows arbitrary {@link Node} subclasses to participate in the
 * fluent builder API, including styling and common node options.
 *
 * @param <N> the concrete {@link Node} type
 */
public class CustomNodeBuilder<N extends Node> extends AbstractNodeBuilder<N, CustomNodeBuilder<N>> {
  private final Supplier<N> nodeSupplier;

  /**
   * Creates a new builder for a custom node.
   *
   * @param nodeSupplier the supplier used to create the node, cannot be {@code null}
   * @param styleClasses the style classes to apply to the node, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public CustomNodeBuilder(Supplier<N> nodeSupplier, String... styleClasses) {
    super(styleClasses);

    this.nodeSupplier = Objects.requireNonNull(nodeSupplier, "nodeSupplier");
  }

  @Override
  public N build() {
    return initialize(nodeSupplier.get());
  }
}