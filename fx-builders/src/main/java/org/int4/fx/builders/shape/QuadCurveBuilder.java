package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.QuadCurve;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link QuadCurve} instances.
 */
public final class QuadCurveBuilder extends AbstractShapeBuilder<QuadCurve, QuadCurveBuilder> {

  /**
   * Constructs a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public QuadCurveBuilder(String... styleClasses) {
    super(QuadCurve::new, styleClasses);
  }

  /**
   * Sets the start point of the curve.
   *
   * @param startX the X coordinate
   * @param startY the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see QuadCurve#setStartX(double)
   * @see QuadCurve#setStartY(double)
   */
  public QuadCurveBuilder start(double startX, double startY) {
    return apply(q -> {
      q.setStartX(startX);
      q.setStartY(startY);
    });
  }

  /**
   * Sets the end point of the curve.
   *
   * @param endX the X coordinate
   * @param endY the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see QuadCurve#setEndX(double)
   * @see QuadCurve#setEndY(double)
   */
  public QuadCurveBuilder end(double endX, double endY) {
    return apply(q -> {
      q.setEndX(endX);
      q.setEndY(endY);
    });
  }

  /**
   * Sets the control point of the curve.
   *
   * @param controlX the X coordinate
   * @param controlY the Y coordinate
   * @return the fluent builder, never {@code null}
   * @see QuadCurve#setControlX(double)
   * @see QuadCurve#setControlY(double)
   */
  public QuadCurveBuilder control(double controlX, double controlY) {
    return apply(q -> {
      q.setControlX(controlX);
      q.setControlY(controlY);
    });
  }

  /**
   * Creates the {@link QuadCurve} with the given points and stroke.
   *
   * @param startX the X coordinate of the start point
   * @param startY the Y coordinate of the start point
   * @param controlX the X coordinate of the control point
   * @param controlY the Y coordinate of the control point
   * @param endX the X coordinate of the end point
   * @param endY the Y coordinate of the end point
   * @param stroke the stroke paint
   * @return the created {@link QuadCurve}, never {@code null}
   */
  public QuadCurve of(double startX, double startY, double controlX, double controlY, double endX, double endY, Paint stroke) {
    QuadCurve q = build();

    q.setStartX(startX);
    q.setStartY(startY);
    q.setControlX(controlX);
    q.setControlY(controlY);
    q.setEndX(endX);
    q.setEndY(endY);
    q.setStroke(stroke);

    return q;
  }
}