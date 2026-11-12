package org.int4.fx.builders;

import org.int4.fx.builders.shape.ArcBuilder;
import org.int4.fx.builders.shape.CircleBuilder;
import org.int4.fx.builders.shape.CubicCurveBuilder;
import org.int4.fx.builders.shape.EllipseBuilder;
import org.int4.fx.builders.shape.LineBuilder;
import org.int4.fx.builders.shape.PathBuilder;
import org.int4.fx.builders.shape.PolygonBuilder;
import org.int4.fx.builders.shape.PolylineBuilder;
import org.int4.fx.builders.shape.QuadCurveBuilder;
import org.int4.fx.builders.shape.RectangleBuilder;
import org.int4.fx.builders.shape.SVGPathBuilder;
import org.int4.fx.builders.shape.TextBuilder;

/**
 * Builder group for JavaFX shapes.
 * <p>
 * This is a mixin interface used to compose custom FX entry points. The
 * standard all-in-one factory is {@link CoreFX#FX}, and the shapes-only
 * instance is {@link #SHAPES}. See {@link CoreFX} for shared builder
 * conventions and build-context behavior.
 */
public interface Shapes {

  /**
   * The shapes-only builder factory instance.
   */
  Shapes SHAPES = new Shapes() {};

  /**
   * Creates a builder for an {@link javafx.scene.shape.Arc}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Arc
   */
  default ArcBuilder arc(String... styleClasses) {
    return new ArcBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.Circle}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Circle
   */
  default CircleBuilder circle(String... styleClasses) {
    return new CircleBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.CubicCurve}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.CubicCurve
   */
  default CubicCurveBuilder cubicCurve(String... styleClasses) {
    return new CubicCurveBuilder(styleClasses);
  }

  /**
   * Creates a builder for an {@link javafx.scene.shape.Ellipse}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Ellipse
   */
  default EllipseBuilder ellipse(String... styleClasses) {
    return new EllipseBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.Line}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Line
   */
  default LineBuilder line(String... styleClasses) {
    return new LineBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.Path}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Path
   */
  default PathBuilder path(String... styleClasses) {
    return new PathBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.Polygon}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Polygon
   */
  default PolygonBuilder polygon(String... styleClasses) {
    return new PolygonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.Polyline}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Polyline
   */
  default PolylineBuilder polyline(String... styleClasses) {
    return new PolylineBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.QuadCurve}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.QuadCurve
   */
  default QuadCurveBuilder quadCurve(String... styleClasses) {
    return new QuadCurveBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.shape.Rectangle}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Rectangle
   */
  default RectangleBuilder rectangle(String... styleClasses) {
    return new RectangleBuilder(styleClasses);
  }

  /**
   * Creates a builder for an {@link javafx.scene.shape.SVGPath}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.SVGPath
   */
  default SVGPathBuilder svgPath(String... styleClasses) {
    return new SVGPathBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.text.Text}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.text.Text
   */
  default TextBuilder text(String... styleClasses) {
    return new TextBuilder(styleClasses);
  }
}
