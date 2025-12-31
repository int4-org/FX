package org.int4.fx.values.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link Observe}.
 */
public class ObserveTest {

  @Test
  void booleansAggregation() {
    SimpleBooleanProperty t1 = new SimpleBooleanProperty(true);
    SimpleBooleanProperty t2 = new SimpleBooleanProperty(false);
    SimpleBooleanProperty t3 = new SimpleBooleanProperty(true);

    ObservableValue<Boolean> allTrue = Observe.booleans(t1, t2, t3).allTrue();
    ObservableValue<Boolean> anyTrue = Observe.booleans(t1, t2, t3).anyTrue();
    ObservableValue<Boolean> allFalse = Observe.booleans(t1, t2, t3).allFalse();
    ObservableValue<Boolean> anyFalse = Observe.booleans(t1, t2, t3).anyFalse();

    assertThat(allTrue.getValue()).isFalse();
    assertThat(allFalse.getValue()).isFalse();
    assertThat(anyTrue.getValue()).isTrue();
    assertThat(anyFalse.getValue()).isTrue();

    t2.set(true);

    assertThat(allTrue.getValue()).isTrue();
    assertThat(anyFalse.getValue()).isFalse();
  }

  @Test
  void booleansListeners() {
    BooleanProperty t1 = new SimpleBooleanProperty(false);
    BooleanProperty t2 = new SimpleBooleanProperty(false);
    BooleanProperty t3 = new SimpleBooleanProperty(false);

    ObservableValue<Boolean> anyTrue = Observe.booleans(t1, t2, t3).anyTrue();

    List<String> changes = new ArrayList<>();

    anyTrue.addListener((obs, old, current) -> changes.add(old + " -> " + current));

    // flip one source to true -> anyTrue should change from false to true and listener invoked
    t2.set(true);

    assertThat(changes).containsExactly("false -> true");

    changes.clear();

    t3.set(true);

    assertThat(changes).isEmpty();

    t2.set(false);

    assertThat(changes).isEmpty();

    t3.set(false);

    assertThat(changes).containsExactly("true -> false");
  }

  @Test
  void booleansShouldRejectNullObservables() {
    assertThatThrownBy(() -> Observe.booleans((ObservableValue<Boolean>)null)).isInstanceOf(NullPointerException.class);
  }

  enum Case {
    VALUES_2(
      2,
      properties -> Observe.values(properties[0], properties[1]).map((a, b) -> a + ":" + b),
      properties -> Observe.values(properties[0], properties[1]).compute((a, b) -> a + ":" + b)
    ),
    VALUES_3(
      3,
      properties -> Observe.values(properties[0], properties[1], properties[2]).map((a, b, c) -> a + ":" + b + ":" + c),
      properties -> Observe.values(properties[0], properties[1], properties[2]).compute((a, b, c) -> a + ":" + b + ":" + c)
    ),
    VALUES_4(
      4,
      properties -> Observe.values(properties[0], properties[1], properties[2], properties[3]).map((a, b, c, d) -> a + ":" + b + ":" + c + ":" + d),
      properties -> Observe.values(properties[0], properties[1], properties[2], properties[3]).compute((a, b, c, d) -> a + ":" + b + ":" + c + ":" + d)
    );

    private final int count;
    private final Function<StringProperty[], ObservableValue<String>> mapFunction;
    private final Function<StringProperty[], ObservableValue<String>> computeFunction;

    Case(
      int count,
      Function<StringProperty[], ObservableValue<String>> mapFunction,
      Function<StringProperty[], ObservableValue<String>> computeFunction
    ) {
      this.count = count;
      this.mapFunction = mapFunction;
      this.computeFunction = computeFunction;
    }
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void shouldComputeCorrectValue(Case c) {
    StringProperty[] properties = new StringProperty[c.count];

    for(int i = 0; i < properties.length; i++) {
      properties[i] = new SimpleStringProperty();
    }

    ObservableValue<String> mapped = c.mapFunction.apply(properties).orElse("");
    ObservableValue<String> computed = c.computeFunction.apply(properties);

    for(int i = 0; i < properties.length; i++) {
      properties[i].set("x");

      String expected = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

      assertThat(mapped.getValue()).isEqualTo(expected.contains("null") ? "" : expected);
      assertThat(computed.getValue()).isEqualTo(expected);
    }

    for(int i = 0; i < properties.length; i++) {
      properties[i].set(null);

      String expected = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

      assertThat(mapped.getValue()).isEqualTo("");
      assertThat(computed.getValue()).isEqualTo(expected);
    }
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void listenersShouldReceiveCorrectValues(Case c) {
    StringProperty[] properties = new StringProperty[c.count];

    for(int i = 0; i < properties.length; i++) {
      properties[i] = new SimpleStringProperty();
    }

    ObservableValue<String> mapped = c.mapFunction.apply(properties).orElse("");
    ObservableValue<String> computed = c.computeFunction.apply(properties);

    List<String> results = new ArrayList<>();
    InvalidationListener invalidationListener = obs -> results.add("invalidated");

    mapped.addListener(invalidationListener);
    computed.addListener(invalidationListener);

    assertThat(results).isEmpty();

    properties[0].set("A");

    assertThat(results).containsExactly("invalidated", "invalidated");
    results.clear();

    properties[0].set("B");

    assertThat(results).isEmpty();

    ChangeListener<String> changeListener = (obs, old, current) -> results.add(old + " -> " + current);

    mapped.addListener(changeListener);
    computed.addListener(changeListener);

    assertThat(results).isEmpty();

    String before = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

    properties[0].set("C");

    String after = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

    assertThat(results).containsExactly("invalidated", "invalidated", before + " -> " + after);  // mapped does invalidate (as it has a change listener now) but shows no change (as there is a null element)
    results.clear();

    mapped.removeListener(invalidationListener);
    computed.removeListener(invalidationListener);

    properties[0].set("D");

    before = after;
    after = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

    assertThat(results).containsExactly(before + " -> " + after);
    results.clear();

    mapped.removeListener(changeListener);
    computed.removeListener(changeListener);

    properties[0].set("E");

    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void shouldRejectNullObservables(Case c) {
    StringProperty[] properties = new StringProperty[c.count];

    assertThatThrownBy(() -> c.mapFunction.apply(properties).orElse("")).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> c.computeFunction.apply(properties).orElse("")).isInstanceOf(NullPointerException.class);
  }
}