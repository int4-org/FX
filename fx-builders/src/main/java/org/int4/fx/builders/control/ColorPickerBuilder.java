package org.int4.fx.builders.control;

import javafx.scene.control.ColorPicker;

import org.int4.fx.builders.common.AbstractControlBuilder;

/**
 * Builder for {@link ColorPicker} instances.
 */
public final class ColorPickerBuilder extends AbstractControlBuilder<ColorPicker, ColorPickerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public ColorPickerBuilder(String... styleClasses) {
    super(ColorPicker::new, styleClasses);
  }

}