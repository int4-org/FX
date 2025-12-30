package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link ToggleButton} instances.
 */
public final class ToggleButtonBuilder extends AbstractControlBuilder<ToggleButton, ToggleButtonBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public ToggleButtonBuilder(String... styleClasses) {
    super(ToggleButton::new, styleClasses);
  }

  /**
   * Sets the text of the toggle button.
   *
   * @param text the text
   * @return the fluent builder, never {@code null}
   * @see ToggleButton#setText(String)
   */
  public ToggleButtonBuilder text(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Sets the graphic of the toggle button.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see ToggleButton#setGraphic(javafx.scene.Node)
   */
  public ToggleButtonBuilder graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return apply(c -> c.setGraphic(Builders.toNode(graphic)));
  }

  /**
   * Sets the action handler of the toggle button.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see ToggleButton#setOnAction(EventHandler)
   */
  public ToggleButtonBuilder onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }

  /**
   * Marks the toggle button as selected.
   *
   * @return the fluent builder, never {@code null}
   * @see ToggleButton#selectedProperty()
   */
  public ToggleButtonBuilder selected() {
    return apply(c -> c.setSelected(true));
  }

  /**
   * Assigns the toggle button to a toggle group.
   *
   * @param group the toggle group, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see ToggleButton#setToggleGroup(ToggleGroup)
   */
  public ToggleButtonBuilder toggleGroup(ToggleGroup group) {
    return apply(c -> c.setToggleGroup(group));
  }
}