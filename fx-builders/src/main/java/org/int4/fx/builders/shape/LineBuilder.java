package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Line} instances.
 */
public final class LineBuilder extends AbstractShapeBuilder<Line, LineBuilder> {

  /**
   * Constructs a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public LineBuilder(String... styleClasses) {
    super(Line::new, styleClasses);
  }

  /**
   * Sets the start coordinates of the line.
   *
   * @param startX the X coordinate of the start point
   * @param startY the Y coordinate of the start point
   * @return the fluent builder, never {@code null}
   * @see Line#setStartX(double)
   * @see Line#setStartY(double)
   */
  public LineBuilder start(double startX, double startY) {
    return apply(l -> {
      l.setStartX(startX);
      l.setStartY(startY);
    });
  }

  /**
   * Sets the end coordinates of the line.
   *
   * @param endX the X coordinate of the end point
   * @param endY the Y coordinate of the end point
   * @return the fluent builder, never {@code null}
   * @see Line#setEndX(double)
   * @see Line#setEndY(double)
   */
  public LineBuilder end(double endX, double endY) {
    return apply(l -> {
      l.setEndX(endX);
      l.setEndY(endY);
    });
  }

  /**
   * Creates the {@link Line} with the given start and end coordinates and stroke.
   *
   * @param startX the X coordinate of the start point
   * @param startY the Y coordinate of the start point
   * @param endX the X coordinate of the end point
   * @param endY the Y coordinate of the end point
   * @param stroke the stroke paint
   * @return the created {@link Line}, never {@code null}
   */
  public Line of(double startX, double startY, double endX, double endY, Paint stroke) {
    Line l = build();

    l.setStartX(startX);
    l.setStartY(startY);
    l.setEndX(endX);
    l.setEndY(endY);
    l.setStroke(stroke);

    return l;
  }
}