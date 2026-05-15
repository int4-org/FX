package org.int4.fx.core.util;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

class RecordBasedTemplateTest {

  @Test
  void argsShouldExtractComponentsFromRecord() {
    record TestRecord(String name, int age, boolean member) implements RecordBasedTemplate {
      @Override public String key() { return "test"; }
    }

    TestRecord record = new TestRecord("John", 30, true);
    Map<String, Object> args = record.args();

    assertThat(args).containsExactly(
      entry("name", "John"),
      entry("age", 30),
      entry("member", true)
    );
  }

  @Test
  void argsShouldBeEmptyForEmptyRecord() {
    record EmptyRecord() implements RecordBasedTemplate {
      @Override public String key() { return "empty"; }
    }

    assertThat(new EmptyRecord().args()).isEmpty();
  }

  @Test
  void argsShouldHaveFixedIterationOrder() {
    record OrderedRecord(int first, int second, int third) implements RecordBasedTemplate {
      @Override public String key() { return "ordered"; }
    }

    OrderedRecord record = new OrderedRecord(1, 2, 3);
    assertThat(record.args().keySet()).containsExactly("first", "second", "third");
  }

  @Test
  void shouldThrowIllegalStateExceptionForNonRecord() {
    class NonRecord implements RecordBasedTemplate {
      @Override public String key() { return "non-record"; }
    }

    assertThatThrownBy(() -> new NonRecord().args())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Class must be a record");
  }

  @Test
  void shouldThrowIllegalStateExceptionOnAccessorFailure() {
    record BadRecord(String value) implements RecordBasedTemplate {
      @Override public String key() { return "bad"; }
      @Override public String value() { throw new RuntimeException("Oops"); }
    }

    assertThatThrownBy(() -> new BadRecord("foo").args())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Failed to extract record component: value");
  }
}
