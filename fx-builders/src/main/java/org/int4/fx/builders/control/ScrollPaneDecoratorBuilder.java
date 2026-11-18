package org.int4.fx.builders.control;

import javafx.scene.control.ScrollPane;

import org.int4.fx.builders.common.AbstractRegionBaseBuilder;
import org.int4.fx.builders.common.NodeBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Decorator builder for wrapping a node in a {@link ScrollPane}.
 * <p>
 * Unlike {@link ScrollPaneBuilder}, this builder derives the scroll pane's
 * visibility and managed state from the wrapped content. It does not expose
 * independent visibility or content configuration.
 */
public final class ScrollPaneDecoratorBuilder
  extends AbstractRegionBaseBuilder<ScrollPane, ScrollPaneDecoratorBuilder>
  implements ScrollPaneOptions<ScrollPaneDecoratorBuilder>
{

  /**
   * Creates a new builder wrapping the given content.
   *
   * @param content a builder for the content to wrap, cannot be {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   */
  ScrollPaneDecoratorBuilder(NodeBuilder<?> content) {
    applyContentStrategy(content, (scrollPane, node) -> {
      scrollPane.setContent(node);
      scrollPane.visibleProperty().bind(node.visibleProperty());
      scrollPane.managedProperty().bind(node.managedProperty());
    });
  }

  @Override
  public ScrollPane build(BuildContext context) {
    return initialize(context, new ScrollPane());
  }
}

