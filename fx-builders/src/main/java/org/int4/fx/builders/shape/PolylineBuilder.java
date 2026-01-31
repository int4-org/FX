package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Polyline} instances.
 */
public final class PolylineBuilder extends AbstractShapeBuilder<Polyline, PolylineBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public PolylineBuilder(String... styleClasses) {
    super(Polyline::new, styleClasses);
  }

  /**
   * Adds points to the polyline.
   *
   * @param points the X and Y coordinates in sequence; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Polyline#getPoints()
   */
  public PolylineBuilder points(double... points) {
    return apply(p -> {
      for(double pt : points) {
        p.getPoints().add(pt);
      }
    });
  }

  /**
   * Creates the {@link Polyline} with the given points and stroke.
   *
   * @param stroke the stroke paint
   * @param points the X and Y coordinates in sequence; the array cannot be {@code null}
   * @return the created {@link Polyline}, never {@code null}
   */
  public Polyline of(Paint stroke, double... points) {
    Polyline p = build();

    p.setStroke(stroke);

    for(double pt : points) {
      p.getPoints().add(pt);
    }

    return p;
  }
}