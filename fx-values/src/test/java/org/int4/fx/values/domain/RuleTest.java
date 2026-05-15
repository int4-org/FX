package org.int4.fx.values.domain;

import org.int4.fx.core.util.Template;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleTest {
  private static final Template TEST_TEMPLATE = new DomainTemplates.Invalid();

  @Test
  void shouldDelegateToPredicate() {
    Rule<String> rule = Rule.of(v -> v.length() > 3, TEST_TEMPLATE);

    assertThat(rule.test("abc")).isFalse();
    assertThat(rule.test("abcd")).isTrue();
    assertThat(rule.template()).isEqualTo(TEST_TEMPLATE);
  }

  @Test
  void shouldEnforceNonNullConstraints() {
    assertThatThrownBy(() -> Rule.of(null, TEST_TEMPLATE)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> Rule.of(v -> true, null)).isInstanceOf(NullPointerException.class);
  }
}
