package org.int4.fx.builders.shape;

import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link SVGPath} instances.
 */
public final class SVGPathBuilder extends AbstractShapeBuilder<SVGPath, SVGPathBuilder> {

  /**
   * Constructs a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public SVGPathBuilder(String... styleClasses) {
    super(SVGPath::new, styleClasses);
  }

  /**
   * Sets the content of the SVG path.
   *
   * @param path the path to set as content
   * @return the fluent builder, never {@code null}
   * @see SVGPath#setContent(String)
   */
  public SVGPathBuilder content(String path) {
    return apply(c -> c.setContent(path));
  }

  /**
   * Sets the fill rule of the {@link SVGPath}.
   *
   * @param fillRule the fill rule to apply
   * @return the fluent builder, never {@code null}
   * @see SVGPath#setFillRule(FillRule)
   */
  public SVGPathBuilder fillRule(FillRule fillRule) {
    return apply(c -> c.setFillRule(fillRule));
  }

  /**
   * Creates the {@link SVGPath} with the given path.
   *
   * @param path the path to set as content
   * @return the created {@link SVGPath}, never {@code null}
   */
  public SVGPath of(String path) {
    SVGPath node = build();

    node.setContent(path);

    return node;
  }
}