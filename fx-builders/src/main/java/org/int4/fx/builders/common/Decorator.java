package org.int4.fx.builders.common;

import javafx.scene.Node;

/**
 * Describes how to wrap a node builder in another node.
 * <p>
 * Decorators are used through {@link AbstractNodeBuilder#decorate(Decorator)}.
 * Implementations attach the given inner builder as content to the wrapping
 * node and return a builder for that node. Decorator builders generally derive
 * their visibility and managed state from the wrapped content, so they do not
 * expose independent content or visibility configuration.
 * <p>
 * This is a functional interface; implementations can be supplied as a lambda
 * or method reference.
 *
 * @param <N> the type of node created by the decorator
 * @param <B> the concrete decorator builder type
 */
@FunctionalInterface
public interface Decorator<N extends Node, B extends AbstractNodeBuilder<N, B>> {

  /**
   * Wraps the given builder in a new node.
   *
   * @param inner a builder for the content to wrap, cannot be {@code null}
   * @return a builder for the wrapping node, never {@code null}
   * @throws NullPointerException if {@code inner} is {@code null}
   */
  B decorate(NodeBuilder<?> inner);
}
