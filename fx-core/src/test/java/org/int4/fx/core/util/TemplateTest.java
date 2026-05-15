package org.int4.fx.core.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(Template.KEY_PATTERN.matcher(key).matches())
      .describedAs("Key '%s' should be valid", key)
      .isTrue();
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
    assertThat(Template.KEY_PATTERN.matcher(key).matches())
      .describedAs("Key '%s' should be invalid", key)
      .isFalse();
  }
}
