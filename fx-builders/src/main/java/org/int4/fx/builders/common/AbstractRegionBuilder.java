package org.int4.fx.builders.common;

import javafx.scene.layout.Region;

/**
 * Base class for builders that create JavaFX {@link Region} instances that can
 * be shown or hidden.
 * <p>
 * On top of the size configuration provided by {@link AbstractRegionBaseBuilder},
 * this class implements {@link Visibility}, binding the region's visible and
 * managed state to an observable condition.
 * <p>
 * Ordinary region, pane, and control builders extend this class. Builders that
 * should not expose visibility extend {@link AbstractRegionBaseBuilder}
 * directly instead.
 *
 * @param <T> the concrete {@link Region} type being built
 * @param <B> the concrete builder type (self type)
 */
public abstract class AbstractRegionBuilder<T extends Region, B extends AbstractRegionBuilder<T, B>>
  extends AbstractRegionBaseBuilder<T, B>
  implements Visibility<T, B>
{

  /**
   * Creates a new region builder, initializing the resulting region with
   * the given style classes.
   *
   * @param styleClasses the style classes to apply, cannot be {@code null} but may be empty
   */
  protected AbstractRegionBuilder(String... styleClasses) {
    super(styleClasses);
  }
}
