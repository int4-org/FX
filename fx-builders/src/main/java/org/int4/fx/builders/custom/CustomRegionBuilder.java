package org.int4.fx.builders.custom;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.scene.layout.Region;

import org.int4.fx.builders.common.AbstractRegionBuilder;

/**
 * Builder for custom {@link Region} instances created via a supplied factory.
 * <p>
 * This builder allows arbitrary {@link Region} subclasses to participate in the
 * fluent builder API, including styling and common region options.
 *
 * @param <C> the concrete {@link Region} type
 */
public class CustomRegionBuilder<C extends Region> extends AbstractRegionBuilder<C, CustomRegionBuilder<C>> {
  private final Supplier<C> regionSupplier;

  /**
   * Creates a new builder for a custom node.
   *
   * @param regionSupplier the supplier used to create the region, cannot be {@code null}
   * @param styleClasses the style classes to apply to the region, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code regionSupplier} or {@code styleClasses} is {@code null}
   */
  public CustomRegionBuilder(Supplier<C> regionSupplier, String... styleClasses) {
    super(styleClasses);

    this.regionSupplier = Objects.requireNonNull(regionSupplier, "regionSupplier");
  }

  @Override
  public C build() {
    return initialize(regionSupplier.get());
  }
}
