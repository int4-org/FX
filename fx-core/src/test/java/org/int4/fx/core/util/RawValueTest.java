package org.int4.fx.core.util;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.int4.common.collection.Immutable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawValueTest {
  private static final Template TEST_TEMPLATE = Template.of("test", Immutable.sequencedMap("a", "b"));

  @Test
  void validShouldHoldValue() {
    RawValue<String> rv = RawValue.valid("hello");

    assertThat(rv.isPresent()).isTrue();
    assertThat(rv.isNull()).isFalse();
    assertThat(rv.orElse("default")).isEqualTo("hello");
    assertThat(rv.orElseThrow()).isEqualTo("hello");
    assertThat(rv).isInstanceOf(RawValue.Valid.class);
  }

  @Test
  void validShouldHoldNullValue() {
    RawValue<String> rv = RawValue.valid(null);

    assertThat(rv.isPresent()).isTrue();
    assertThat(rv.isNull()).isTrue();
    assertThat(rv.orElse("default")).isNull();
    assertThat(rv.orElseThrow()).isNull();
  }

  @Test
  void invalidShouldHoldValueAndReason() {
    RawValue<String> rv = RawValue.invalid("bad", TEST_TEMPLATE);

    assertThat(rv.isPresent()).isTrue();
    assertThat(rv.isNull()).isFalse();
    assertThat(rv.orElse("default")).isEqualTo("bad");
    assertThat(rv.orElseThrow()).isEqualTo("bad");
    assertThat(((RawValue.Invalid<String>)rv).reason()).isEqualTo(TEST_TEMPLATE);
  }

  @Test
  void invalidShouldHoldNullValueAndReason() {
    RawValue<String> rv = RawValue.invalid(null, TEST_TEMPLATE);

    assertThat(rv.isPresent()).isTrue();
    assertThat(rv.isNull()).isTrue();
    assertThat(rv.orElse("default")).isNull();
    assertThat(rv.orElseThrow()).isNull();
  }

  @Test
  void incompatibleShouldHoldReasonButNoValue() {
    RawValue<String> rv = RawValue.incompatible(TEST_TEMPLATE);

    assertThat(rv.isPresent()).isFalse();
    assertThat(rv.isNull()).isFalse();
    assertThat(rv.orElse("default")).isEqualTo("default");
    assertThatThrownBy(rv::orElseThrow).isInstanceOf(NoSuchElementException.class);
    assertThat(((RawValue.Incompatible<String>)rv).reason()).isEqualTo(TEST_TEMPLATE);
  }

  @Test
  void shouldEnforceNonNullReason() {
    assertThatThrownBy(() -> RawValue.invalid("abc", null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> RawValue.incompatible(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void equalsAndHashCodeShouldWork() {
    RawValue<String> v1 = RawValue.valid("a");
    RawValue<String> v2 = RawValue.valid("a");
    RawValue<String> v3 = RawValue.valid("b");
    RawValue<String> i1 = RawValue.invalid("a", TEST_TEMPLATE);
    RawValue<String> i2 = RawValue.invalid("a", TEST_TEMPLATE);
    RawValue<String> inc1 = RawValue.incompatible(TEST_TEMPLATE);
    RawValue<String> inc2 = RawValue.incompatible(TEST_TEMPLATE);

    assertThat(v1).isEqualTo(v2).hasSameHashCodeAs(v2);
    assertThat(v1).isNotEqualTo(v3);
    assertThat(v1).isNotEqualTo(i1);
    assertThat(i1).isEqualTo(i2).hasSameHashCodeAs(i2);
    assertThat(inc1).isEqualTo(inc2).hasSameHashCodeAs(inc2);
    assertThat(i1).isNotEqualTo(inc1);
  }

  @Test
  void ifPresentShouldCallConsumerWithValidValue() {
    RawValue<String> rv = RawValue.valid("hello");
    AtomicBoolean called = new AtomicBoolean(false);

    rv.ifPresent(v -> {
      assertThat(v).isEqualTo("hello");
      called.set(true);
    });
    assertThat(called.get()).isTrue();
  }

  @Test
  void ifPresentShouldCallConsumerWithNullValueInValidState() {
    RawValue<String> rv = RawValue.valid(null);
    AtomicBoolean called = new AtomicBoolean(false);

    rv.ifPresent(v -> {
      assertThat(v).isNull();
      called.set(true);
    });
    assertThat(called.get()).isTrue();
  }

  @Test
  void ifPresentShouldCallConsumerInInvalidValue() {
    RawValue<String> rv = RawValue.invalid("bad", TEST_TEMPLATE);
    AtomicBoolean called = new AtomicBoolean(false);

    rv.ifPresent(v -> {
      assertThat(v).isEqualTo("bad");
      called.set(true);
    });
    assertThat(called.get()).isTrue();
  }

  @Test
  void ifPresentShouldNotCallConsumerWithIncompatibleValue() {
    RawValue<String> rv = RawValue.incompatible(TEST_TEMPLATE);
    AtomicBoolean called = new AtomicBoolean(false);

    rv.ifPresent(v -> called.set(true));
    assertThat(called.get()).isFalse();
  }

  @Test
  void ifPresentShouldRejectNullConsumers() {
    assertThatThrownBy(() -> RawValue.valid("a").ifPresent(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> RawValue.invalid("a", TEST_TEMPLATE).ifPresent(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> RawValue.incompatible(TEST_TEMPLATE).ifPresent(null))
      .isInstanceOf(NullPointerException.class);
  }
}
