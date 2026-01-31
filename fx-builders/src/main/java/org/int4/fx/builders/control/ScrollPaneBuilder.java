package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.scene.control.ScrollPane;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link ScrollPane} instances.
 */
public final class ScrollPaneBuilder extends AbstractControlBuilder<ScrollPane, ScrollPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ScrollPaneBuilder(String... styleClasses) {
    super(ScrollPane::new, styleClasses);
  }

  /**
   * Configures the scroll pane to fit its content to the available width.
   *
   * @return the fluent builder, never {@code null}
   * @see ScrollPane#fitToWidthProperty()
   */
  public ScrollPaneBuilder fitToWidth() {
    return apply(c -> c.setFitToWidth(true));
  }

  /**
   * Configures the scroll pane to fit its content to the available height.
   *
   * @return the fluent builder, never {@code null}
   * @see ScrollPane#fitToHeightProperty()
   */
  public ScrollPaneBuilder fitToHeight() {
    return apply(c -> c.setFitToHeight(true));
  }

  /**
   * Configures the scroll pane with the given content.
   *
   * @param content a content node or other supported object, cannot be {@code null}
   * @return the created {@link ScrollPane}, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see ScrollPane#setContent(javafx.scene.Node)
   */
  public ScrollPaneBuilder content(Object content) {
    Objects.requireNonNull(content, "content");

    return apply(node -> node.setContent(NodeBuilder.toNode(content)));
  }
}
