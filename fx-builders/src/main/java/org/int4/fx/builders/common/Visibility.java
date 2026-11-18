package org.int4.fx.builders.common;

import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import org.int4.common.builder.OptionBuilder;

/**
 * Capability interface for builders whose nodes can be hidden from rendering and layout.
 * <p>
 * Implementing builders expose {@link #visible(ObservableValue)} and
 * {@link #invisible(ObservableValue)}, which bind both the visible and managed
 * properties of the node being built.
 * <p>
 * This interface is intentionally not implemented by every node builder.
 * Builders whose node is a decorator for other content should not implement
 * it, so that their presence follows the content they wrap instead of being
 * configured independently.
 *
 * @param <N> the type of {@link Node} being built
 * @param <B> the concrete builder type
 */
public interface Visibility<N extends Node, B extends OptionBuilder<N, B>> extends OptionBuilder<N, B> {

  /**
   * Binds the visibility and managed state of the node to the negation of the given condition.
   * <p>
   * This is a convenience method equivalent to
   * {@code visible(when.map(b -> !b))}.
   *
   * @param when an observable condition controlling invisibility, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code when} is {@code null}
   * @see Node#visibleProperty()
   * @see Node#managedProperty()
   */
  default B invisible(ObservableValue<Boolean> when) {
    return visible(when.map(b -> !b));
  }

  /**
   * Binds both {@link Node#visibleProperty()} and
   * {@link Node#managedProperty()} to the given condition.
   * <p>
   * When the condition is {@code false}, the node will be hidden and
   * excluded from layout calculations.
   *
   * @param when an observable condition controlling visibility
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code when} is {@code null}
   * @see Node#visibleProperty()
   * @see Node#managedProperty()
   */
  default B visible(ObservableValue<Boolean> when) {
    Objects.requireNonNull(when, "when");

    return apply(n -> {
      n.managedProperty().bind(when);
      n.visibleProperty().bind(when);
    });
  }
}
