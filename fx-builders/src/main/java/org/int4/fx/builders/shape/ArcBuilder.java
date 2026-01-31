package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Arc} instances.
 */
public final class ArcBuilder extends AbstractShapeBuilder<Arc, ArcBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ArcBuilder(String... styleClasses) {
    super(Arc::new, styleClasses);
  }

  /**
   * Sets the center coordinates of the arc.
   *
   * @param centerX the X coordinate of the center
   * @param centerY the Y coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Arc#setCenterX(double)
   * @see Arc#setCenterY(double)
   */
  public ArcBuilder center(double centerX, double centerY) {
    return apply(a -> {
      a.setCenterX(centerX);
      a.setCenterY(centerY);
    });
  }

  /**
   * Sets the radii of the arc.
   *
   * @param radiusX the horizontal radius
   * @param radiusY the vertical radius
   * @return the fluent builder, never {@code null}
   * @see Arc#setRadiusX(double)
   * @see Arc#setRadiusY(double)
   */
  public ArcBuilder radius(double radiusX, double radiusY) {
    return apply(a -> {
      a.setRadiusX(radiusX);
      a.setRadiusY(radiusY);
    });
  }

  /**
   * Sets the start angle of the arc.
   *
   * @param startAngle the start angle in degrees
   * @return the fluent builder, never {@code null}
   * @see Arc#setStartAngle(double)
   */
  public ArcBuilder startAngle(double startAngle) {
    return apply(a -> a.setStartAngle(startAngle));
  }

  /**
   * Sets the length of the arc.
   *
   * @param length the angular length of the arc in degrees
   * @return the fluent builder, never {@code null}
   * @see Arc#setLength(double)
   */
  public ArcBuilder length(double length) {
    return apply(a -> a.setLength(length));
  }

  /**
   * Sets the type of the arc.
   *
   * @param type the {@link ArcType} of the arc
   * @return the fluent builder, never {@code null}
   * @see Arc#setType(ArcType)
   */
  public ArcBuilder type(ArcType type) {
    return apply(a -> a.setType(type));
  }

  /**
   * Creates the {@link Arc} with the given radius, start angle, length, and fill.
   *
   * @param radiusX the horizontal radius
   * @param radiusY the vertical radius
   * @param startAngle the start angle in degrees
   * @param length the angular length of the arc in degrees
   * @param fill the fill paint
   * @return the created {@link Arc}, never {@code null}
   */
  public Arc of(double radiusX, double radiusY, double startAngle, double length, Paint fill) {
    Arc a = build();

    a.setRadiusX(radiusX);
    a.setRadiusY(radiusY);
    a.setStartAngle(startAngle);
    a.setLength(length);
    a.setFill(fill);

    return a;
  }
}