package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A model representing a single object value of type {@code T}.
 * <p>
 * An object model holds a value that may be {@code null} (if allowed by the
 * domain) or a valid object according to the associated {@link Domain}.
 * The model tracks validity and applicability, and supports observers for
 * changes to the value or its state.
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
 * @param <T> the type of object held by this model
 */
public interface ObjectModel<T> extends ValueModel<T> {

  /**
   * Creates a non-null object model with {@code null} as the initial value.
   *
   * @param <T> the value type
   * @return a new object model with a non-null domain, never {@code null}
   */
  static <T> ObjectModel<T> of() {
    return of(Domain.nonNull());
  }

  /**
   * Creates a nullable object model with {@code null} as the initial value.
   *
   * @param <T> the value type
   * @return a new object model with a nullable domain, never {@code null}
   */
  static <T> ObjectModel<T> ofNullable() {
    return of(Domain.any());
  }

  /**
   * Creates an object model backed by the given domain, with an initial value of {@code null}.
   *
   * @param <T> the value type
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new object model with {@code null} as the initial value
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static <T> ObjectModel<T> of(Domain<T> domain) {
    return new SimpleObjectModel<>(null, domain);
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
