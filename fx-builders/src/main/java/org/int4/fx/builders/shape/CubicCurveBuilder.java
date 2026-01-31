package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurve;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link CubicCurve} instances.
 */
public final class CubicCurveBuilder extends AbstractShapeBuilder<CubicCurve, CubicCurveBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public CubicCurveBuilder(String... styleClasses) {
    super(CubicCurve::new, styleClasses);
  }

  /**
   * Sets the start point of the curve.
   *
   * @param startX the X coordinate
   * @param startY the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see CubicCurve#setStartX(double)
   * @see CubicCurve#setStartY(double)
   */
  public CubicCurveBuilder start(double startX, double startY) {
    return apply(c -> {
      c.setStartX(startX);
      c.setStartY(startY);
    });
  }

  /**
   * Sets the end point of the curve.
   *
   * @param endX the X coordinate
   * @param endY the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see CubicCurve#setEndX(double)
   * @see CubicCurve#setEndY(double)
   */
  public CubicCurveBuilder end(double endX, double endY) {
    return apply(c -> {
      c.setEndX(endX);
      c.setEndY(endY);
    });
  }

  /**
   * Sets the first control point of the curve.
   *
   * @param controlX1 the X coordinate of the first control point
   * @param controlY1 the Y coordinate of the first control point
   * @return the fluent builder, never {@code null}
   * @see CubicCurve#setControlX1(double)
   * @see CubicCurve#setControlY1(double)
   */
  public CubicCurveBuilder control1(double controlX1, double controlY1) {
    return apply(c -> {
      c.setControlX1(controlX1);
      c.setControlY1(controlY1);
    });
  }

  /**
   * Sets the second control point of the curve.
   *
   * @param controlX2 the X coordinate of the second control point
   * @param controlY2 the Y coordinate of the second control point
   * @return the fluent builder, never {@code null}
   * @see CubicCurve#setControlX2(double)
   * @see CubicCurve#setControlY2(double)
   */
  public CubicCurveBuilder control2(double controlX2, double controlY2) {
    return apply(c -> {
      c.setControlX2(controlX2);
      c.setControlY2(controlY2);
    });
  }

  /**
   * Creates the {@link CubicCurve} with the given start, end, and control points, and stroke.
   *
   * @param startX the X coordinate of the start point
   * @param startY the Y coordinate of the start point
   * @param controlX1 the X coordinate of the first control point
   * @param controlY1 the Y coordinate of the first control point
   * @param controlX2 the X coordinate of the second control point
   * @param controlY2 the Y coordinate of the second control point
   * @param endX the X coordinate of the end point
   * @param endY the Y coordinate of the end point
   * @param stroke the stroke paint
   * @return the created {@link CubicCurve}, never {@code null}
   */
  public CubicCurve of(double startX, double startY,
    double controlX1, double controlY1,
    double controlX2, double controlY2,
    double endX, double endY,
    Paint stroke
  ) {
    CubicCurve c = build();

    c.setStartX(startX);
    c.setStartY(startY);
    c.setControlX1(controlX1);
    c.setControlY1(controlY1);
    c.setControlX2(controlX2);
    c.setControlY2(controlY2);
    c.setEndX(endX);
    c.setEndY(endY);
    c.setStroke(stroke);

    return c;
  }
}