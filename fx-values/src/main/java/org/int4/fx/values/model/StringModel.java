package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A {@link ValueModel} for {@link String} values.
 * <p>
 * This model supports string-specific constraints and validation, such as
 * regular expression matching. The domain of allowed values can be customized
 * using the {@link Domain} API.
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
 * <p>Example usage:</p>
 * <pre>{@code
 * StringModel name = StringModel.regex(".{5,}");
 * name.set("Alice");      // valid
 * name.set("Bob");        // throws InvalidValueException on get(), getValue() returns null
 * }</pre>
 */
public interface StringModel extends ObjectModel<String> {

  /**
   * Creates a {@code StringModel} that only accepts strings matching
   * the provided regular expression.
   *
   * @param regex the regular expression to validate against, cannot be {@code null}
   * @return a new string model, never {@code null}
   * @throws java.util.regex.PatternSyntaxException if the regex is invalid
   */
  static StringModel regex(String regex) {
    return of(Domain.regex(regex));
  }

  /**
   * Creates a new {@code StringModel} with no initial value and a non-null domain.
   *
   * @return a new string model, never {@code null}
   */
  static StringModel of() {
    return of((String)null);
  }

  /**
   * Creates a new {@code StringModel} with the given initial value and a non-null domain.
   *
   * @param initialValue the initial value, may be {@code null}
   * @return a new string model, never {@code null}
   */
  static StringModel of(String initialValue) {
    return of(initialValue, Domain.nonNull());
  }

  /**
   * Creates a new {@code StringModel} with no initial value and an unrestricted domain.
   *
   * @return a new string model, never {@code null}
   */
  static StringModel nullable() {
    return nullable((String)null);
  }

  /**
   * Creates a new {@code StringModel} with the given initial value and an unrestricted domain.
   *
   * @param initialValue the initial value, may be {@code null}
   * @return a new string model, never {@code null}
   */
  static StringModel nullable(String initialValue) {
    return of(initialValue, Domain.any());
  }

  /**
   * Creates a new {@code StringModel} with the given domain and no initial value.
   *
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new string model, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static StringModel of(Domain<String> domain) {
    return of(null, domain);
  }

  /**
   * Creates a new {@code StringModel} with the given initial value and domain.
   *
   * @param initialValue the initial value, may be {@code null}
   * @param domain the domain of allowed values, cannot be {@code null}
   * @return a new string model, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  static StringModel of(String initialValue, Domain<String> domain) {
    return new SimpleStringModel(initialValue, domain);
  }
}
