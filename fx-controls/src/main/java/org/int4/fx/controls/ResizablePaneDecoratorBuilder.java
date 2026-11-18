package org.int4.fx.controls;

import org.int4.fx.builders.common.AbstractRegionBaseBuilder;
import org.int4.fx.builders.common.NodeBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Decorator builder for wrapping a node in a {@link ResizablePane}.
 * <p>
 * The resizable pane follows the visibility and managed state of the wrapped
 * content. Content and visibility are not independently configurable.
 */
public final class ResizablePaneDecoratorBuilder extends AbstractRegionBaseBuilder<ResizablePane, ResizablePaneDecoratorBuilder> {

  /**
   * Creates a new builder wrapping the given content.
   *
   * @param content a builder for the content to wrap, cannot be {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   */
  ResizablePaneDecoratorBuilder(NodeBuilder<?> content) {
    applyContentStrategy(content, (resizablePane, node) -> {
      resizablePane.setContent(node);
      resizablePane.visibleProperty().bind(node.visibleProperty());
      resizablePane.managedProperty().bind(node.managedProperty());
    });
  }

  @Override
  public ResizablePane build(BuildContext context) {
    return initialize(context, new ResizablePane());
  }
}
