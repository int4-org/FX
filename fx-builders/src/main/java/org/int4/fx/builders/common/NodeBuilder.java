package org.int4.fx.builders.common;

import javafx.scene.Node;

import org.int4.fx.builders.context.BuildContext;

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
 *
 * @param <N> the type of node being build
 */
public interface NodeBuilder<N extends Node> {

  /**
   * Completes the builder, using a default context, and creates a new
   * {@link Node}.
   *
   * @return a new {@link Node}, never {@code null}
   */
  default N build() {
    return build(null);
  }

  /**
   * Completes the builder, using the given context for the builder (and nested
   * builders), and creates a new {@link Node}.
   *
   * @param context a {@link BuildContext}, can be {@code null}
   * @return a new {@link Node}, never {@code null}
   */
  N build(BuildContext context);
}
