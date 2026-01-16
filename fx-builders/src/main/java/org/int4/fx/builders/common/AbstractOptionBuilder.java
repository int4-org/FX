package org.int4.fx.builders.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class for fluent builders that collect deferred configuration
 * options to be applied after object creation.
 * <p>
 * Subclasses register configuration steps via {@link #apply(Consumer)}
 * (or convenience methods built on top of it). These options are applied
 * in the order they were added when {@link #initialize(Object)} is invoked.
 * <p>
 * This class intentionally does not define object instantiation; concrete
 * subclasses are responsible for creating the target instance and then
 * calling {@code initialize(...)} to apply all collected options.
 *
 * @param <T> the type of object being configured
 * @param <B> the concrete builder type (self type)
 */
public abstract class AbstractOptionBuilder<T, B extends AbstractOptionBuilder<T, B>> {
  private final List<Consumer<? super T>> options = new ArrayList<>();

  /**
   * Constructs a new instance.
   */
  protected AbstractOptionBuilder() {
  }

  /**
   * Returns this builder cast to its concrete self type.
   * <p>
   * This method supports fluent APIs without requiring casts in subclasses.
   *
   * @return this builder instance
   */
  @SuppressWarnings("unchecked")
  protected final B self() {
    return (B)this;
  }

  /**
   * Registers a configuration option to be applied to the target object
   * during initialization.
   * <p>
   * Options are executed in the order they are registered.
   *
   * @param option a configuration action to apply, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   */
  public final B apply(Consumer<? super T> option) {
    options.add(option);

    return self();
  }

  /**
   * Applies all registered configuration options to the given object.
   * <p>
   * Subclasses typically call this method immediately after creating the
   * target instance.
   *
   * @param obj the object to initialize
   * @return the initialized object
   */
  protected final T initialize(T obj) {
    for(Consumer<? super T> option : options) {
      option.accept(obj);
    }

    return obj;
  }
}
