package org.int4.fx.builders.strategy;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.scene.Node;

import org.int4.fx.builders.context.BuildStrategy;

/**
 * A strategy for processing and applying children supplied to a builder.
 * <p>
 * Implementations are responsible for transforming the builder-provided
 * children into JavaFX {@link Node}s and applying them to the constructed
 * parent node.
 */
public interface ChildrenStrategy extends BuildStrategy<ChildrenStrategy> {

  /**
   * The base children strategy.
   */
  static final ChildrenStrategy BASE = (node, inputChildren, setter) -> setter.accept(
    inputChildren.stream().filter(Objects::nonNull).map(obj -> NodeConverter.toNode(obj)).toList()
  );

  /**
   * Returns the base children strategy.
   *
   * @return the default {@code ChildrenStrategy} implementation, never {@code null}
   */
  static ChildrenStrategy base() {
    return BASE;
  }

  @Override
  default Class<ChildrenStrategy> type() {
    return ChildrenStrategy.class;
  }

  /**
   * Processes the given input children and applies the resulting nodes using
   * the provided setter.
   * <p>
   * The {@code inputChildren} list contains the values supplied to the builder
   * and may include arbitrary objects, or {@code null} values. Any nested
   * builders will already have been resolved to {@link Node} instances before
   * this method is invoked.
   *
   * @param node the parent node to which the children belong, cannot be {@code null}
   * @param inputChildren the children supplied to the builder, cannot be {@code null}
   *   but may be empty or contain {@code null} values
   * @param setter a consumer that applies the resolved children to the parent
   *   node, cannot be {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  void apply(Node node, List<Object> inputChildren, Consumer<List<Node>> setter);
}