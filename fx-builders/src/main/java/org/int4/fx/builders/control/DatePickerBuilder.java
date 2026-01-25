package org.int4.fx.builders.control;

import java.time.LocalDate;
import java.util.Objects;

import javafx.beans.property.Property;
import javafx.scene.control.DatePicker;

import org.int4.fx.values.model.ValueModel;

/**
 * Builder for {@link DatePicker} instances.
 */
public final class DatePickerBuilder extends AbstractComboBoxBaseBuilder<DatePicker, DatePickerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public DatePickerBuilder(String... styleClasses) {
    super(DatePicker::new, styleClasses);
  }

  /**
   * Binds the selected value bidirectionally to a property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see DatePicker#valueProperty()
   */
  public final DatePickerBuilder value(Property<LocalDate> property) {
    Objects.requireNonNull(property, "property");

    return apply(c -> c.valueProperty().bindBidirectional(property));
  }

  /**
   * Links the selected value to the given model and completes the builder.
   *
   * @param model the model to link to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see DatePicker#valueProperty()
   */
  public DatePickerBuilder model(ValueModel<LocalDate> model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> ModelLinker.link(
      node,
      model,
      node.valueProperty()
    ));
  }
}