package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.scene.control.TitledPane;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link TitledPane} instances.
 */
public final class TitledPaneBuilder extends AbstractControlBuilder<TitledPane, TitledPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public TitledPaneBuilder(String... styleClasses) {
    super(TitledPane::new, styleClasses);
  }

  /**
   * Sets the title text of the titled pane.
   *
   * @param text the title text
   * @return the fluent builder, never {@code null}
   * @see TitledPane#textProperty()
   */
  public TitledPaneBuilder title(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Sets the graphic of the titled pane.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see TitledPane#setGraphic(javafx.scene.Node)
   */
  public TitledPaneBuilder graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return apply(c -> c.setGraphic(NodeBuilder.toNode(graphic)));
  }

  /**
   * Disables collapsing of the titled pane.
   *
   * @return the fluent builder, never {@code null}
   * @see TitledPane#collapsibleProperty()
   */
  public TitledPaneBuilder uncollapsible() {
    return apply(c -> c.setCollapsible(false));
  }

  /**
   * Configures the titled pane with the given content.
   *
   * @param content a content node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see TitledPane#setContent(javafx.scene.Node)
   */
  public TitledPaneBuilder content(Object content) {
    Objects.requireNonNull(content, "content");

    return apply(node -> node.setContent(NodeBuilder.toNode(content)));
  }
}
