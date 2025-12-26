package org.int4.fx.values.domain;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DomainTest {

  @Test
  void validateIntegerBoundedDomain() {
    assertThatThrownBy(() -> Domain.bounded(0, -1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("min must not exceed max");

    Domain<Integer> domain = Domain.bounded(0, 5);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(-1)).isFalse();
    assertThat(domain.contains(0)).isTrue();
    assertThat(domain.contains(5)).isTrue();
    assertThat(domain.contains(6)).isFalse();

    IndexedView<Integer> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly(0, 1, 2, 3, 4, 5);
    assertThat(iv.size()).isEqualTo(6);
    assertThatThrownBy(() -> iv.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> iv.get(6)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThat(iv.get(0)).isEqualTo(0);
    assertThat(iv.get(5)).isEqualTo(5);
    assertThat(iv.indexOf(-1)).isEqualTo(0);
    assertThat(iv.indexOf(2)).isEqualTo(2);
    assertThat(iv.indexOf(6)).isEqualTo(5);
    assertThat(iv.indexOf(null)).isEqualTo(-1);

    StepperView<Integer> sv = domain.requireView(StepperView.class);

    assertThat(sv.step(2, 0)).isEqualTo(2);
    assertThat(sv.step(2, 2)).isEqualTo(4);
    assertThat(sv.step(2, -1)).isEqualTo(1);

    // assert snapping behavior:
    assertThat(sv.step(-1, 0)).isEqualTo(-1);
    assertThat(sv.step(-1, 1)).isEqualTo(0);
    assertThat(sv.step(-1, -1)).isEqualTo(0);

    // assert null behavior:
    assertThat(sv.step(null, -2)).isNull();
    assertThat(sv.step(null, 0)).isNull();
    assertThat(sv.step(null, 2)).isNull();
  }

  @Test
  void validateIntegerBoundedDomainWithStep() {
    assertThatThrownBy(() -> Domain.bounded(0, -1, 1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("min must not exceed max");

    assertThatThrownBy(() -> Domain.bounded(0, 10, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("step must be positive");

    assertThatThrownBy(() -> Domain.bounded(0, 10, 3))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("max not aligned to step");

    Domain<Integer> domain = Domain.bounded(0, 10, 2);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(-1)).isFalse();
    assertThat(domain.contains(0)).isTrue();
    assertThat(domain.contains(5)).isFalse();
    assertThat(domain.contains(10)).isTrue();
    assertThat(domain.contains(11)).isFalse();

    IndexedView<Integer> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly(0, 2, 4, 6, 8, 10);
    assertThat(iv.size()).isEqualTo(6);
    assertThatThrownBy(() -> iv.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> iv.get(6)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThat(iv.get(0)).isEqualTo(0);
    assertThat(iv.get(5)).isEqualTo(10);
    assertThat(iv.indexOf(-1)).isEqualTo(0);
    assertThat(iv.indexOf(2)).isEqualTo(1);
    assertThat(iv.indexOf(6)).isEqualTo(3);
    assertThat(iv.indexOf(11)).isEqualTo(5);
    assertThat(iv.indexOf(null)).isEqualTo(-1);

    StepperView<Integer> sv = domain.requireView(StepperView.class);

    assertThat(sv.step(2, 0)).isEqualTo(2);
    assertThat(sv.step(2, 2)).isEqualTo(6);
    assertThat(sv.step(2, -1)).isEqualTo(0);

    // assert snapping behavior:
    assertThat(sv.step(-1, 0)).isEqualTo(-1);
    assertThat(sv.step(-1, 1)).isEqualTo(0);
    assertThat(sv.step(-1, -1)).isEqualTo(0);

    // assert null behavior:
    assertThat(sv.step(null, -2)).isNull();
    assertThat(sv.step(null, 0)).isNull();
    assertThat(sv.step(null, 2)).isNull();
  }

  @Test
  void validateLongBoundedDomain() {
    assertThatThrownBy(() -> Domain.bounded(0L, -1L))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("min must not exceed max");

    Domain<Long> domain = Domain.bounded(0L, 5L);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(-1L)).isFalse();
    assertThat(domain.contains(0L)).isTrue();
    assertThat(domain.contains(5L)).isTrue();
    assertThat(domain.contains(6L)).isFalse();

    IndexedView<Long> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly(0L, 1L, 2L, 3L, 4L, 5L);
    assertThat(iv.size()).isEqualTo(6);
    assertThatThrownBy(() -> iv.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> iv.get(6)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThat(iv.get(0)).isEqualTo(0);
    assertThat(iv.get(5)).isEqualTo(5);
    assertThat(iv.indexOf(-1L)).isEqualTo(0);
    assertThat(iv.indexOf(2L)).isEqualTo(2);
    assertThat(iv.indexOf(6L)).isEqualTo(5);
    assertThat(iv.indexOf(null)).isEqualTo(-1);

    StepperView<Long> sv = domain.requireView(StepperView.class);

    assertThat(sv.step(2L, 0)).isEqualTo(2);
    assertThat(sv.step(2L, 2)).isEqualTo(4);
    assertThat(sv.step(2L, -1)).isEqualTo(1);

    // assert snapping behavior:
    assertThat(sv.step(-1L, 0)).isEqualTo(-1);
    assertThat(sv.step(-1L, 1)).isEqualTo(0);
    assertThat(sv.step(-1L, -1)).isEqualTo(0);

    // assert null behavior:
    assertThat(sv.step(null, -2)).isNull();
    assertThat(sv.step(null, 0)).isNull();
    assertThat(sv.step(null, 2)).isNull();
  }

  @Test
  void validateLongBoundedDomainWithStep() {
    assertThatThrownBy(() -> Domain.bounded(0L, -1L, 1L))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("min must not exceed max");

    assertThatThrownBy(() -> Domain.bounded(0L, 10L, 0L))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("step must be positive");

    assertThatThrownBy(() -> Domain.bounded(0L, 10L, 3L))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("max not aligned to step");

    Domain<Long> domain = Domain.bounded(0L, 10L, 2L);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(-1L)).isFalse();
    assertThat(domain.contains(0L)).isTrue();
    assertThat(domain.contains(5L)).isFalse();
    assertThat(domain.contains(10L)).isTrue();
    assertThat(domain.contains(11L)).isFalse();

    IndexedView<Long> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly(0L, 2L, 4L, 6L, 8L, 10L);
    assertThat(iv.size()).isEqualTo(6);
    assertThatThrownBy(() -> iv.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> iv.get(6)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThat(iv.get(0)).isEqualTo(0);
    assertThat(iv.get(5)).isEqualTo(10);
    assertThat(iv.indexOf(-1L)).isEqualTo(0);
    assertThat(iv.indexOf(2L)).isEqualTo(1);
    assertThat(iv.indexOf(6L)).isEqualTo(3);
    assertThat(iv.indexOf(11L)).isEqualTo(5);
    assertThat(iv.indexOf(null)).isEqualTo(-1);

    StepperView<Long> sv = domain.requireView(StepperView.class);

    assertThat(sv.step(2L, 0)).isEqualTo(2);
    assertThat(sv.step(2L, 2)).isEqualTo(6);
    assertThat(sv.step(2L, -1)).isEqualTo(0);

    // assert snapping behavior:
    assertThat(sv.step(-1L, 0)).isEqualTo(-1);
    assertThat(sv.step(-1L, 1)).isEqualTo(0);
    assertThat(sv.step(-1L, -1)).isEqualTo(0);

    // assert null behavior:
    assertThat(sv.step(null, -2)).isNull();
    assertThat(sv.step(null, 0)).isNull();
    assertThat(sv.step(null, 2)).isNull();
  }

  @Test
  void validateDoubleBoundedDomainWithStep() {
    assertThatThrownBy(() -> Domain.bounded(0.0, -1.0, 0.1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("min must not exceed max");

    assertThatThrownBy(() -> Domain.bounded(0.0, 10.0, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("step must be positive and finite");

    Domain<Double> domain = Domain.bounded(0.0, 1.0, 0.25);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(-1.0)).isFalse();
    assertThat(domain.contains(0.0)).isTrue();
    assertThat(domain.contains(0.4)).isFalse();
    assertThat(domain.contains(0.5)).isTrue();
    assertThat(domain.contains(1.0)).isTrue();
    assertThat(domain.contains(1.5)).isFalse();

    IndexedView<Double> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly(0.0, 0.25, 0.5, 0.75, 1.0);
    assertThat(iv.size()).isEqualTo(5);
    assertThatThrownBy(() -> iv.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> iv.get(5)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThat(iv.get(0)).isEqualTo(0);
    assertThat(iv.get(4)).isEqualTo(1);
    assertThat(iv.indexOf(-0.5)).isEqualTo(0);
    assertThat(iv.indexOf(0.0)).isEqualTo(0);
    assertThat(iv.indexOf(0.5)).isEqualTo(2);
    assertThat(iv.indexOf(1.0)).isEqualTo(4);
    assertThat(iv.indexOf(1.5)).isEqualTo(4);
    assertThat(iv.indexOf(null)).isEqualTo(-1);

    StepperView<Double> sv = domain.requireView(StepperView.class);

    assertThat(sv.step(0.5, 0)).isEqualTo(0.5);
    assertThat(sv.step(0.5, 2)).isEqualTo(1.0);
    assertThat(sv.step(0.5, -1)).isEqualTo(0.25);

    // assert snapping behavior:
    assertThat(sv.step(-1.0, 0)).isEqualTo(-1);
    assertThat(sv.step(-1.0, 1)).isEqualTo(0);
    assertThat(sv.step(-1.0, -1)).isEqualTo(0);

    // assert null behavior:
    assertThat(sv.step(null, -2)).isNull();
    assertThat(sv.step(null, 0)).isNull();
    assertThat(sv.step(null, 2)).isNull();
  }

  @Test
  void validateContinuousDomain() {
    assertThatThrownBy(() -> Domain.continuous(0.0, -1.0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("min must not exceed max");

    Domain<Double> domain = Domain.continuous(0.0, 10.0);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(-1.0)).isFalse();
    assertThat(domain.contains(0.0)).isTrue();
    assertThat(domain.contains(0.4)).isTrue();
    assertThat(domain.contains(0.5)).isTrue();
    assertThat(domain.contains(1.0)).isTrue();
    assertThat(domain.contains(10.0)).isTrue();
    assertThat(domain.contains(10.01)).isFalse();

    ContinuousView<Double> cv = domain.requireView(ContinuousView.class);

    assertThat(cv.get(-0.5)).isEqualTo(0.0);
    assertThat(cv.get(0.0)).isEqualTo(0.0);
    assertThat(cv.get(0.5)).isEqualTo(5.0);
    assertThat(cv.get(1.0)).isEqualTo(10.0);
    assertThat(cv.get(1.5)).isEqualTo(10.0);
    assertThat(cv.fractionOf(-0.5)).isEqualTo(0.0);
    assertThat(cv.fractionOf(0.0)).isEqualTo(0.0);
    assertThat(cv.fractionOf(5.0)).isEqualTo(0.5);
    assertThat(cv.fractionOf(10.0)).isEqualTo(1.0);
    assertThat(cv.fractionOf(10.5)).isEqualTo(1.0);
    assertThat(cv.fractionOf(null)).isEqualTo(0.0);
  }

  @Test
  void validateAnyDomain() {
    Domain<String> domain = Domain.any();

    assertThat(domain.allowsNull()).isTrue();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isTrue();
    assertThat(domain.contains("anything")).isTrue();
  }

  @Test
  void validateNonNullDomain() {
    Domain<String> domain = Domain.nonNull();

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains("x")).isTrue();
  }

  @Test
  void validateEmptyDomain() {
    Domain<String> domain = Domain.empty();

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isTrue();
    assertThat(domain.isNotEmpty()).isFalse();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains("x")).isFalse();
  }

  @Test
  void validateOfListDomain() {
    assertThatThrownBy(() -> Domain.of((List<?>)null)).isInstanceOf(NullPointerException.class);

    Domain<String> domain = Domain.of(Arrays.asList("a", "b", "c"));

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains("c")).isTrue();
    assertThat(domain.contains("d")).isFalse();

    IndexedView<String> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly("a", "b", "c");
    assertThat(iv.size()).isEqualTo(3);
    assertThat(iv.get(0)).isEqualTo("a");
    assertThat(iv.indexOf("b")).isEqualTo(1);
    assertThat(iv.indexOf("d")).isEqualTo(-1);
    assertThat(iv.indexOf(null)).isEqualTo(-1);
  }

  @Test
  void validateOfElementsDomain() {
    Domain<Integer> domain = Domain.of(1, 2, 3);

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.contains(2)).isTrue();
    assertThat(domain.contains(4)).isFalse();

    IndexedView<Integer> iv = domain.requireView(IndexedView.class);

    assertThat(iv.asList()).containsExactly(1, 2, 3);
    assertThat(iv.size()).isEqualTo(3);
    assertThat(iv.get(0)).isEqualTo(1);
    assertThat(iv.indexOf(0)).isEqualTo(-1);
    assertThat(iv.indexOf(1)).isEqualTo(0);
    assertThat(iv.indexOf(2)).isEqualTo(1);
    assertThat(iv.indexOf(3)).isEqualTo(2);
    assertThat(iv.indexOf(4)).isEqualTo(-1);
    assertThat(iv.indexOf(null)).isEqualTo(-1);
  }

  @Test
  void validateOfPredicateDomain() {
    Domain<String> domain = Domain.of(v -> v.startsWith("x"));

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains("x1")).isTrue();
    assertThat(domain.contains("y")).isFalse();
    assertThat(domain.contains(null)).isFalse();
  }

  @Test
  void validateRegexDomain() {
    assertThatThrownBy(() -> Domain.regex(null)).isInstanceOf(NullPointerException.class);

    Domain<String> domain = Domain.regex("\\d+");

    assertThat(domain.allowsNull()).isFalse();
    assertThat(domain.isEmpty()).isFalse();
    assertThat(domain.isNotEmpty()).isTrue();
    assertThat(domain.contains("123")).isTrue();
    assertThat(domain.contains("a12")).isFalse();
    assertThat(domain.contains(null)).isFalse();
  }

  @Nested
  class ComparableBoundedDomain {

    {
      assertThatThrownBy(() -> Domain.bounded("a", null)).isInstanceOf(NullPointerException.class);
      assertThatThrownBy(() -> Domain.bounded(null, "d")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void notNullable() {
      Domain<String> domain = Domain.bounded("a", "d");

      assertThat(domain.allowsNull()).isFalse();
      assertThat(domain.isEmpty()).isFalse();
      assertThat(domain.isNotEmpty()).isTrue();
      assertThat(domain.contains("a")).isTrue();
      assertThat(domain.contains("e")).isFalse();
      assertThat(domain.contains(null)).isFalse();

      NormalizedView<String> nv = domain.requireView(NormalizedView.class);

      assertThat(nv.snap("z")).isEqualTo("d");
      assertThat(nv.snap("`")).isEqualTo("a");
      assertThat(nv.snap(null)).isEqualTo("a");
    }

    @Test
    void nullable() {
      Domain<String> domain = Domain.bounded("a", "d").nullable();

      assertThat(domain.allowsNull()).isTrue();
      assertThat(domain.isEmpty()).isFalse();
      assertThat(domain.isNotEmpty()).isTrue();
      assertThat(domain.contains("a")).isTrue();
      assertThat(domain.contains("e")).isFalse();
      assertThat(domain.contains(null)).isTrue();

      NormalizedView<String> nv = domain.requireView(NormalizedView.class);

      assertThat(nv.snap("z")).isEqualTo("d");
      assertThat(nv.snap("`")).isEqualTo("a");
      assertThat(nv.snap(null)).isEqualTo(null);
    }
  }
}