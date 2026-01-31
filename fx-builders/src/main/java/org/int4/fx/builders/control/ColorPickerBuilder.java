package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.beans.property.Property;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import org.int4.fx.values.model.ValueModel;

/**
 * Builder for {@link ColorPicker} instances.
 */
public final class ColorPickerBuilder extends AbstractComboBoxBaseBuilder<ColorPicker, ColorPickerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ColorPickerBuilder(String... styleClasses) {
    super(ColorPicker::new, styleClasses);
  }

  /**
   * Binds the selected value bidirectionally to a property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see ColorPicker#valueProperty()
   */
  public final ColorPickerBuilder value(Property<Color> property) {
    Objects.requireNonNull(property, "property");

    return apply(c -> c.valueProperty().bindBidirectional(property));
  }

  /**
   * Links the selected value to the given model and completes the builder.
   *
   * @param model the model to link to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ColorPicker#valueProperty()
   */
  public ColorPickerBuilder model(ValueModel<Color> model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> ModelLinker.link(
      node,
      model,
      node.valueProperty()
    ));
  }
}