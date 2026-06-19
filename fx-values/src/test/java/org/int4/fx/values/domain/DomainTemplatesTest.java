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
    assertThat(DomainTemplates.INAPPLICABLE.key()).isEqualTo("domain.inapplicable");
    assertThat(DomainTemplates.MISSING.key()).isEqualTo("domain.missing");
    assertThat(DomainTemplates.outOfRange(0, 10).key()).isEqualTo("domain.outOfRange");
    assertThat(DomainTemplates.misaligned(0, 2).key()).isEqualTo("domain.misaligned");
    assertThat(DomainTemplates.notContained(List.of("A")).key()).isEqualTo("domain.notContained");
    assertThat(DomainTemplates.noMatch(".*").key()).isEqualTo("domain.noMatch");
    assertThat(DomainTemplates.INVALID.key()).isEqualTo("domain.invalid");
  }

  @Test
  void argsShouldBeExtractedFromRecords() {
    Map<String, Object> rangeArgs = DomainTemplates.outOfRange(5, 15).args();

    assertThat(rangeArgs).containsOnly(entry("min", 5), entry("max", 15));

    Map<String, Object> misalignedArgs = DomainTemplates.misaligned(1, 3).args();

    assertThat(misalignedArgs).containsOnly(entry("min", 1), entry("step", 3));

    List<String> items = List.of("A", "B");
    Map<String, Object> notContainedArgs = DomainTemplates.notContained(items).args();

    assertThat(notContainedArgs).containsOnly(entry("items", items));

    Map<String, Object> noMatchArgs = DomainTemplates.noMatch("[a-z]").args();

    assertThat(noMatchArgs).containsOnly(entry("regex", "[a-z]"));

    assertThat(DomainTemplates.MISSING.args()).isEmpty();
  }

  @Test
  void argsShouldHaveFixedIterationOrder() {
    Map<String, Object> args = DomainTemplates.outOfRange(1, 2).args();

    assertThat(args.keySet()).containsExactly("min", "max");
  }

  @Test
  void shouldEnforceNonNullConstraints() {
    assertThatThrownBy(() -> DomainTemplates.outOfRange(null, 10)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainTemplates.outOfRange(10, null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainTemplates.misaligned(null, 1)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainTemplates.misaligned(1, null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainTemplates.notContained(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainTemplates.noMatch(null)).isInstanceOf(NullPointerException.class);
  }
}
