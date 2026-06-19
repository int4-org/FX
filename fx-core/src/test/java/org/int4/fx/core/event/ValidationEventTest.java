package org.int4.fx.core.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.int4.fx.core.util.Template;
import org.junit.jupiter.api.Test;

class ValidationEventTest {
  private static final Template TEMPLATE = Template.of("test.key");

  @Test
  void validShouldReturnValidEvent() {
    ValidationEvent event = ValidationEvent.valid();

    assertThat(event.isValid()).isTrue();
    assertThat(event.template()).isNull();
    assertThat(event.invalidValue()).isNull();
    assertThat(event.invalidValueTypeIncompatible()).isFalse();
    assertThat(event.getEventType()).isEqualTo(ValidationEvent.VALIDATION_CHANGED);
  }

  @Test
  void invalidShouldReturnInvalidEvent() {
    Object value = "invalid-value";
    ValidationEvent event = ValidationEvent.invalid(TEMPLATE, value, true);

    assertThat(event.isValid()).isFalse();
    assertThat(event.template()).isEqualTo(TEMPLATE);
    assertThat(event.invalidValue()).isEqualTo(value);
    assertThat(event.invalidValueTypeIncompatible()).isTrue();
    assertThat(event.getEventType()).isEqualTo(ValidationEvent.VALIDATION_CHANGED);
  }

  @Test
  void invalidShouldAcceptNullValue() {
    ValidationEvent event = ValidationEvent.invalid(TEMPLATE, null, false);

    assertThat(event.isValid()).isFalse();
    assertThat(event.template()).isEqualTo(TEMPLATE);
    assertThat(event.invalidValue()).isNull();
    assertThat(event.invalidValueTypeIncompatible()).isFalse();
  }

  @Test
  void invalidShouldThrowExceptionForNullTemplate() {
    assertThatThrownBy(() -> ValidationEvent.invalid(null, "value", false))
      .isExactlyInstanceOf(NullPointerException.class)
      .hasMessage("template");
  }
}
