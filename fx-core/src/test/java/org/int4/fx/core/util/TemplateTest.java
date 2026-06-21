package org.int4.fx.core.util;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

import org.int4.common.collection.Immutable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TemplateTest {

  @ParameterizedTest
  @ValueSource(strings = {
    "a",
    "_a",
    "a.b",
    "a_b.c",
    "a-b.c",
    "a.b1",
    "domain.outOfRange",
    "_namespace.myKey_123",
    "a.b-c.d",
    "part-with-dash",
    "part_with_underscore",
    "partWithMixedCase",
    "_a.b",
    "a._b"
  })
  void shouldMatchValidKeys(String key) {
    assertDoesNotThrow(() -> Template.of(key));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "",
    " ",
    "a b",
    ".a",
    "a.",
    "a..b",
    "1a",
    "a.1b",
    "A",
    "a.B",
    "a.B.C",
    "-a",
    "a-",
    "a.-b",
    "a.b-",
    "a--b",
    "a.1",
    "a!b",
    "a$b",
    "a. b",
    "a .b",
    "a.123"
  })
  void shouldNotMatchInvalidKeys(String key) {
    assertThatThrownBy(() -> Template.of(key)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowOnNullKey() {
    assertThatThrownBy(() -> Template.of(null)).isInstanceOf(NullPointerException.class).hasMessage("key");
  }

  @Test
  void shouldThrowOnNullArgs() {
    assertThatThrownBy(() -> new Template("a", null)).isInstanceOf(NullPointerException.class).hasMessage("args");
  }

  @Test
  void shouldHaveEmptyArgsByDefault() {
    Template template = Template.of("a.b");

    assertThat(template.key()).isEqualTo("a.b");
    assertThat(template.args()).isEmpty();
  }

  @Test
  void shouldStoreKeyAndArgs() {
    SequencedMap<String, Object> args = new LinkedHashMap<>();

    args.put("name", "test");
    args.put("count", 42);

    Template template = Template.of("domain.message", args);

    assertThat(template.key()).isEqualTo("domain.message");
    assertThat(template.args()).containsExactlyEntriesOf(Immutable.sequencedMap("name", "test", "count", 42));
    assertThat(template.args()).hasSize(2);

    args.put("another", "oops");  // it should have made an (immutable) copy

    assertThat(template.args()).hasSize(2);

    assertThatThrownBy(() -> template.args().clear()).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldCreateWithEmptyArgsMap() {
    SequencedMap<String, Object> args = Immutable.sequencedMap();

    Template template = Template.of("a.b.c", args);

    assertThat(template.key()).isEqualTo("a.b.c");
    assertThat(template.args()).isEmpty();
  }
}
