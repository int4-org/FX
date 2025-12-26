package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A model representing a double-precision floating-point value with optional bounds.
 * <p>
 * A double model holds a single {@code double} value that may be nullable if the domain allows.
 * The model tracks validity and applicability, and supports observers for changes to the value or its state.
 * <p>
 * There are three ways to read the value from the model:
 * <ul>
 *   <li>{@link #getRawValue()} – returns the stored value as a {@link Double} unconditionally;
 *       returns {@code null} if the model is marked null.</li>
 *   <li>{@link #getValue()} – returns the value if the model is valid, applicable, and not null;
 *       otherwise returns {@code null}.</li>
 *   <li>{@link #get()} – returns the value if valid; throws {@link NullValueException} if marked null
 *       or not applicable; throws {@link InvalidValueException} if invalid.</li>
 * </ul>
 * Typically, {@link #get()} is used when manipulating the model directly for business logic,
 * whereas {@link #getValue()} is safer for UI binding and general inspection.
 */
public interface DoubleModel extends ValueModel<Double> {

  /**
   * Creates a non-null double model with the specified initial value.
   *
   * @param initialValue the initial value
   * @return a new double model with domain {@link Domain#nonNull()}, never {@code null}
   */
  static DoubleModel of(double initialValue) {
    return of(initialValue, Domain.nonNull());
  }

  /**
   * Creates a double model with a bounded domain and the minimum value as the initial value.
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @return a new bounded double model, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static DoubleModel range(double min, double max) {
    return of(min, Domain.bounded(min, max));
  }

  /**
   * Creates a double model with a bounded domain that allows {@code null}, with initial value {@code null}.
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @return a new bounded nullable double model, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static DoubleModel nullableRange(double min, double max) {
    return of(null, Domain.bounded(min, max).nullable());
  }

  /**
   * Creates a double model with the specified initial value and domain.
   *
   * @param initialValue the initial value, may be {@code null} if domain allows
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new double model with the supplied initial value and domain, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static DoubleModel of(Double initialValue, Domain<Double> domain) {
    return new SimpleDoubleModel(initialValue, domain);
  }

  /**
   * Returns whether the model is currently marked as null.
   *
   * @return {@code true} if the model is null, {@code false} otherwise
   */
  boolean isNull();

  /**
   * Returns the current double value, potentially throwing if invalid or null.
   * <p>
   * If the model is valid and not null, the stored value is returned.
   * If the model is valid but marked null or not applicable, a {@link NullValueException} is thrown.
   * If the model is invalid, an {@link InvalidValueException} is thrown.
   *
   * @return the current value
   * @throws NullValueException if the model is null or not applicable
   * @throws InvalidValueException if the model is invalid
   */
  double get();

  /**
   * Updates the value of this model. The new value must conform to the
   * model's domain; otherwise, the model will be marked invalid.
   *
   * @param newValue the new value
   */
  void set(double newValue);
}
