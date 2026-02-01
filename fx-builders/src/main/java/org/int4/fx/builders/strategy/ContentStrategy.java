package org.int4.fx.builders.strategy;

import java.util.function.Consumer;

import javafx.scene.Node;

import org.int4.fx.builders.context.BuildStrategy;

/**
 * A strategy for processing and applying a single content value to a node.
 * <p>
 * Implementations are responsible for transforming the builder-provided
 * content value into a JavaFX {@link Node} and applying it to the constructed
 * parent node.
 */
public interface ContentStrategy extends BuildStrategy<ContentStrategy> {

  /**
   * The base content strategy.
   */
  static final ContentStrategy BASE = (node, content, setter) -> setter.accept(NodeConverter.toNode(content));

  /**
   * Returns the base content strategy.
   *
   * @return the default {@code ContentStrategy} implementation, never {@code null}
   */
  static ContentStrategy base() {
    return BASE;
  }

  @Override
  default Class<ContentStrategy> type() {
    return ContentStrategy.class;
  }

  /**
   * Processes the given content value and applies the resulting node using
   * the provided setter.
   * <p>
   * The {@code content} parameter represents the value supplied to the builder
   * and may be an arbitrary object. A nested builders will already have been
   * resolved to a {@link Node} before this method is invoked.
   *
   * @param node the parent node to which the content belongs, cannot be {@code null}
   * @param content the content supplied to the builder, cannot be {@code null}
   * @param setter a consumer that applies the resolved content node to the parent,
   *   cannot be {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  void apply(Node node, Object content, Consumer<Node> setter);
}