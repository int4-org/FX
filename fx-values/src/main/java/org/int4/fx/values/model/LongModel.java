package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A model representing a long value with optional bounds.
 * <p>
 * A long model holds a single {@code long} value that may be nullable
 * if the domain allows. The model tracks validity and applicability, and
 * supports observers for changes to the value or its state.
 * <p>
 * There are three ways to read the value from the model:
 * <ul>
 *   <li>{@link #getRawValue()} – returns the stored value as a {@link Long} unconditionally;
 *       returns {@code null} if the model is marked null.</li>
 *   <li>{@link #getValue()} – returns the value if the model is valid, applicable, and not null;
 *       otherwise returns {@code null}.</li>
 *   <li>{@link #get()} – returns the value if valid; throws {@link NullValueException} if marked null
 *       or not applicable; throws {@link InvalidValueException} if invalid.</li>
 * </ul>
 * Typically, {@link #get()} is used when manipulating the model directly for business logic,
 * whereas {@link #getValue()} is safer for UI binding and general inspection.
 */
public interface LongModel extends ValueModel<Long> {

  /**
   * Creates a bounded long model with the given minimum value as the initial value.
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @return a new bounded long model, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static LongModel of(long min, long max) {
    return of(min, Domain.bounded(min, max));
  }

  /**
   * Creates a bounded long model that allows {@code null}, with initial value {@code null}.
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @return a new bounded nullable long model, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static LongModel ofNullable(long min, long max) {
    return of(null, Domain.bounded(min, max).nullable());
  }

  /**
   * Creates a long model with the specified initial value and domain.
   *
   * @param initialValue the initial value, may be {@code null} if domain allows
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new long model with the supplied initial value and domain, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static LongModel of(Long initialValue, Domain<Long> domain) {
    return new SimpleLongModel(initialValue, domain);
  }

  /**
   * Returns whether the model is currently marked as null.
   *
   * @return {@code true} if the model is null, {@code false} otherwise
   */
  boolean isNull();

  /**
   * Returns the current long value, potentially throwing if invalid or null.
   * <p>
   * If the model is valid and not null, the stored value is returned.
   * If the model is valid but marked null or not applicable, a {@link NullValueException} is thrown.
   * If the model is invalid, an {@link InvalidValueException} is thrown.
   *
   * @return the current value
   * @throws NullValueException if the model is null or not applicable
   * @throws InvalidValueException if the model is invalid
   */
  long get();

  /**
   * Updates the value of this model. The new value must conform to the
   * model's domain; otherwise, the model will be marked invalid.
   *
   * @param newValue the new value
   */
  void set(long newValue);
}
