package org.int4.fx.values.domain;

import java.util.Objects;
import java.util.function.Predicate;

import org.int4.fx.core.util.Template;

/**
 * A rule that associates a {@link Predicate} with a {@link Template}.
 * <p>
 * Rules are used within a {@link Domain} to define membership constraints.
 * When a rule fails, its associated template provides the reason for the
 * exclusion.
 *
 * @param <T> the type of value this rule validates
 */
public interface Rule<T> extends Predicate<T> {

  /**
   * Creates a new rule from the given predicate and template.
   *
   * @param <T> the value type
   * @param predicate the predicate to test, never {@code null}
   * @param template the template providing the failure reason, never {@code null}
   * @return a new rule, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  static <T> Rule<T> of(Predicate<T> predicate, Template template) {
    Objects.requireNonNull(predicate, "predicate");
    Objects.requireNonNull(template, "template");

    return new Rule<>() {
      @Override
      public boolean test(T value) {
        return predicate.test(value);
      }

      @Override
      public Template template() {
        return template;
      }
    };
  }

  /**
   * Returns the template associated with this rule.
   *
   * @return the template, never {@code null}
   */
  Template template();
}
