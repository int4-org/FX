package org.int4.fx.builders.strategy;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.scene.Node;

import org.int4.fx.builders.context.BuildStrategy;

/**
 * A strategy for applying text values to nodes.
 * <p>
 * This strategy defines how builder-supplied text values are interpreted
 * and applied to a node. Implementations may perform simple assignment,
 * transformation, localization, formatting, or other processing before
 * setting the text.
 */
public interface TextStrategy extends BuildStrategy<TextStrategy> {

  /**
   * The base text strategy, which applies the input directly to the node by
   * calling {@link Objects#toString()}.
   */
  static final TextStrategy BASE = (node, input, setter) -> setter.accept(Objects.toString(input));

  /**
   * Returns the base text strategy.
   * <p>
   * This strategy performs a direct assignment of the input text to the node.
   *
   * @return the base text strategy, never {@code null}
   */
  static TextStrategy base() {
    return BASE;
  }

  @Override
  default Class<TextStrategy> type() {
    return TextStrategy.class;
  }

  /**
   * Applies the given text value to the specified node.
   * <p>
   * Implementations may transform, localize, validate, or otherwise process
   * the input text before applying it.
   *
   * @param node the node to modify, cannot be {@code null}
   * @param input the value supplied to the builder, may be {@code null}
   * @param setter a consumer that applies the text to the parent, cannot be {@code null}
   * @throws NullPointerException if {@code node} is {@code null}
   */
  void apply(Node node, Object input, Consumer<String> setter);
}