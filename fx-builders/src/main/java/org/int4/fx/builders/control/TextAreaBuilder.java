package org.int4.fx.builders.control;

import javafx.scene.control.TextArea;

/**
 * Builder for {@link TextArea} instances.
 */
public final class TextAreaBuilder extends AbstractTextInputControlBuilder<TextArea, TextAreaBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public TextAreaBuilder(String... styleClasses) {
    super(TextArea::new, styleClasses);
  }

  /**
   * Enables text wrapping for the text area.
   *
   * @return the fluent builder, never {@code null}
   * @see TextArea#setWrapText(boolean)
   */
  public TextAreaBuilder wrapText() {
    return apply(c -> c.setWrapText(true));
  }
}