package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Ellipse} instances.
 */
public final class EllipseBuilder extends AbstractShapeBuilder<Ellipse, EllipseBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public EllipseBuilder(String... styleClasses) {
    super(Ellipse::new, styleClasses);
  }

  /**
   * Sets the center X coordinate of the ellipse.
   *
   * @param centerX the X coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setCenterX(double)
   */
  public EllipseBuilder centerX(double centerX) {
    return apply(e -> e.setCenterX(centerX));
  }

  /**
   * Sets the center Y coordinate of the ellipse.
   *
   * @param centerY the Y coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setCenterY(double)
   */
  public EllipseBuilder centerY(double centerY) {
    return apply(e -> e.setCenterY(centerY));
  }

  /**
   * Sets both the center X and Y coordinates of the ellipse.
   *
   * @param x the X coordinate of the center
   * @param y the Y coordinate of the center
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setCenterX(double)
   * @see Ellipse#setCenterY(double)
   */
  public EllipseBuilder center(double x, double y) {
    return apply(e -> {
      e.setCenterX(x);
      e.setCenterY(y);
    });
  }

  /**
   * Sets the horizontal radius of the ellipse.
   *
   * @param radiusX the horizontal radius
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setRadiusX(double)
   */
  public EllipseBuilder radiusX(double radiusX) {
    return apply(e -> e.setRadiusX(radiusX));
  }

  /**
   * Sets the vertical radius of the ellipse.
   *
   * @param radiusY the vertical radius
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setRadiusY(double)
   */
  public EllipseBuilder radiusY(double radiusY) {
    return apply(e -> e.setRadiusY(radiusY));
  }

  /**
   * Sets both horizontal and vertical radii of the ellipse.
   *
   * @param radiusX the horizontal radius
   * @param radiusY the vertical radius
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setRadiusX(double)
   * @see Ellipse#setRadiusY(double)
   */
  public EllipseBuilder radius(double radiusX, double radiusY) {
    return apply(e -> {
      e.setRadiusX(radiusX);
      e.setRadiusY(radiusY);
    });
  }

  /**
   * Translates the ellipse by the given offsets.
   *
   * @param dx the offset in X direction
   * @param dy the offset in Y direction
   * @return the fluent builder, never {@code null}
   * @see Ellipse#setTranslateX(double)
   * @see Ellipse#setTranslateY(double)
   */
  public EllipseBuilder translate(double dx, double dy) {
    return apply(e -> {
      e.setTranslateX(dx);
      e.setTranslateY(dy);
    });
  }

  /**
   * Creates the {@link Ellipse} with the given horizontal and vertical radii
   * and fill paint.
   *
   * @param radiusX the horizontal radius
   * @param radiusY the vertical radius
   * @param fill the fill paint
   * @return the created {@link Ellipse}, never {@code null}
   */
  public Ellipse of(double radiusX, double radiusY, Paint fill) {
    Ellipse e = build();

    e.setRadiusX(radiusX);
    e.setRadiusY(radiusY);
    e.setFill(fill);

    return e;
  }
}