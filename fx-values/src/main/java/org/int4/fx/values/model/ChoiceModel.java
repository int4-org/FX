package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A model representing a value selected from a fixed set of allowed values.
 * <p>
 * A choice model holds a single value of type {@code T} that must either be
 * {@code null} (if allowed by the domain) or one of the values in the
 * associated {@link Domain}. The model tracks validity and applicability,
 * and supports observers for changes to the value or its state.
 * <p>
 * There are three ways to read the value from the model:
 * <ul>
 *   <li>{@link #getRawValue()} – returns the stored value unconditionally.</li>
 *   <li>{@link #getValue()} – returns the value if the model is valid and applicable, otherwise {@code null}.</li>
 *   <li>{@link #get()} – returns the value if valid; returns {@code null} if not applicable;
 *       throws {@link InvalidValueException} if the value is invalid.</li>
 * </ul>
 * Typically, {@link #get()} is used when manipulating the model directly for business logic,
 * whereas {@link #getValue()} is safer for UI binding and general inspection.
 *
 * @param <T> the type of values allowed by this model
 */
public interface ChoiceModel<T> extends ValueModel<T> {

  /**
   * Creates a choice model with the supplied values. The first value in the
   * array is used as the initial value if available.
   *
   * @param <T> the value type
   * @param values the allowed values for the model, may be empty; cannot be a {@code null} array
   * @return a new choice model containing the supplied values, never {@code null}
   * @throws NullPointerException if values arrays is {@code null}
   */
  static <T> ChoiceModel<T> of(@SuppressWarnings("unchecked") T... values) {
    return of(values.length == 0 ? null : values[0], Domain.of(values));
  }

  /**
   * Creates a choice model with the given initial value and allowed values.
   *
   * @param <T> the value type
   * @param initialValue the initial selected value, may be {@code null} if the domain allows
   * @param values the allowed values for the model, may be empty; cannot be a {@code null} array
   * @return a new choice model with the supplied initial value and domain, never {@code null}
   * @throws NullPointerException if values arrays is {@code null}
   */
  static <T> ChoiceModel<T> withInitial(T initialValue, @SuppressWarnings("unchecked") T... values) {
    return of(initialValue, Domain.of(values));
  }

  /**
   * Creates a choice model backed by the given domain, with an initial value of {@code null}.
   *
   * @param <T> the value type
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new choice model with {@code null} as the initial value
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static <T> ChoiceModel<T> of(Domain<T> domain) {
    return of(null, domain);
  }

  /**
   * Creates a choice model with the given initial value and domain.
   *
   * @param initialValue the initial selected value, may be {@code null} if the domain allows
   * @param domain the domain of allowed values, cannot be {@code null}
   * @param <T> the value type
   * @return a new choice model with the supplied initial value and domain
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static <T> ChoiceModel<T> of(T initialValue, Domain<T> domain) {
    return new SimpleChoiceModel<>(initialValue, domain);
  }

  /**
   * Returns the current value of this model, potentially throwing if invalid.
   * <p>
   * If the model is valid and applicable, the stored value is returned.
   * If the model is valid but not applicable, {@code null} is returned.
   * If the model is invalid, an {@link InvalidValueException} is thrown.
   *
   * @return the current value, may be {@code null} if not applicable
   * @throws InvalidValueException if the model is invalid
   */
  T get();

  /**
   * Updates the value of this model. The new value must conform to the
   * model's domain; otherwise, the model will be marked invalid.
   *
   * @param newValue the new value, may be {@code null} if allowed by the domain
   */
  void set(T newValue);
}
