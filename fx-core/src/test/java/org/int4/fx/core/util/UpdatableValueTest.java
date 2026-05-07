package org.int4.fx.core.util;

import java.util.function.BiConsumer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class UpdatableValueTest {
  enum Case {

    VALUES_1(
      1,
      (instances, values) -> UpdatableValue.set(instances[0], values[0])
    ),

    VALUES_2(
      2,
      (instances, values) -> UpdatableValue.set(
        instances[0], values[0],
        instances[1], values[1]
      )
    ),

    VALUES_3(
      3,
      (instances, values) -> UpdatableValue.set(
        instances[0], values[0],
        instances[1], values[1],
        instances[2], values[2]
      )
    ),

    VALUES_4(
      4,
      (instances, values) -> UpdatableValue.set(
        instances[0], values[0],
        instances[1], values[1],
        instances[2], values[2],
        instances[3], values[3]
      )
    );

    final int count;
    final BiConsumer<UpdatableValue<String>[], String[]> setter;

    Case(int count, BiConsumer<UpdatableValue<String>[], String[]> setter) {
      this.count = count;
      this.setter = setter;
    }
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void shouldNeverExposeInconsistentStateDuringNotification(Case testCase) {
    @SuppressWarnings("unchecked")
    UpdatableValue<String>[] values = new UpdatableValue[testCase.count];

    for(int i = 0; i < testCase.count; i++) {
      values[i] = UpdatableValue.of();
    }

    String[] expected = new String[testCase.count];

    for(int i = 0; i < testCase.count; i++) {
      expected[i] = "V" + i;
    }

    boolean[] observedConsistencyViolation = {false};

    for(int i = 0; i < testCase.count; i++) {
      values[i].asObservableValue().addListener((obs, oldVal, newVal) -> {
        for(int j = 0; j < testCase.count; j++) {
          String actual = values[j].getValue();
          String expectedValue = expected[j];

          if(!expectedValue.equals(actual)) {
            observedConsistencyViolation[0] = true;
          }
        }
      });
    }

    testCase.setter.accept(values, expected);

    assertThat(observedConsistencyViolation[0])
      .as("listeners should never observe partially updated state")
      .isFalse();
  }
}