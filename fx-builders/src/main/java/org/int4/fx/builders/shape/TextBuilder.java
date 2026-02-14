package org.int4.fx.builders.shape;

import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Text} instances.
 */
public final class TextBuilder extends AbstractShapeBuilder<Text, TextBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public TextBuilder(String... styleClasses) {
    super(Text::new, styleClasses);
  }

  /**
   * Sets the content of the text.
   *
   * @param text the text content
   * @return the fluent builder, never {@code null}
   * @see Text#setText(String)
   */
  public TextBuilder text(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Binds the content of the text to the given observable value.
   *
   * @param value the text content
   * @return the fluent builder, never {@code null}
   * @see Text#textProperty()
   */
  public TextBuilder text(ObservableValue<String> value) {
    return apply(c -> c.textProperty().bind(value));
  }

  /**
   * Sets the X coordinate of the text.
   *
   * @param x the X coordinate
   * @return the fluent builder, never {@code null}
   * @see Text#setX(double)
   */
  public TextBuilder x(double x) {
    return apply(t -> t.setX(x));
  }

  /**
   * Sets the Y coordinate of the text.
   *
   * @param y the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see Text#setY(double)
   */
  public TextBuilder y(double y) {
    return apply(t -> t.setY(y));
  }

  /**
   * Sets both X and Y coordinates of the text.
   *
   * @param x the X coordinate
   * @param y the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see Text#setX(double)
   * @see Text#setY(double)
   */
  public TextBuilder position(double x, double y) {
    return apply(t -> {
      t.setX(x);
      t.setY(y);
    });
  }

  /**
   * Sets the font of the text.
   *
   * @param font the font to apply
   * @return the fluent builder, never {@code null}
   * @see Text#setFont(Font)
   */
  public TextBuilder font(Font font) {
    return apply(t -> t.setFont(font));
  }

  /**
   * Sets the wrapping width of the text.
   *
   * @param width the wrapping width
   * @return the fluent builder, never {@code null}
   * @see Text#setWrappingWidth(double)
   */
  public TextBuilder wrappingWidth(double width) {
    return apply(t -> t.setWrappingWidth(width));
  }

  /**
   * Creates the {@link Text} with the given content.
   *
   * @param text the text content
   * @return the created {@link Text}, never {@code null}
   */
  public Text of(String text) {
    Text t = build();

    t.setText(text);

    return t;
  }

  /**
   * Creates the {@link Text} with the given content, font, fill paint, and coordinates.
   *
   * @param text the text content
   * @param font the font to apply
   * @param fill the fill paint
   * @param x the X coordinate
   * @param y the Y coordinate
   * @return the created {@link Text}, never {@code null}
   */
  public Text of(String text, Font font, Paint fill, double x, double y) {
    Text t = build();

    t.setText(text);
    t.setFont(font);
    t.setFill(fill);
    t.setX(x);
    t.setY(y);

    return t;
  }
}