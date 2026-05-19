package org.int4.fx.values.domain;

import java.util.Objects;
import java.util.function.Predicate;

import org.int4.fx.core.util.Template;

/**
 * A rule that checks a value against a constraint.
 * <p>
 * Rules are used within a {@link Domain} to define membership conditions.
 *
 * @param <T> the type of value this rule validates
 */
public interface Rule<T> extends Predicate<T> {

  /**
   * Creates a new rule from the given predicate and template.
   *
   * @param <T> the value type
   * @param predicate the predicate to test, never {@code null}
   * @param failureTemplate the template providing the failure reason, never {@code null}
   * @return a new rule, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  static <T> Rule<T> of(Predicate<T> predicate, Template failureTemplate) {
    Objects.requireNonNull(predicate, "predicate");
    Objects.requireNonNull(failureTemplate, "failureTemplate");

    Membership.Excluded excluded = new Membership.Excluded(failureTemplate);

    return value -> predicate.test(value) ? Membership.MEMBER : excluded;
  }

  @Override
  default boolean test(T value) {
    return evaluate(value).equals(Membership.MEMBER);
  }

  /**
   * Checks the rule against the given value and returns its membership status.
   *
   * @param value a value to check against this rule, cannot be {@code null}
   * @return a {@link Membership}, never {@code null}
   */
  Membership evaluate(T value);
}
