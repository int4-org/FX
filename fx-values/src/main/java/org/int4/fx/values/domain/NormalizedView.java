package org.int4.fx.values.domain;

/**
 * A view that deterministically projects arbitrary values into the domain's valid value set.
 * <p>
 * If the domain defines a meaningful ordering or notion of closeness, this view must use it
 * to select a valid value that is closest according to that rule, using a deterministic
 * tie-breaking strategy if multiple candidates are equally close. Otherwise, it must return
 * a valid value using a deterministic strategy.
 *
 * @param <T> the type of values in the domain
 */
public interface NormalizedView<T> extends View<T> {

  /**
   * Adjusts the given value to a value valid within the associated domain,
   * or returns the same value if it was already valid.
   * <p>
   * If the domain defines a meaningful ordering or notion of closeness, the result must be
   * the closest valid value according to that rule; ties must be resolved deterministically.
   * Otherwise, a deterministic valid value must be returned.
   *
   * @param value a value to snap, can be {@code null}
   * @return a value valid within the domain, can be {@code null}
   */
  T snap(T value);
}
