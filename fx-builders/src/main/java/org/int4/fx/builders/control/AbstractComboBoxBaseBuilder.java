package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBoxBase;

import org.int4.fx.builders.common.AbstractControlBuilder;

/**
 * Base builder for {@link ComboBoxBase} controls.
 *
 * @param <T> the item type
 * @param <C> the concrete {@link ComboBoxBase} type
 * @param <B> the concrete builder type
 */
public abstract class AbstractComboBoxBaseBuilder<T, C extends ComboBoxBase<T>, B extends AbstractComboBoxBaseBuilder<T, C, B>> extends AbstractControlBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code instantiator} or {@code styleClasses} is {@code null}
   */
  protected AbstractComboBoxBaseBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets the prompt text displayed when no color is selected.
   *
   * @param text the prompt text
   * @return the fluent builder, never {@code null}
   * @see ColorPicker#setPromptText(String)
   */
  public final B promptText(String text) {
    return apply(c -> c.setPromptText(text));
  }

  /**
   * Sets the action handler invoked when the selected color changes.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see ColorPicker#setOnAction(EventHandler)
   */
  public final B onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }

  /**
   * Binds the selected value bidirectionally to a property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see ComboBoxBase#valueProperty()
   */
  public final B value(Property<T> property) {
    Objects.requireNonNull(property, "property");

    return apply(c -> c.valueProperty().bindBidirectional(property));
  }
}