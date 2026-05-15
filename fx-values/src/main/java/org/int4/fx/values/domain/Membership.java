package org.int4.fx.values.domain;

import java.util.Objects;

import org.int4.fx.core.util.Template;

/**
 * Represents the result of evaluating whether a value belongs to a {@link Domain}.
 * <p>
 * A {@code Membership} is the outcome of a domain evaluation and indicates whether a
 * value is part of the domain or has been excluded by one of its constraints.
 */
public sealed interface Membership {

  /**
   * Returns {@code true} if the value is a member of the domain, otherwise {@code false}.
   *
   * @return {@code true} if the value is a member of the domain, otherwise {@code false}
   */
  boolean isMember();

  /**
   * Indicates that the evaluated value belongs to the domain.
   */
  record Member() implements Membership {
    @Override
    public boolean isMember() {
      return true;
    }
  }

  /**
   * Indicates that the evaluated value does not belong to the domain.
   * <p>
   * This result includes a {@link Template} explaining why the value was excluded.
   *
   * @param reason the reason for exclusion, never {@code null}
   */
  record Excluded(Template reason) implements Membership {

    public Excluded {
      Objects.requireNonNull(reason, "reason");
    }

    @Override
    public boolean isMember() {
      return false;
    }
  }
}
