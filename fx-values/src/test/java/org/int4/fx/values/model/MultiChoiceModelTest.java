package org.int4.fx.values.model;

import java.util.List;

import org.int4.fx.values.domain.Domain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MultiChoiceModelTest {

  @Test
  void ofShouldInitializeWithDefaults() {
    MultiChoiceModel<String> model = MultiChoiceModel.of();

    assertThat(model.getValue()).isEmpty();
    assertThat(model.getCompositeDomain()).isEqualTo(Domain.nonNull());
    assertThat(model.getElementDomain()).isEqualTo(Domain.nonNull());
  }

  @Test
  void ofElementDomainShouldInitializeWithCustomElementDomain() {
    Domain<String> elementDomain = Domain.of("A", "B");
    MultiChoiceModel<String> model = MultiChoiceModel.of(elementDomain);

    assertThat(model.getValue()).isEmpty();
    assertThat(model.getCompositeDomain()).isEqualTo(Domain.nonNull());
    assertThat(model.getElementDomain()).isEqualTo(elementDomain);
  }

  @Test
  void ofInitialValueAndElementDomainShouldInitializeCorrectly() {
    Domain<String> elementDomain = Domain.of("A", "B");
    List<String> initialValue = List.of("A");
    MultiChoiceModel<String> model = MultiChoiceModel.of(initialValue, elementDomain);

    assertThat(model.getValue()).containsExactly("A");
    assertThat(model.getCompositeDomain()).isEqualTo(Domain.nonNull());
    assertThat(model.getElementDomain()).isEqualTo(elementDomain);
  }

  @Test
  void ofAllParametersShouldInitializeCorrectly() {
    Domain<String> elementDomain = Domain.any();
    Domain<List<String>> compositeDomain = Domain.where(l -> l.size() <= 2);
    List<String> initialValue = List.of("A", "B");
    MultiChoiceModel<String> model = MultiChoiceModel.of(initialValue, compositeDomain, elementDomain);

    assertThat(model.getValue()).containsExactly("A", "B");
    assertThat(model.getCompositeDomain()).isEqualTo(compositeDomain);
    assertThat(model.getElementDomain()).isEqualTo(elementDomain);
  }
}
