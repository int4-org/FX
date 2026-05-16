package org.int4.fx.core.util;

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
import javafx.util.Subscription;

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
      properties -> Observe.values(properties[0], properties[1]).compute((a, b) -> a + ":" + b),
      (properties, consumer) -> Observe.values(properties[0], properties[1]).subscribe((a, b) -> consumer.accept(a + ":" + b)),
      (properties, runnable) -> Observe.values(properties[0], properties[1]).subscribe(runnable)
    ),
    VALUES_3(
      3,
      properties -> Observe.values(properties[0], properties[1], properties[2]).map((a, b, c) -> a + ":" + b + ":" + c),
      properties -> Observe.values(properties[0], properties[1], properties[2]).compute((a, b, c) -> a + ":" + b + ":" + c),
      (properties, consumer) -> Observe.values(properties[0], properties[1], properties[2]).subscribe((a, b, c) -> consumer.accept(a + ":" + b + ":" + c)),
      (properties, runnable) -> Observe.values(properties[0], properties[1], properties[2]).subscribe(runnable)
    ),
    VALUES_4(
      4,
      properties -> Observe.values(properties[0], properties[1], properties[2], properties[3]).map((a, b, c, d) -> a + ":" + b + ":" + c + ":" + d),
      properties -> Observe.values(properties[0], properties[1], properties[2], properties[3]).compute((a, b, c, d) -> a + ":" + b + ":" + c + ":" + d),
      (properties, consumer) -> Observe.values(properties[0], properties[1], properties[2], properties[3]).subscribe((a, b, c, d) -> consumer.accept(a + ":" + b + ":" + c + ":" + d)),
      (properties, runnable) -> Observe.values(properties[0], properties[1], properties[2], properties[3]).subscribe(runnable)
    );

    private final int count;
    private final Function<StringProperty[], ObservableValue<String>> mapFunction;
    private final Function<StringProperty[], ObservableValue<String>> computeFunction;
    private final SubscribeFunction subscribeFunction;
    private final InvalidationSubscribeFunction invalidationSubscribeFunction;

    Case(
      int count,
      Function<StringProperty[], ObservableValue<String>> mapFunction,
      Function<StringProperty[], ObservableValue<String>> computeFunction,
      SubscribeFunction subscribeFunction,
      InvalidationSubscribeFunction invalidationSubscribeFunction
    ) {
      this.count = count;
      this.mapFunction = mapFunction;
      this.computeFunction = computeFunction;
      this.subscribeFunction = subscribeFunction;
      this.invalidationSubscribeFunction = invalidationSubscribeFunction;
    }
  }

  interface SubscribeFunction {
    Subscription apply(StringProperty[] properties, java.util.function.Consumer<String> results);
  }

  interface InvalidationSubscribeFunction {
    Subscription apply(StringProperty[] properties, Runnable runnable);
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void subscribeShouldBeLazyAndTransactional(Case c) {
    StringProperty[] properties = new StringProperty[c.count];

    for(int i = 0; i < c.count; i++) {
      properties[i] = new SimpleStringProperty("V" + i);
    }

    int[] counter = {0};
    Subscription sub = c.invalidationSubscribeFunction.apply(properties, () -> counter[0]++);

    assertThat(counter[0]).isEqualTo(0);

    // Test when dependencies change:
    for(int i = 0; i < c.count; i++) {
      int before = counter[0];
      properties[i].set("New" + i);

      assertThat(counter[0]).isEqualTo(before + 1);
    }

    sub.unsubscribe();

    for(int i = 0; i < c.count; i++) {
      int before = counter[0];
      properties[i].set("Cancelled");

      assertThat(counter[0]).isEqualTo(before);
    }
  }

  @ParameterizedTest
  @EnumSource(Case.class)
  void subscribeShouldBeLazyAndFireOncePerDependencyUntilRevalidated(Case c) {
    StringProperty[] properties = new StringProperty[c.count];

    for(int i = 0; i < c.count; i++) {
      properties[i] = new SimpleStringProperty("V" + i);
    }

    int[] counter = {0};
    Subscription sub = c.invalidationSubscribeFunction.apply(properties, () -> counter[0]++);

    assertThat(counter[0]).isEqualTo(0); // Lazy initial state

    // 1. Change first property
    properties[0].set("New 0");
    assertThat(counter[0]).isEqualTo(1);

    // 2. Change first property again without revalidating -> should NOT fire
    properties[0].set("New 0 - again");
    assertThat(counter[0]).isEqualTo(1);

    // 3. Change second property (if it exists) -> should fire independently
    if(c.count > 1) {
      properties[1].set("New 1");
      assertThat(counter[0]).isEqualTo(2);

      // 4. Change second again -> should NOT fire
      properties[1].set("New 1 - again");
      assertThat(counter[0]).isEqualTo(2);
    }

    // 5. Revalidate first property
    properties[0].getValue();

    // 6. Change first property again -> should fire again
    properties[0].set("New 0 - third");
    assertThat(counter[0]).isEqualTo(c.count > 1 ? 3 : 2);

    sub.unsubscribe();
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

  @ParameterizedTest
  @EnumSource(Case.class)
  void subscribeShouldBeEagerAndTransactional(Case c) {
    StringProperty[] properties = new StringProperty[c.count];

    for(int i = 0; i < c.count; i++) {
      properties[i] = new SimpleStringProperty("V" + i);
    }

    List<String> results = new ArrayList<>();
    Subscription sub = c.subscribeFunction.apply(properties, results::add);

    String initialExpected = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

    assertThat(results).containsExactly(initialExpected);

    // Test when dependencies change:
    for(int i = 0; i < c.count; i++) {
      results.clear();
      properties[i].set("New");

      String updateExpected = Arrays.stream(properties).map(StringProperty::get).collect(Collectors.joining(":"));

      assertThat(results).containsExactly(updateExpected);
    }

    results.clear();

    // Test when dependencies don't change:
    for(int i = 0; i < c.count; i++) {
      properties[i].set(properties[i].get());

      assertThat(results).isEmpty();
    }

    results.clear();

    sub.unsubscribe();

    for(int i = 0; i < c.count; i++) {
      properties[i].set("Cancelled");

      assertThat(results).isEmpty();
    }
  }

  @Test
  void shouldAvoidRedundantRecomputations() {
    StringProperty s1 = new SimpleStringProperty("A");
    StringProperty s2 = new SimpleStringProperty("B");
    int[] counter = {0};

    ObservableValue<String> combined = Observe.values(s1, s2).compute((v1, v2) -> {
      counter[0]++;

      return v1 + ":" + v2;
    });

    assertThat(combined.getValue()).isEqualTo("A:B");
    assertThat(counter[0]).isEqualTo(1);

    assertThat(combined.getValue()).isEqualTo("A:B");
    assertThat(counter[0]).isEqualTo(1); // Doesn't recompute as dependencies didn't change

    s1.set("D");

    assertThat(combined.getValue()).isEqualTo("D:B");
    assertThat(counter[0]).isEqualTo(2);

    s2.set("B");

    assertThat(combined.getValue()).isEqualTo("D:B");
    assertThat(counter[0]).isEqualTo(2);

    s1.set("A");

    assertThat(combined.getValue()).isEqualTo("A:B");
    assertThat(counter[0]).isEqualTo(3);

    List<String> listenerOutputs = new ArrayList<>();

    combined.addListener((obs, old, cur) -> listenerOutputs.add(old + " -> " + cur)); // Start observing

    assertThat(counter[0]).isEqualTo(3);
    assertThat(listenerOutputs).isEmpty();

    listenerOutputs.clear();
    s1.set("A"); // No real change

    assertThat(counter[0]).isEqualTo(3);
    assertThat(listenerOutputs).isEmpty();

    listenerOutputs.clear();
    s1.set("C");

    assertThat(counter[0]).isEqualTo(4);
    assertThat(listenerOutputs).containsExactly("A:B -> C:B");

    listenerOutputs.clear();
    s2.set("C");

    assertThat(counter[0]).isEqualTo(5);
    assertThat(listenerOutputs).containsExactly("C:B -> C:C");

    listenerOutputs.clear();
    s2.set("C"); // No change

    assertThat(counter[0]).isEqualTo(5);
    assertThat(listenerOutputs).isEmpty();
  }
}
