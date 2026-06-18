package org.int4.fx.controls.validation;

import org.int4.fx.core.util.Template;

/**
 * Represents a validation problem associated with a value and a validation template.
 *
 * @param <T> the type of the value that failed validation
 */
public sealed interface ValidationIssue<T> {

  /**
   * A validation issue where the value is of the correct type but violates a rule.
   *
   * @param <T> the type of the value
   * @param value the invalid value
   * @param reason the template describing the validation rule that was violated
   */
  record Invalid<T>(T value, Template reason) implements ValidationIssue<T> {}

  /**
   * A validation issue where the value's type is incompatible with the expected type.
   *
   * @param <T> the expected type
   * @param reason the template describing the expected validation rule
   */
  record Incompatible<T>(Template reason) implements ValidationIssue<T> {}
}
