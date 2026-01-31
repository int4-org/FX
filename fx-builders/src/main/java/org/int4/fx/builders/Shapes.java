package org.int4.fx.builders;

import org.int4.fx.builders.common.NodeBuilder;
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
 * Entry point for creating JavaFX shape builders.
 * <p>
 * Builders produced by this class are typically used to declaratively construct
 * JavaFX scene graphs. When builders are passed as children to other builders,
 * there is no need to explicitly call {@link NodeBuilder#build()}:
 * builders are finalized automatically where required.
 * <p>
 * Child values are interpreted as follows:
 * <ul>
 *   <li>A builder is built and converted into a {@link javafx.scene.Node}.</li>
 *   <li>A {@link javafx.scene.Node} is accepted directly.</li>
 *   <li>A {@link String} is converted into a {@link javafx.scene.control.Label}.</li>
 *   <li>Any other object results in a placeholder node indicating an unsupported type.</li>
 * </ul>
 * <p>
 * Builder methods are named to match the JavaFX properties they manipulate.
 * For boolean properties, builders typically provide both positive and negative
 * convenience methods without parameters, in addition to the standard setter-style
 * method. Builders may also offer higher-level convenience methods that configure
 * multiple commonly used properties together.
 */
public class Shapes {

  /**
   * Creates a builder for an {@link javafx.scene.shape.Arc}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.shape.Arc
   */
  public static ArcBuilder arc(String... styleClasses) {
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
  public static CircleBuilder circle(String... styleClasses) {
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
  public static CubicCurveBuilder cubicCurve(String... styleClasses) {
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
  public static EllipseBuilder ellipse(String... styleClasses) {
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
  public static LineBuilder line(String... styleClasses) {
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
  public static PathBuilder path(String... styleClasses) {
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
  public static PolygonBuilder polygon(String... styleClasses) {
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
  public static PolylineBuilder polyline(String... styleClasses) {
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
  public static QuadCurveBuilder quadCurve(String... styleClasses) {
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
  public static RectangleBuilder rectangle(String... styleClasses) {
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
  public static SVGPathBuilder svgPath(String... styleClasses) {
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
  public static TextBuilder text(String... styleClasses) {
    return new TextBuilder(styleClasses);
  }

  private Shapes() {}
}
