package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A model representing a boolean value.
 * <p>
 * A boolean model holds a single {@code boolean} value that may be nullable if the domain allows.
 * The model tracks validity and applicability, and supports observers for changes to the value or its state.
 * <p>
 * There are three ways to read the value from the model:
 * <ul>
 *   <li>{@link #getRawValue()} – returns the stored value as a {@link Boolean} unconditionally;
 *       returns {@code null} if the model is marked null.</li>
 *   <li>{@link #getValue()} – returns the value if the model is valid, applicable, and not null;
 *       otherwise returns {@code null}.</li>
 *   <li>{@link #get()} – returns the value if valid; throws {@link NullValueException} if marked null
 *       or not applicable; throws {@link InvalidValueException} if invalid.</li>
 * </ul>
 * Typically, {@link #get()} is used when manipulating the model directly for business logic,
 * whereas {@link #getValue()} is safer for UI binding and general inspection.
 */
public interface BooleanModel extends ValueModel<Boolean> {

  /**
   * Creates a non-null boolean model with initial value {@code false}.
   *
   * @return a new boolean model, never {@code null}
   */
  public static BooleanModel of() {
    return of(false);
  }

  /**
   * Creates a non-null boolean model with the specified initial value.
   *
   * @param initialValue the initial value
   * @return a new boolean model, never {@code null}
   */
  public static BooleanModel of(boolean initialValue) {
    return of(initialValue, Domain.of(false, true));
  }

  /**
   * Creates a boolean model with the specified initial value and domain.
   *
   * @param initialValue the initial value, may be {@code null} if domain allows
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new boolean model with the supplied initial value and domain, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  public static BooleanModel of(boolean initialValue, Domain<Boolean> domain) {
    return new SimpleBooleanModel(initialValue, domain);
  }

  /**
   * Returns whether the model is currently marked as null.
   *
   * @return {@code true} if the model is null, {@code false} otherwise
   */
  boolean isNull();

  /**
   * Returns the current boolean value, potentially throwing if invalid or null.
   * <p>
   * If the model is valid and not null, the stored value is returned.
   * If the model is valid but marked null or not applicable, a {@link NullValueException} is thrown.
   * If the model is invalid, an {@link InvalidValueException} is thrown.
   *
   * @return the current value
   * @throws NullValueException if the model is null or not applicable
   * @throws InvalidValueException if the model is invalid
   */
  boolean get();

  /**
   * Updates the value of this model. The new value must conform to the
   * model's domain; otherwise, the model will be marked invalid.
   *
   * @param newValue the new value
   */
  void set(boolean newValue);
}
