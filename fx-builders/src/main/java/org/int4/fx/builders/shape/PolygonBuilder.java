package org.int4.fx.builders.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Polygon} instances.
 */
public final class PolygonBuilder extends AbstractShapeBuilder<Polygon, PolygonBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public PolygonBuilder(String... styleClasses) {
    super(Polygon::new, styleClasses);
  }

  /**
   * Adds points to the polygon.
   *
   * @param points the X and Y coordinates in sequence; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Polygon#getPoints()
   */
  public PolygonBuilder points(double... points) {
    return apply(p -> {
      for(double pt : points) {
        p.getPoints().add(pt);
      }
    });
  }

  /**
   * Creates the {@link Polygon} with the given points and fill.
   *
   * @param fill the fill paint
   * @param points the X and Y coordinates in sequence; the array cannot be {@code null}
   * @return the created {@link Polygon}, never {@code null}
   */
  public Polygon of(Paint fill, double... points) {
    Polygon p = build();

    p.setFill(fill);

    for(double pt : points) {
      p.getPoints().add(pt);
    }

    return p;
  }
}