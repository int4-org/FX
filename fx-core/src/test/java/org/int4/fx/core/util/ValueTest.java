package org.int4.fx.core.util;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueTest {

  @Test
  void shouldRepresentPresentValue() {
    Value<String> value = Value.present("foo");

    assertThat(value.isPresent()).isTrue();
    assertThat(value.isAbsent()).isFalse();
    assertThat(value.orElse("bar")).isEqualTo("foo");
    assertThat(value.orElseThrow()).isEqualTo("foo");
  }

  @Test
  void shouldRepresentPresentNullValue() {
    Value<String> value = Value.present(null);

    assertThat(value.isPresent()).isTrue();
    assertThat(value.isAbsent()).isFalse();
    assertThat(value.orElse("bar")).isNull();
    assertThat(value.orElseThrow()).isNull();
  }

  @Test
  void shouldRepresentAbsentValue() {
    Value<String> value = Value.absent();

    assertThat(value.isPresent()).isFalse();
    assertThat(value.isAbsent()).isTrue();
    assertThat(value.orElse("bar")).isEqualTo("bar");
    assertThatThrownBy(() -> value.orElseThrow()).isExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldSupportPatternMatching() {
    Value<String> present = Value.present("foo");
    Value<String> absent = Value.absent();

    assertThat(present).isInstanceOfSatisfying(Value.Present.class, p -> {
      assertThat(p.value()).isEqualTo("foo");
    });

    assertThat(absent).isExactlyInstanceOf(Value.Absent.class);

    String resultPresent = switch(present) {
      case Value.Present(var v) -> v;
      case Value.Absent() -> "absent";
    };

    String resultAbsent = switch(absent) {
      case Value.Present(var v) -> v;
      case Value.Absent() -> "absent";
    };

    assertThat(resultPresent).isEqualTo("foo");
    assertThat(resultAbsent).isEqualTo("absent");
  }

  @Test
  void shouldRepresentPresentValueWithRecord() {
    Value.Present<String> present = new Value.Present<>("foo");

    assertThat(present.value()).isEqualTo("foo");
    assertThat(present.isPresent()).isTrue();
    assertThat(present.orElse("bar")).isEqualTo("foo");
    assertThat(present.orElseThrow()).isEqualTo("foo");
  }

  @Test
  void shouldRepresentAbsentValueWithRecord() {
    Value.Absent<String> absent = new Value.Absent<>();

    assertThat(absent.isPresent()).isFalse();
    assertThat(absent.orElse("bar")).isEqualTo("bar");
    assertThatThrownBy(() -> absent.orElseThrow()).isExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldMapValue() {
    assertThat(Value.present("foo").map(v -> v + "bar")).isEqualTo(Value.present("foobar"));
    assertThat(Value.present("foo").map(v -> null)).isEqualTo(Value.present(null));
    assertThat(Value.<String>present(null).map(v -> v == null ? "null" : "not null")).isEqualTo(Value.present("null"));
    assertThat(Value.<String>absent().map(v -> v + "bar")).isEqualTo(Value.absent());
  }

  @Test
  void shouldFlatMapValue() {
    assertThat(Value.present("foo").flatMap(v -> Value.present(v + "bar"))).isEqualTo(Value.present("foobar"));
    assertThat(Value.present("foo").flatMap(v -> Value.absent())).isEqualTo(Value.absent());
    assertThat(Value.<String>present(null).flatMap(v -> Value.present(v))).isEqualTo(Value.present(null));
    assertThat(Value.<String>absent().flatMap(v -> Value.present(v + "bar"))).isEqualTo(Value.absent());
  }

  @Test
  void mapShouldThrowExceptionWhenMapperIsNull() {
    assertThatThrownBy(() -> Value.present("foo").map(null)).isExactlyInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> Value.absent().map(null)).isExactlyInstanceOf(NullPointerException.class);
  }

  @Test
  void flatMapShouldThrowExceptionWhenMapperIsNull() {
    assertThatThrownBy(() -> Value.present("foo").flatMap(null)).isExactlyInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> Value.absent().flatMap(null)).isExactlyInstanceOf(NullPointerException.class);
  }
}
