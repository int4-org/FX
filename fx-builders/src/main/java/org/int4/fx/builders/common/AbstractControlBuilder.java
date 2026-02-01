package org.int4.fx.builders.common;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.scene.control.Control;

import org.int4.fx.builders.context.BuildContext;

/**
 * Base class for builders that create JavaFX {@link Control} instances.
 *
 * @param <T> the concrete {@link Control} type being built
 * @param <B> the concrete builder type (self type)
 */
public abstract class AbstractControlBuilder<T extends Control, B extends AbstractControlBuilder<T, B>> extends AbstractRegionBuilder<T, B> {
  private final Supplier<T> instantiator;

  /**
   * Creates a new control builder, initializing the resulting control with
   * the given style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes to apply, cannot be {@code null} but may be empty
   */
  protected AbstractControlBuilder(Supplier<T> instantiator, String... styleClasses) {
    super(styleClasses);

    this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
  }

  @Override
  public final T build(BuildContext context) {
    return initialize(context, instantiator.get());
  }
}
