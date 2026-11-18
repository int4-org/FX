package org.int4.fx.builders.control;

import javafx.scene.control.ScrollPane;

import org.int4.fx.builders.common.Decorator;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Decorator that wraps a node in a {@link ScrollPane}.
 * <p>
 * Use {@link #INSTANCE} with
 * {@link org.int4.fx.builders.common.AbstractNodeBuilder#decorate(Decorator)}
 * to make an existing node scrollable. The resulting
 * {@link ScrollPaneDecoratorBuilder} follows the visibility and managed state
 * of the wrapped content and does not expose independent content or visibility
 * configuration.
 */
public enum ScrollPaneDecorator implements Decorator<ScrollPane, ScrollPaneDecoratorBuilder> {

  /**
   * The shared scroll pane decorator.
   */
  INSTANCE;

  @Override
  public ScrollPaneDecoratorBuilder decorate(NodeBuilder<?> inner) {
    return new ScrollPaneDecoratorBuilder(inner);
  }
}
