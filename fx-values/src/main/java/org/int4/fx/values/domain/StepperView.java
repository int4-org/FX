package org.int4.fx.values.domain;

/**
 * A view that provides logical stepping within a domain.
 * <p>
 * The {@link #step(Object, int)} method produces a value that is valid in
 * the domain, moving forward or backward by a specified number of steps.
 *
 * <h2>Stepping behavior:</h2>
 * <ul>
 *   <li>If {@code steps == 0}, the input value is returned unaltered, even if invalid.</li>
 *   <li>If {@code steps != 0} and the input value is invalid, it is first
 *       normalized (snapped to the nearest valid value). This normalization
 *       counts as the first step only if it changes the value.</li>
 *   <li>Subsequent steps are applied relative to the normalized value.</li>
 *   <li>The return value is guaranteed to be valid in this domain.</li>
 * </ul>
 *
 * <p>Example:</p>
 * <pre>
 * Domain: {10, 20, 30, 40}
 * step(0, 0)  → 0        (unchanged)
 * step(0, 1)  → 10       (snap counts as first step)
 * step(10, 1) → 20       (normal step)
 * step(22, 1) → 20       (snap first, counts as step)
 * step(22, 2) → 30       (snap first, then one step)
 * </pre>
 *
 * @param <T> the type of values in the domain
 */
public interface StepperView<T> extends View<T> {

  /**
   * Returns a value stepped from the given input.
   *
   * @param value an input value, valid or invalid in the domain
   * @param steps a number of steps to move; can be positive, negative, or zero
   * @return a new value valid in this domain, or the original value if {@code steps == 0}
   */
  T step(T value, int steps);
}