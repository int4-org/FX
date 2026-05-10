package org.int4.fx.values.domain;

/**
 * A view for continuous numeric domains.
 *
 * @param <T> the type of values in the domain
 */
public interface ContinuousView<T extends Number> extends View<T> {

  /**
   * Projects an arbitrary value into the valid domain range.
   * <p>
   * This is a convenience method equivalent to:
   * <pre>{@code
   * get(fractionOf(value))
   * }</pre>
   * <p>
   * The value is first mapped to a normalized fraction via {@link #fractionOf(Number)},
   * and then converted back into a valid domain value via {@link #get(double)}.
   *
   * @param value a value, can be {@code null}
   * @return a value guaranteed to be valid in the associated domain
   */
  default T project(T value) {
    return get(fractionOf(value));
  }

  /**
   * Maps a normalized value in [0,1] to the domain value. A value outside
   * this range will be clamped first.
   *
   * @param fraction a value between 0 and 1
   * @return a value valid in the associated domain
   */
  T get(double fraction);

  /**
   * Maps a domain value to a normalized [0,1] fraction.
   * If value is outside the domain, it can be clamped.
   *
   * @param value a value, can be {@code null}
   * @return a normalized [0,1] fraction
   */
  double fractionOf(T value);
}
