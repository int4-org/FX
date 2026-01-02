package org.int4.fx.builders.control;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import org.int4.fx.values.model.ObjectModel;

/**
 * Builder for {@link ColorPicker} instances.
 */
public final class ColorPickerBuilder extends AbstractComboBoxBaseBuilder<Color, ColorPicker, ColorPickerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public ColorPickerBuilder(String... styleClasses) {
    super(ColorPicker::new, styleClasses);
  }

  /**
   * Links the selected value to the given model and completes the builder.
   *
   * @param model the model to link to, cannot be {@code null}
   * @return the created {@link ColorPicker}, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ColorPicker#valueProperty()
   */
  public ColorPicker model(ObjectModel<Color> model) {
    ColorPicker node = build();

    ModelLinker.link(
      node,
      model,
      node.valueProperty()
    );

    return node;
  }
}