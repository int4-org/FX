package org.int4.fx.controls;

import org.int4.fx.builders.common.Decorator;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Decorator that wraps a node in a {@link ResizablePane}.
 * <p>
 * Use {@link #INSTANCE} with
 * {@link org.int4.fx.builders.common.AbstractNodeBuilder#decorate(Decorator)}
 * to make an existing node resizable. The resulting
 * {@link ResizablePaneDecoratorBuilder} follows the visibility and managed
 * state of the wrapped content and does not expose independent content or
 * visibility configuration.
 */
public enum ResizablePaneDecorator implements Decorator<ResizablePane, ResizablePaneDecoratorBuilder> {

  /**
   * The shared resizable pane decorator.
   */
  INSTANCE;

  @Override
  public ResizablePaneDecoratorBuilder decorate(NodeBuilder<?> inner) {
    return new ResizablePaneDecoratorBuilder(inner);
  }
}
