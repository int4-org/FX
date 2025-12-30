package org.int4.fx.builders.control;

import javafx.scene.control.TextField;

/**
 * Builder for {@link TextField} instances.
 */
public final class TextFieldBuilder extends AbstractTextInputControlBuilder<TextField, TextFieldBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public TextFieldBuilder(String... styleClasses) {
    super(TextField::new, styleClasses);
  }
}