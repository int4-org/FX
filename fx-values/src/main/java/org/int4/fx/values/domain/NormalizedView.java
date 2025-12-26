package org.int4.fx.values.domain;

/**
 * A view that provides snapshot normalization (snapping) for values.
 *
 * @param <T> the type of values in the domain
 */
public interface NormalizedView<T> extends View<T> {

  /**
   * Adjusts the given value to be a value valid within the associated domain,
   * or returns the same value if it was already valid.
   *
   * @param value a value to snap
   * @return a value valid within the domain
   */
  T snap(T value);
}
