package org.int4.fx.builders.control;

import java.time.LocalDate;

import javafx.scene.control.DatePicker;

import org.int4.fx.values.model.ValueModel;

/**
 * Builder for {@link DatePicker} instances.
 */
public final class DatePickerBuilder extends AbstractComboBoxBaseBuilder<LocalDate, DatePicker, DatePickerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public DatePickerBuilder(String... styleClasses) {
    super(DatePicker::new, styleClasses);
  }

  /**
   * Links the selected value to the given model and completes the builder.
   *
   * @param model the model to link to, cannot be {@code null}
   * @return the created {@link DatePicker}, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see DatePicker#valueProperty()
   */
  public DatePicker model(ValueModel<LocalDate> model) {
    DatePicker node = build();

    ModelLinker.link(
      node,
      model,
      node.valueProperty()
    );

    return node;
  }
}