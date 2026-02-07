package org.int4.fx.builders.pane;

import java.util.Objects;

import javafx.scene.layout.BorderPane;

import org.int4.fx.builders.common.AbstractPaneBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Builder for {@link BorderPane} instances.
 */
public final class BorderPaneBuilder extends AbstractPaneBuilder<BorderPane, BorderPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public BorderPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Sets the node for the left region.
   *
   * @param content the node or other supported object to place in the left region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see BorderPane#setLeft(javafx.scene.Node)
   */
  public BorderPaneBuilder left(Object content) {
    Objects.requireNonNull(content, "content");

    return applyContentStrategy(content, BorderPane::setLeft);
  }

  /**
   * Sets the node for the right region.
   *
   * @param content the node or other supported object to place in the right region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see BorderPane#setRight(javafx.scene.Node)
   */
  public BorderPaneBuilder right(Object content) {
    Objects.requireNonNull(content, "content");

    return applyContentStrategy(content, BorderPane::setRight);
  }

  /**
   * Sets the node for the top region.
   *
   * @param content the node or other supported object to place in the top region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see BorderPane#setTop(javafx.scene.Node)
   */
  public BorderPaneBuilder top(Object content) {
    Objects.requireNonNull(content, "content");

    return applyContentStrategy(content, BorderPane::setTop);
  }

  /**
   * Sets the node for the bottom region.
   *
   * @param content the node or other supported object to place in the bottom region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see BorderPane#setBottom(javafx.scene.Node)
   */
  public BorderPaneBuilder bottom(Object content) {
    Objects.requireNonNull(content, "content");

    return applyContentStrategy(content, BorderPane::setBottom);
  }

  /**
   * Sets the node for the center region.
   *
   * @param content the node or other supported object to place in the center region, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see BorderPane#setCenter(javafx.scene.Node)
   */
  public BorderPaneBuilder center(Object content) {
    Objects.requireNonNull(content, "content");

    return applyContentStrategy(content, BorderPane::setCenter);
  }

  @Override
  public BorderPane build(BuildContext context) {
    return initialize(context, new BorderPane());
  }
}
