package org.int4.fx.values.model;

import java.util.List;

import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleMultiChoiceModelTest {
  private static final Template INCOMPATIBLE_TEMPLATE = Template.of("test.incompatible");

  @Test
  void addShouldAppendToTheEndAndPreserveOrder() {
    MultiChoiceModel<String> model = MultiChoiceModel.of(Domain.any());

    model.add("A");
    model.add("B");
    model.add("A");

    assertThat(model.getValue()).containsExactly("A", "B", "A");
  }

  @Test
  void removeShouldRemoveFirstOccurrenceOnly() {
    MultiChoiceModel<String> model = MultiChoiceModel.of(List.of("A", "B", "A"), Domain.any());

    model.remove("A");

    assertThat(model.getValue()).containsExactly("B", "A");
  }

  @Test
  void removeWhenItemNotPresentShouldHaveNoEffect() {
    MultiChoiceModel<String> model = MultiChoiceModel.of(List.of("A", "B"), Domain.any());

    model.remove("C");

    assertThat(model.getValue()).containsExactly("A", "B");
  }

  @Test
  void addWhenIncompatibleStateShouldRecoverByStartingNewList() {
    MultiChoiceModel<String> model = MultiChoiceModel.of(Domain.any());

    // Force incompatible state
    model.trySet("invalid", s -> { throw new RuntimeException(); }, INCOMPATIBLE_TEMPLATE);
    assertThat(model.getRawValue()).isInstanceOf(RawValue.Incompatible.class);

    // Add item - should recover
    model.add("Recovered");

    assertThat(model.getValue()).containsExactly("Recovered");
    assertThat(model.isValid()).isTrue();
  }

  @Test
  void removeWhenIncompatibleStateShouldBeNoOp() {
    MultiChoiceModel<String> model = MultiChoiceModel.of(List.of("A"), Domain.any());

    // Force incompatible state
    model.trySet("invalid", s -> { throw new RuntimeException(); }, INCOMPATIBLE_TEMPLATE);
    assertThat(model.getRawValue()).isInstanceOf(RawValue.Incompatible.class);

    // Remove item - should be no-op in incompatible state
    model.remove("A");

    assertThat(model.getRawValue()).isInstanceOf(RawValue.Incompatible.class);
  }

  @Test
  void permissivenessShouldAllowInvalidState() {
    Domain<String> restrictedDomain = Domain.of("A", "B");
    MultiChoiceModel<String> model = MultiChoiceModel.of(restrictedDomain);

    model.add("C"); // Not in domain

    assertThat(model.getRawValue().orElse(null)).containsExactly("C");
    assertThat(model.isValid()).isFalse();
    assertThat(model.getValue()).isNull();
  }

  @Test
  void addWithNullShouldWorkAsDeterminedByDomain() {
    MultiChoiceModel<String> model = MultiChoiceModel.of(Domain.any()); // any allows null

    model.add(null);

    assertThat(model.getValue()).containsExactly((String) null);
    assertThat(model.isValid()).isTrue();
  }
}
