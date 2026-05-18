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
  void shouldOnlyFireAtEndOfBatch(Case testCase) {
    @SuppressWarnings("unchecked")
    UpdatableValue<String>[] values = new UpdatableValue[testCase.count];

    for(int i = 0; i < testCase.count; i++) {
      values[i] = UpdatableValue.of();
    }

    String[] expected = new String[testCase.count];
    int[] fireCount = {0};

    for(int i = 0; i < testCase.count; i++) {
      expected[i] = "V" + i;
      values[i].asObservableValue().addListener((obs, old, next) -> fireCount[0]++);
    }

    UpdatableValue.batch(() -> {
      testCase.setter.accept(values, expected);

      assertThat(fireCount[0]).isEqualTo(0); // No fires during batch
    });

    // Each property fires exactly once, regardless of the setter count
    assertThat(fireCount[0]).isEqualTo(testCase.count);
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void shouldSupportNestedBatches(Case testCase) {
    @SuppressWarnings("unchecked")
    UpdatableValue<String>[] values = new UpdatableValue[testCase.count];
    int[] fireCount = {0};

    for(int i = 0; i < testCase.count; i++) {
      values[i] = UpdatableValue.of();
      values[i].asObservableValue().addListener((obs, old, next) -> fireCount[0]++);
    }

    UpdatableValue.batch(() -> {
      UpdatableValue.set(values[0], "1");

      UpdatableValue.batch(() -> {
        UpdatableValue.set(values[0], "2");
      });

      assertThat(fireCount[0]).isEqualTo(0); // Nested batch doesn't trigger early
    });

    assertThat(fireCount[0]).isEqualTo(1); // Only fired at end of outer batch
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void shouldFireEvenOnException(Case testCase) {
    @SuppressWarnings("unchecked")
    UpdatableValue<String>[] values = new UpdatableValue[testCase.count];
    int[] fireCount = {0};

    for(int i = 0; i < testCase.count; i++) {
      values[i] = UpdatableValue.of();
      values[i].asObservableValue().addListener((obs, old, next) -> fireCount[0]++);
    }

    try {
      UpdatableValue.batch(() -> {
        UpdatableValue.set(values[0], "ErrorValue");

        throw new RuntimeException("Oops");
      });
    }
    catch(RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("Oops");
    }

    assertThat(fireCount[0]).isEqualTo(1); // Still fired despite exception
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