package org.int4.fx.builders.control;

import java.time.LocalDate;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.values.model.ObjectModel;

/**
 * Builder for {@link DatePicker} instances.
 */
public final class DatePickerBuilder extends AbstractControlBuilder<DatePicker, DatePickerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public DatePickerBuilder(String... styleClasses) {
    super(DatePicker::new, styleClasses);
  }

  /**
   * Sets the prompt text displayed when no date is selected.
   *
   * @param text the prompt text
   * @return the fluent builder, never {@code null}
   * @see DatePicker#setPromptText(String)
   */
  public DatePickerBuilder promptText(String text) {
    return apply(c -> c.setPromptText(text));
  }

  /**
   * Sets the action handler invoked when the selected date changes.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see DatePicker#setOnAction(EventHandler)
   */
  public DatePickerBuilder onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }

  /**
   * Binds the selected value bidirectionally to the given property and
   * completes the builder.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the created {@link DatePicker}, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see DatePicker#valueProperty()
   */
  public DatePicker value(ObjectProperty<LocalDate> property) {
    DatePicker node = build();

    node.valueProperty().bindBidirectional(property);

    return node;
  }

  /**
   * Links the selected value to the given model and completes the builder.
   *
   * @param model the model to link to, cannot be {@code null}
   * @return the created {@link DatePicker}, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see DatePicker#valueProperty()
   */
  public DatePicker value(ObjectModel<LocalDate> model) {
    DatePicker node = build();

    ModelLinker.link(
      node,
      model,
      node.valueProperty()
    );

    return node;
  }
}