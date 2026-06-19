package org.int4.fx.values.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleTest {
  @Test
  void shouldDelegateToPredicate() {
    Rule<String> rule = Rule.of(v -> v.length() > 3, DomainTemplates.INVALID);

    assertThat(rule.test("abc")).isFalse();
    assertThat(rule.test("abcd")).isTrue();
    assertThat(rule.evaluate("abc")).isEqualTo(new Membership.Excluded(DomainTemplates.INVALID));
    assertThat(rule.evaluate("abcd")).isEqualTo(Membership.MEMBER);
  }

  @Test
  void shouldEnforceNonNullConstraints() {
    assertThatThrownBy(() -> Rule.of(null, DomainTemplates.INVALID)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> Rule.of(v -> true, null)).isInstanceOf(NullPointerException.class);
  }
}
