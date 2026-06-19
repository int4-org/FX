package org.int4.fx.values.domain;

import java.util.List;
import java.util.Objects;

import org.int4.common.collection.Immutable;
import org.int4.fx.core.util.Template;

/**
 * A collection of standard templates for domain-related message descriptors.
 */
public interface DomainTemplates {

  /**
   * Inapplicable template singleton.
   */
  static final Template INAPPLICABLE = Template.of("domain.inapplicable");

  /**
   * Missing template singleton.
   */
  static final Template MISSING = Template.of("domain.missing");

  /**
   * Invalid template singleton.
   */
  static final Template INVALID = Template.of("domain.invalid");

  /**
   * Creates a template indicating that a value is outside a defined range.
   *
   * @param <T> the value type
   * @param min the inclusive minimum value, never {@code null}
   * @param max the inclusive maximum value, never {@code null}
   * @return a {@link Template}, never {@code null}
   */
  static <T> Template outOfRange(T min, T max) {
    return Template.of(
      "domain.outOfRange",
      Immutable.sequencedMap(
        "min", Objects.requireNonNull(min, "min"),
        "max", Objects.requireNonNull(max, "max")
      )
    );
  }

  /**
   * Creates a template indicating that a value is not aligned with a required step increment.
   *
   * @param <T> the value type
   * @param min the base value for alignment, never {@code null}
   * @param step the required step size, never {@code null}
   * @return a {@link Template}, never {@code null}
   */
  static <T extends Number> Template misaligned(T min, T step) {
    return Template.of(
      "domain.misaligned",
      Immutable.sequencedMap(
        "min", Objects.requireNonNull(min, "min"),
        "step", Objects.requireNonNull(step, "step")
      )
    );
  }

  /**
   * Creates a template indicating that a value is not contained within a specific set of items.
   *
   * @param <T> the value type
   * @param items the allowed items, never {@code null}
   * @return a {@link Template}, never {@code null}
   */
  static <T> Template notContained(List<T> items) {
    return Template.of(
      "domain.notContained",
      Immutable.sequencedMap(
        "items", Immutable.of(Objects.requireNonNull(items, "items"))
      )
    );
  }

  /**
   * Creates a template indicating that a string does not match a required regular expression.
   *
   * @param regex the regular expression pattern, never {@code null}
   * @return a {@link Template}, never {@code null}
   */
  static Template noMatch(String regex) {
    return Template.of(
      "domain.noMatch",
      Immutable.sequencedMap(
        "regex", Objects.requireNonNull(regex, "regex")
      )
    );
  }
}
