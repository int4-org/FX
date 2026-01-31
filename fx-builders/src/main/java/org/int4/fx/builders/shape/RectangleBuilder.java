package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Rectangle} instances.
 */
public final class RectangleBuilder extends AbstractShapeBuilder<Rectangle, RectangleBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public RectangleBuilder(String... styleClasses) {
    super(Rectangle::new, styleClasses);
  }

  /**
   * Sets the X coordinate of the rectangle.
   *
   * @param x the X coordinate
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setX(double)
   */
  public RectangleBuilder x(double x) {
    return apply(r -> r.setX(x));
  }

  /**
   * Sets the Y coordinate of the rectangle.
   *
   * @param y the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setY(double)
   */
  public RectangleBuilder y(double y) {
    return apply(r -> r.setY(y));
  }

  /**
   * Sets both X and Y coordinates of the rectangle.
   *
   * @param x the X coordinate
   * @param y the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setX(double)
   * @see Rectangle#setY(double)
   */
  public RectangleBuilder position(double x, double y) {
    return apply(r -> {
      r.setX(x);
      r.setY(y);
    });
  }

  /**
   * Sets the width of the rectangle.
   *
   * @param width the width, must be non-negative
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setWidth(double)
   */
  public RectangleBuilder width(double width) {
    return apply(r -> r.setWidth(width));
  }

  /**
   * Sets the height of the rectangle.
   *
   * @param height the height, must be non-negative
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setHeight(double)
   */
  public RectangleBuilder height(double height) {
    return apply(r -> r.setHeight(height));
  }

  /**
   * Sets both width and height of the rectangle.
   *
   * @param width the width, must be non-negative
   * @param height the height, must be non-negative
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setWidth(double)
   * @see Rectangle#setHeight(double)
   */
  public RectangleBuilder size(double width, double height) {
    return apply(r -> {
      r.setWidth(width);
      r.setHeight(height);
    });
  }

  /**
   * Sets the arc width for rounded corners.
   *
   * @param arcWidth the arc width
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setArcWidth(double)
   */
  public RectangleBuilder arcWidth(double arcWidth) {
    return apply(r -> r.setArcWidth(arcWidth));
  }

  /**
   * Sets the arc height for rounded corners.
   *
   * @param arcHeight the arc height
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setArcHeight(double)
   */
  public RectangleBuilder arcHeight(double arcHeight) {
    return apply(r -> r.setArcHeight(arcHeight));
  }

  /**
   * Sets both arc width and height for rounded corners.
   *
   * @param arcWidth the arc width
   * @param arcHeight the arc height
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setArcWidth(double)
   * @see Rectangle#setArcHeight(double)
   */
  public RectangleBuilder arc(double arcWidth, double arcHeight) {
    return apply(r -> {
      r.setArcWidth(arcWidth);
      r.setArcHeight(arcHeight);
    });
  }

  /**
   * Translates the rectangle by the given offsets.
   *
   * @param dx the offset in X direction
   * @param dy the offset in Y direction
   * @return the fluent builder, never {@code null}
   * @see Rectangle#setTranslateX(double)
   * @see Rectangle#setTranslateY(double)
   */
  public RectangleBuilder translate(double dx, double dy) {
    return apply(r -> {
      r.setTranslateX(dx);
      r.setTranslateY(dy);
    });
  }

  /**
   * Creates the {@link Rectangle} with the given width, height, and fill paint.
   *
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param fill the fill paint
   * @return the created {@link Rectangle}, never {@code null}
   */
  public Rectangle of(double width, double height, Paint fill) {
    Rectangle r = build();

    r.setWidth(width);
    r.setHeight(height);
    r.setFill(fill);

    return r;
  }
}