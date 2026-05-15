package org.int4.fx.values.domain;

import org.int4.fx.core.util.Template;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MembershipTest {
  private static final Template TEST_TEMPLATE = new DomainTemplates.Invalid();

  @Test
  void memberShouldReturnTrue() {
    Membership membership = new Membership.Member();
    assertThat(membership.isMember()).isTrue();
  }

  @Test
  void excludedShouldReturnFalseAndProvideReason() {
    Membership membership = new Membership.Excluded(TEST_TEMPLATE);
    assertThat(membership.isMember()).isFalse();
    assertThat(((Membership.Excluded)membership).reason()).isEqualTo(TEST_TEMPLATE);
  }

  @Test
  void excludedShouldEnforceNonNullReason() {
    assertThatThrownBy(() -> new Membership.Excluded(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void equalsAndHashCodeShouldWork() {
    Membership m1 = new Membership.Member();
    Membership m2 = new Membership.Member();
    Membership e1 = new Membership.Excluded(TEST_TEMPLATE);
    Membership e2 = new Membership.Excluded(TEST_TEMPLATE);

    assertThat(m1).isEqualTo(m2).hasSameHashCodeAs(m2);
    assertThat(e1).isEqualTo(e2).hasSameHashCodeAs(e2);
    assertThat(m1).isNotEqualTo(e1);
  }
}
