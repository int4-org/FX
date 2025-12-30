package org.int4.fx.builders.control;

import javafx.scene.control.ScrollPane;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link ScrollPane} instances.
 */
public final class ScrollPaneBuilder extends AbstractControlBuilder<ScrollPane, ScrollPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
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
   * Creates the scroll pane with the given content.
   *
   * @param content a content node or other supported object, cannot be {@code null}
   * @return the created {@link ScrollPane}, never {@code null}
   * @throws IllegalArgumentException if {@code content} is {@code null}
   * @see ScrollPane#setContent(javafx.scene.Node)
   */
  public ScrollPane content(Object content) {
    ScrollPane node = build();

    node.setContent(Builders.toNode(content));

    return node;
  }
}
