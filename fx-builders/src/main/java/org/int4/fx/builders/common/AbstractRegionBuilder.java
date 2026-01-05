package org.int4.fx.builders.common;

import javafx.scene.layout.Region;

/**
 * Base class for builders that create JavaFX {@link Region} instances.
 * <p>
 * This builder provides convenience methods for configuring common layout
 * constraints such as minimum, preferred, and maximum sizes. All methods
 * operate fluently and defer configuration until the region is built.
 *
 * @param <T> the concrete {@link Region} type being built
 * @param <B> the concrete builder type (self type)
 */
public abstract class AbstractRegionBuilder<T extends Region, B extends AbstractRegionBuilder<T, B>> extends AbstractNodeBuilder<T, B> {

  /**
   * Creates a new region builder, initializing the resulting region with
   * the given style classes.
   *
   * @param styleClasses the style classes to apply, cannot be {@code null} but may be empty
   */
  protected AbstractRegionBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Sets the minimum width and height of the resulting region.
   *
   * @param w the minimum width
   * @param h the minimum height
   * @return the fluent builder, never {@code null}
   * @see Region#setMinSize(double, double)
   */
  public final B minSize(double w, double h) {
    return apply(c -> c.setMinSize(w, h));
  }

  /**
   * Sets the minimum width of the resulting region.
   *
   * @param w the minimum width
   * @return the fluent builder, never {@code null}
   * @see Region#setMinWidth(double)
   */
  public final B minWidth(double w) {
    return apply(c -> c.setMinWidth(w));
  }

  /**
   * Sets the minimum height of the resulting region.
   *
   * @param h the minimum height
   * @return the fluent builder, never {@code null}
   * @see Region#setMinHeight(double)
   */
  public final B minHeight(double h) {
    return apply(c -> c.setMinHeight(h));
  }

  /**
   * Sets the preferred width and height of the resulting region.
   *
   * @param w the preferred width
   * @param h the preferred height
   * @return the fluent builder, never {@code null}
   * @see Region#setPrefSize(double, double)
   */
  public final B prefSize(double w, double h) {
    return apply(c -> c.setPrefSize(w, h));
  }

  /**
   * Sets the preferred width of the resulting region.
   *
   * @param w the preferred width
   * @return the fluent builder, never {@code null}
   * @see Region#setPrefWidth(double)
   */
  public final B prefWidth(double w) {
    return apply(c -> c.setPrefWidth(w));
  }

  /**
   * Sets the preferred height of the resulting region.
   *
   * @param h the preferred height
   * @return the fluent builder, never {@code null}
   * @see Region#setPrefHeight(double)
   */
  public final B prefHeight(double h) {
    return apply(c -> c.setPrefHeight(h));
  }

  /**
   * Sets the maximum width and height of the resulting region.
   *
   * @param w the maximum width
   * @param h the maximum height
   * @return the fluent builder, never {@code null}
   * @see Region#setMaxSize(double, double)
   */
  public final B maxSize(double w, double h) {
    return apply(c -> c.setMaxSize(w, h));
  }

  /**
   * Sets the maximum width of the resulting region.
   *
   * @param w the maximum width
   * @return the fluent builder, never {@code null}
   * @see Region#setMaxWidth(double)
   */
  public final B maxWidth(double w) {
    return apply(c -> c.setMaxWidth(w));
  }

  /**
   * Sets the maximum height of the resulting region.
   *
   * @param h the maximum height
   * @return the fluent builder, never {@code null}
   * @see Region#setMaxHeight(double)
   */
  public final B maxHeight(double h) {
    return apply(c -> c.setMaxHeight(h));
  }
}
