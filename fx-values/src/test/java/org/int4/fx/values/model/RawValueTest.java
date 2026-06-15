package org.int4.fx.values.model;

import java.util.Map;
import java.util.NoSuchElementException;

import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawValueTest {
  private static final Template TEST_TEMPLATE = new Template() {
    @Override
    public String key() {
      return "test";
    }

    @Override
    public Map<String, Object> args() {
      return Map.of("a", "b");
    }
  };

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
}
