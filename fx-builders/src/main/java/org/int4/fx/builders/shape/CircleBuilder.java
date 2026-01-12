package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Circle} instances.
 */
public final class CircleBuilder extends AbstractShapeBuilder<Circle, CircleBuilder> {

  /**
   * Constructs a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public CircleBuilder(String... styleClasses) {
    super(Circle::new, styleClasses);
  }

  /**
   * Sets the center X coordinate of the circle.
   *
   * @param centerX the X coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Circle#setCenterX(double)
   */
  public CircleBuilder centerX(double centerX) {
    return apply(c -> c.setCenterX(centerX));
  }

  /**
   * Sets the center Y coordinate of the circle.
   *
   * @param centerY the Y coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Circle#setCenterY(double)
   */
  public CircleBuilder centerY(double centerY) {
    return apply(c -> c.setCenterY(centerY));
  }

  /**
   * Sets the radius of the circle.
   *
   * @param radius the radius of the circle, must be non-negative
   * @return the fluent builder, never {@code null}
   * @see Circle#setRadius(double)
   */
  public CircleBuilder radius(double radius) {
    return apply(c -> c.setRadius(radius));
  }

  /**
   * Sets both center X and Y coordinates of the circle.
   *
   * @param x the X coordinate of the center
   * @param y the Y coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Circle#setCenterX(double)
   * @see Circle#setCenterY(double)
   */
  public CircleBuilder center(double x, double y) {
    return apply(c -> {
      c.setCenterX(x);
      c.setCenterY(y);
    });
  }

  /**
   * Translates the circle by the given offsets.
   *
   * @param dx the offset in X direction
   * @param dy the offset in Y direction
   * @return the fluent builder, never {@code null}
   * @see Circle#setTranslateX(double)
   * @see Circle#setTranslateY(double)
   */
  public CircleBuilder translate(double dx, double dy) {
    return apply(c -> {
      c.setTranslateX(dx);
      c.setTranslateY(dy);
    });
  }

  /**
   * Creates the {@link Circle} with the given fill paint and radius.
   *
   * @param radius the radius of the circle
   * @param fill the fill paint
   * @return the created {@link Circle}, never {@code null}
   */
  public Circle of(double radius, Paint fill) {
    Circle c = build();

    c.setRadius(radius);
    c.setFill(fill);

    return c;
  }
}