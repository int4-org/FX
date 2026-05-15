package org.int4.fx.values.domain;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

class DomainTemplatesTest {

  @Test
  void keysShouldBeCorrect() {
    assertThat(new DomainTemplates.Inapplicable().key()).isEqualTo("domain.inapplicable");
    assertThat(new DomainTemplates.Missing().key()).isEqualTo("domain.missing");
    assertThat(new DomainTemplates.OutOfRange<>(0, 10).key()).isEqualTo("domain.outOfRange");
    assertThat(new DomainTemplates.Misaligned<>(0, 2).key()).isEqualTo("domain.misaligned");
    assertThat(new DomainTemplates.NotContained<>(List.of("A")).key()).isEqualTo("domain.notContained");
    assertThat(new DomainTemplates.NoMatch(".*").key()).isEqualTo("domain.noMatch");
    assertThat(new DomainTemplates.Invalid().key()).isEqualTo("domain.invalid");
  }

  @Test
  void argsShouldBeExtractedFromRecords() {
    Map<String, Object> rangeArgs = new DomainTemplates.OutOfRange<>(5, 15).args();
    assertThat(rangeArgs).containsOnly(entry("min", 5), entry("max", 15));

    Map<String, Object> misalignedArgs = new DomainTemplates.Misaligned<>(1, 3).args();
    assertThat(misalignedArgs).containsOnly(entry("min", 1), entry("step", 3));

    List<String> items = List.of("A", "B");
    Map<String, Object> notContainedArgs = new DomainTemplates.NotContained<>(items).args();
    assertThat(notContainedArgs).containsOnly(entry("items", items));

    Map<String, Object> noMatchArgs = new DomainTemplates.NoMatch("[a-z]").args();
    assertThat(noMatchArgs).containsOnly(entry("regex", "[a-z]"));

    assertThat(new DomainTemplates.Missing().args()).isEmpty();
  }

  @Test
  void argsShouldHaveFixedIterationOrder() {
    Map<String, Object> args = new DomainTemplates.OutOfRange<>(1, 2).args();
    assertThat(args.keySet()).containsExactly("min", "max");
  }

  @Test
  void shouldEnforceNonNullConstraints() {
    assertThatThrownBy(() -> new DomainTemplates.OutOfRange<>(null, 10)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new DomainTemplates.OutOfRange<>(10, null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new DomainTemplates.Misaligned<>(null, 1)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new DomainTemplates.Misaligned<>(1, null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new DomainTemplates.NotContained<>(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new DomainTemplates.NoMatch(null)).isInstanceOf(NullPointerException.class);
  }
}
