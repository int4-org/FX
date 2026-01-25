package org.int4.fx.builders.control;

import javafx.scene.control.Button;

/**
 * Builder for {@link Button} instances.
 * <p>
 * Provides a fluent API to configure the button's text, graphic, style, and event handlers.
 * All methods return the builder itself for chaining.
 */
public final class ButtonBuilder extends AbstractButtonBaseBuilder<Button, ButtonBuilder> {

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
}
