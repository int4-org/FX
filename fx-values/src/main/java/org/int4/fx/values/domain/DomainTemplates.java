package org.int4.fx.values.domain;

import java.util.List;
import java.util.Objects;

import org.int4.fx.core.util.RecordBasedTemplate;
import org.int4.fx.core.util.Template;

/**
 * A collection of standard templates for domain-related message descriptors.
 */
public interface DomainTemplates {

  /**
   * Inapplicable template singleton.
   */
  static final Template INAPPLICABLE = new Inapplicable();

  /**
   * Missing template singleton.
   */
  static final Template MISSING = new Missing();

  /**
   * Invalid template singleton.
   */
  static final Template INVALID = new Invalid();

  /**
   * A template indicating that a domain is not applicable in the current context.
   */
  record Inapplicable() implements RecordBasedTemplate {
    @Override
    public String key() {
      return "domain.inapplicable";
    }
  }

  /**
   * A template indicating that a value is missing.
   */
  record Missing() implements RecordBasedTemplate {
    @Override
    public String key() {
      return "domain.missing";
    }
  }

  /**
   * A template indicating that a value is outside a defined range.
   *
   * @param <T> the value type
   * @param min the inclusive minimum value, never {@code null}
   * @param max the inclusive maximum value, never {@code null}
   */
  record OutOfRange<T>(T min, T max) implements RecordBasedTemplate {

    /**
     * Constructs a new instance.
     *
     * @throws NullPointerException if {@code min} or {@code max} is {@code null}
     */
    public OutOfRange {
      Objects.requireNonNull(min, "min");
      Objects.requireNonNull(max, "max");
    }

    @Override
    public String key() {
      return "domain.outOfRange";
    }
  }

  /**
   * A template indicating that a value is not aligned with a required step increment.
   *
   * @param <T> the value type
   * @param min the base value for alignment, never {@code null}
   * @param step the required step size, never {@code null}
   */
  record Misaligned<T extends Number>(T min, T step) implements RecordBasedTemplate {

    /**
     * Constructs a new instance.
     *
     * @throws NullPointerException if {@code min} or {@code step} is {@code null}
     */
    public Misaligned {
      Objects.requireNonNull(min, "min");
      Objects.requireNonNull(step, "step");
    }

    @Override
    public String key() {
      return "domain.misaligned";
    }
  }

  /**
   * A template indicating that a value is not contained within a specific set of items.
   *
   * @param <T> the value type
   * @param items the allowed items, never {@code null}
   */
  record NotContained<T>(List<T> items) implements RecordBasedTemplate {

    /**
     * Constructs a new instance.
     *
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public NotContained {
      Objects.requireNonNull(items, "items");
    }

    @Override
    public String key() {
      return "domain.notContained";
    }
  }

  /**
   * A template indicating that a string does not match a required regular expression.
   *
   * @param regex the regular expression pattern, never {@code null}
   */
  record NoMatch(String regex) implements RecordBasedTemplate {

    /**
     * Constructs a new instance.
     *
     * @throws NullPointerException if {@code regex} is {@code null}
     */
    public NoMatch {
      Objects.requireNonNull(regex, "regex");
    }

    @Override
    public String key() {
      return "domain.noMatch";
    }
  }

  /**
   * A template for generic validation failures.
   */
  record Invalid() implements RecordBasedTemplate {
    @Override
    public String key() {
      return "domain.invalid";
    }
  }
}
