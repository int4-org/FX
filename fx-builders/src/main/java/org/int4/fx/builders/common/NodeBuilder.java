package org.int4.fx.builders.common;

import javafx.scene.Node;

/**
 * Builder capable of creating a {@link Node}.
 * <p>
 * This interface marks builders that can produce a {@link Node} and allows
 * them to be accepted transparently in places where builders support arbitrary
 * child objects.
 * <p>
 * In such contexts, callers do not need to invoke {@link #build()} explicitly;
 * the builder will be completed automatically when a {@link Node} is required.
 * This enables fluent composition of builders without manual materialization.
 */
public interface NodeBuilder {

  /**
   * Completes the builder and creates a new {@link Node}.
   *
   * @return a new {@link Node}, never {@code null}
   */
  Node build();
}
