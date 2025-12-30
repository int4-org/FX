package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.common.NodeEventHandler;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link Button} instances.
 * <p>
 * Provides a fluent API to configure the button’s text, graphic, style, and event handlers.
 * All methods return the builder itself for chaining.
 */
public final class ButtonBuilder extends AbstractControlBuilder<Button, ButtonBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public ButtonBuilder(String... styleClasses) {
    super(Button::new, styleClasses);
  }

  /**
   * Marks this button as the default button.
   *
   * @return the fluent builder, never {@code null}
   * @see Button#setDefaultButton(boolean)
   */
  public ButtonBuilder defaultButton() {
    return apply(c -> c.setDefaultButton(true));
  }

  /**
   * Sets the text of the button.
   *
   * @param text the button text, may be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Button#setText(String)
   */
  public ButtonBuilder text(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Binds the button text to the given observable.
   *
   * @param text an {@link ObservableValue} providing the text, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Button#textProperty()
   */
  public ButtonBuilder text(ObservableValue<String> text) {
    return apply(c -> c.textProperty().bind(text));
  }

  /**
   * Sets the graphic of the button.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see Button#setGraphic(javafx.scene.Node)
   */
  public ButtonBuilder graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return apply(c -> c.setGraphic(Builders.toNode(graphic)));
  }

  /**
   * Sets an {@link EventHandler} for {@link ActionEvent}s.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Button#setOnAction(EventHandler)
   */
  public ButtonBuilder onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }

  /**
   * Sets a {@link NodeEventHandler} for {@link ActionEvent}s, which receives
   * both the button instance and the event.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Button#setOnAction(EventHandler)
   */
  public ButtonBuilder onAction(NodeEventHandler<Button, ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(e -> eventHandler.handle(c, e)));
  }
}
