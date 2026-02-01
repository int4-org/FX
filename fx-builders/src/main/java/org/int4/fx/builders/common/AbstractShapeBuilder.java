package org.int4.fx.builders.common;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import org.int4.fx.builders.context.BuildContext;

/**
 * Base class for builders that create JavaFX {@link Shape} instances.
 *
 * @param <T> the concrete {@link Shape} type being built
 * @param <B> the concrete builder type (self type)
 */
public abstract class AbstractShapeBuilder<T extends Shape, B extends AbstractShapeBuilder<T, B>> extends AbstractNodeBuilder<T, B> {
  private final Supplier<T> instantiator;

  /**
   * Creates a new shape builder, initializing the resulting shape with
   * the given style classes.
   *
   * @param instantiator the shape instantiator, cannot be {@code null}
   * @param styleClasses the style classes to apply, cannot be {@code null} but may be empty
   */
  protected AbstractShapeBuilder(Supplier<T> instantiator, String... styleClasses) {
    super(styleClasses);

    this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
  }

  @Override
  public final T build(BuildContext context) {
    return initialize(context, instantiator.get());
  }

  /**
   * Sets the fill of the shape.
   *
   * @param paint the paint
   * @return the fluent builder, never {@code null}
   * @see Shape#setFill(Paint)
   */
  public final B fill(Paint paint) {
    return apply(c -> c.setFill(paint));
  }

  /**
   * Sets the stroke of the shape.
   *
   * @param paint the paint
   * @return the fluent builder, never {@code null}
   * @see Shape#setStroke(Paint)
   */
  public final B stroke(Paint paint) {
    return apply(c -> c.setStroke(paint));
  }

  /**
   * Replaces the stroke dash array of the shape.
   *
   * @param dashes the dash lengths
   * @return the fluent builder, never {@code null}
   * @see Shape#getStrokeDashArray()
   */
  public final B strokeDashArray(double... dashes) {
    return apply(c -> {
      c.getStrokeDashArray().clear();

      for (double d : dashes) {
        c.getStrokeDashArray().add(d);
      }
    });
  }

  /**
   * Sets the stroke dash offset of the shape.
   *
   * @param offset the dash offset
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeDashOffset(double)
   */
  public final B strokeDashOffset(double offset) {
    return apply(c -> c.setStrokeDashOffset(offset));
  }

  /**
   * Sets the stroke line cap of the shape.
   *
   * @param lineCap the line cap
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeLineCap(StrokeLineCap)
   */
  public final B strokeLineCap(StrokeLineCap lineCap) {
    return apply(c -> c.setStrokeLineCap(lineCap));
  }

  /**
   * Sets the stroke line join of the shape.
   *
   * @param lineJoin the line join
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeLineJoin(StrokeLineJoin)
   */
  public final B strokeLineJoin(StrokeLineJoin lineJoin) {
    return apply(c -> c.setStrokeLineJoin(lineJoin));
  }

  /**
   * Sets the stroke miter limit of the shape.
   *
   * @param miterLimit the miter limit
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeMiterLimit(double)
   */
  public final B strokeMiterLimit(double miterLimit) {
    return apply(c -> c.setStrokeMiterLimit(miterLimit));
  }

  /**
   * Sets the stroke type of the shape.
   *
   * @param type the stroke type
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeType(StrokeType)
   */
  public final B strokeType(StrokeType type) {
    return apply(c -> c.setStrokeType(type));
  }

  /**
   * Sets the stroke width of the shape.
   *
   * @param width the width
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeWidth(double)
   */
  public final B strokeWidth(double width) {
    return apply(c -> c.setStrokeWidth(width));
  }

  /**
   * Sets the shape to not smoothed.
   *
   * @return the fluent builder, never {@code null}
   * @see Shape#setSmooth(boolean)
   */
  public final B noSmooth() {
    return smooth(false);
  }

  /**
   * Sets whether the shape is smoothed.
   *
   * @param smooth whether smoothing is enabled
   * @return the fluent builder, never {@code null}
   * @see Shape#setSmooth(boolean)
   */
  public final B smooth(boolean smooth) {
    return apply(c -> c.setSmooth(smooth));
  }

  /**
   * Sets the stroke paint and stroke width of the shape.
   *
   * @param paint the stroke paint
   * @param width the stroke width
   * @return the fluent builder, never {@code null}
   * @see Shape#setStroke(Paint)
   * @see Shape#setStrokeWidth(double)
   */
  public final B stroke(Paint paint, double width) {
    return apply(c -> {
      c.setStroke(paint);
      c.setStrokeWidth(width);
    });
  }

  /**
   * Sets the stroke paint, stroke width, line cap, and line join of the shape.
   *
   * @param paint the stroke paint
   * @param width the stroke width
   * @param lineCap the stroke line cap
   * @param lineJoin the stroke line join
   * @return the fluent builder, never {@code null}
   * @see Shape#setStroke(Paint)
   * @see Shape#setStrokeWidth(double)
   * @see Shape#setStrokeLineCap(StrokeLineCap)
   * @see Shape#setStrokeLineJoin(StrokeLineJoin)
   */
  public final B stroke(Paint paint, double width, StrokeLineCap lineCap, StrokeLineJoin lineJoin) {
    return apply(c -> {
      c.setStroke(paint);
      c.setStrokeWidth(width);
      c.setStrokeLineCap(lineCap);
      c.setStrokeLineJoin(lineJoin);
    });
  }

  /**
   * Configures a dashed stroke using the given dash pattern and no offset.
   *
   * @param dashes the dash lengths
   * @return the fluent builder, never {@code null}
   * @see Shape#getStrokeDashArray()
   */
  public final B dashedStroke(double... dashes) {
    return dashedStroke(0.0, dashes);
  }

  /**
   * Configures a dashed stroke using the given dash pattern and offset.
   *
   * @param offset the dash offset
   * @param dashes the dash lengths
   * @return the fluent builder, never {@code null}
   * @see Shape#setStrokeDashOffset(double)
   * @see Shape#getStrokeDashArray()
   */
  public final B dashedStroke(double offset, double... dashes) {
    return apply(c -> {
      c.setStrokeDashOffset(offset);
      c.getStrokeDashArray().clear();

      for (double d : dashes) {
        c.getStrokeDashArray().add(d);
      }
    });
  }

  /**
   * Clears the fill of the shape.
   *
   * @return the fluent builder, never {@code null}
   * @see Shape#setFill(Paint)
   */
  public final B noFill() {
    return apply(c -> c.setFill(null));
  }
}
