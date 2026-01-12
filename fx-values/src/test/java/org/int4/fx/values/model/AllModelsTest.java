package org.int4.fx.values.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.int4.fx.values.domain.Domain;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AllModelsTest {

  static class Models implements ArgumentsProvider {
    @SuppressWarnings("unchecked")
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
        Arguments.of(new Case<>(
          BooleanModel.class,
          true,
          Domain.of(false, true),
          Domain.of(false),
          true,
          false,
          null,
          SimpleBooleanModel::new,
          BooleanModel::get,
          BooleanModel::set,
          BooleanModel::isNull
        )),
        Arguments.of(new Case<>(
          BooleanModel.class,
          true,
          Domain.of(null, false, true),
          Domain.of(null, false),
          true,
          false,
          null,
          SimpleBooleanModel::new,
          BooleanModel::get,
          BooleanModel::set,
          BooleanModel::isNull
        )),
        Arguments.of(new Case<>(
          IntegerModel.class,
          true,
          Domain.bounded(0, 12),
          Domain.bounded(5, 15),
          2,
          10,
          100,
          SimpleIntegerModel::new,
          IntegerModel::get,
          IntegerModel::set,
          IntegerModel::isNull
        )),
        Arguments.of(new Case<>(
          IntegerModel.class,
          true,
          Domain.bounded(0, 12).nullable(),
          Domain.bounded(5, 15).nullable(),
          2,
          10,
          100,
          SimpleIntegerModel::new,
          IntegerModel::get,
          IntegerModel::set,
          IntegerModel::isNull
        )),
        Arguments.of(new Case<>(
          LongModel.class,
          true,
          Domain.bounded(0L, 12),
          Domain.bounded(5L, 15),
          2L,
          10L,
          100L,
          SimpleLongModel::new,
          LongModel::get,
          LongModel::set,
          LongModel::isNull
        )),
        Arguments.of(new Case<>(
          LongModel.class,
          true,
          Domain.bounded(0L, 12).nullable(),
          Domain.bounded(5L, 15).nullable(),
          2L,
          10L,
          100L,
          SimpleLongModel::new,
          LongModel::get,
          LongModel::set,
          LongModel::isNull
        )),
        Arguments.of(new Case<>(
          DoubleModel.class,
          true,
          Domain.bounded(0, 12, 0.5),
          Domain.bounded(5, 15, 0.5),
          2.5,
          10.5,
          100.5,
          SimpleDoubleModel::new,
          DoubleModel::get,
          DoubleModel::set,
          DoubleModel::isNull
        )),
        Arguments.of(new Case<>(
          DoubleModel.class,
          true,
          Domain.bounded(0, 12, 0.5).nullable(),
          Domain.bounded(5, 15, 0.5).nullable(),
          2.5,
          10.5,
          100.5,
          SimpleDoubleModel::new,
          DoubleModel::get,
          DoubleModel::set,
          DoubleModel::isNull
        )),
        Arguments.of(new Case<>(
          (Class<ObjectModel<String>>)(Object)ObjectModel.class,
          false,
          Domain.of("A", "B", "C"),
          Domain.of("C", "D", "E"),
          "A",
          "C",
          "-",
          SimpleObjectModel::new,
          ObjectModel::get,
          ObjectModel::set,
          m -> m.get() == null
        )),
        Arguments.of(new Case<>(
          (Class<ObjectModel<String>>)(Object)ObjectModel.class,
          false,
          Domain.of(null, "A", "B", "C"),
          Domain.of(null, "C", "D", "E"),
          "A",
          "C",
          "-",
          SimpleObjectModel::new,
          ObjectModel::get,
          ObjectModel::set,
          m -> m.get() == null
        )),
        Arguments.of(new Case<>(
          (Class<ChoiceModel<String>>)(Object)ChoiceModel.class,
          false,
          Domain.<String>of("A", "B", "C"),
          Domain.<String>of("C", "D", "E"),
          "A",
          "C",
          "-",
          SimpleChoiceModel::new,
          ChoiceModel::get,
          ChoiceModel::set,
          m -> m.get() == null
        )),
        Arguments.of(new Case<>(
          (Class<ChoiceModel<String>>)(Object)ChoiceModel.class,
          false,
          Domain.<String>of(null, "A", "B", "C"),
          Domain.<String>of(null, "C", "D", "E"),
          "A",
          "C",
          "-",
          SimpleChoiceModel::new,
          ChoiceModel::get,
          ChoiceModel::set,
          m -> m.get() == null
        )),
        Arguments.of(new Case<>(
          StringModel.class,
          false,
          Domain.regex("[a-p]{1}"),
          Domain.regex("[k-z]{1}"),
          "a",
          "n",
          "-",
          SimpleStringModel::new,
          StringModel::get,
          StringModel::set,
          m -> m.get() == null
        ))
      );
    }
  }

  @ParameterizedTest
  @ArgumentsSource(Models.class)
  <M extends ValueModel<T>, T, D extends Domain<T>> void shouldCreateModelWithValidValue(Case<M, T, D> c) {
    M m = c.creator.apply(c.validInDomain1, c.domain1);

    assertThat(c.get(m)).isEqualTo(c.validInDomain1);
    assertThat(m.getValue()).isEqualTo(c.validInDomain1);
    assertThat(m.getRawValue()).isEqualTo(c.validInDomain1);
    assertThat(m.getDomain()).isEqualTo(c.domain1);
    assertThat(m.isApplicable()).isEqualTo(true);
  }

  @ParameterizedTest
  @ArgumentsSource(Models.class)
  <M extends ValueModel<T>, T, D extends Domain<T>> void isValidShouldDependOnValueOrBeTrueWithEmptyDomain(Case<M, T, D> c) {
    M m = c.creator.apply(c.validInDomain1, c.domain2);

    assertThat(m.isValid()).isFalse();

    m.setValue(c.validInBoth);

    assertThat(m.isValid()).isTrue();

    m.setValue(c.validInDomain1);

    assertThat(m.isValid()).isFalse();

    m.setValue(null);

    assertThat(m.isValid()).isEqualTo(m.getDomain().allowsNull());

    m.setDomain(Domain.empty());

    assertThat(m.isValid()).isTrue();

    M m2 = c.creator.apply(c.validInBoth, c.domain2);

    assertThat(m2.isValid()).isTrue();
  }

  @ParameterizedTest
  @ArgumentsSource(Models.class)
  <M extends ValueModel<T>, T, D extends Domain<T>> void shouldNotifyOfChanges(Case<M, T, D> c) {
    M m = c.creator.apply(c.validInDomain1, c.domain2);
    List<T> list = new ArrayList<>();

    m.subscribe(v -> list.add(v));

    assertThat(list).containsExactly((T)null);

    list.clear();

    m.setValue(c.validInBoth);
    m.setValue(c.validInBoth);
    m.setValue(c.validInDomain1);

    assertThat(list).containsExactly(c.validInBoth);

    list.clear();

    m.setValue(null);
    m.setValue(null);
    m.setValue(c.validInDomain1);
    m.setValue(c.validInDomain1);
    m.setValue(null);
    m.setValue(c.validInDomain1);
    m.setValue(null);

    if(c.domain1.allowsNull()) {
      assertThat(list).containsExactly((T)null);
    }
    else {
      assertThat(list).isEmpty();
    }
  }

  @ParameterizedTest
  @ArgumentsSource(Models.class)
  <M extends ValueModel<T>, T, D extends Domain<T>> void shouldRestrictValueToDomain(Case<M, T, D> c) {
    M m = c.creator.apply(c.validInDomain1, c.domain2);

    assertThat(m.getDomain()).isEqualTo(c.domain2);
    assertThat(m.isApplicable()).isEqualTo(true);
    assertThatThrownBy(() -> c.get(m)).isInstanceOf(InvalidValueException.class);
    assertThat(m.getValue()).isNull();
    assertThat(m.getRawValue()).isEqualTo(c.validInDomain1);

    m.setDomain(c.domain1);

    assertThat(m.getDomain()).isEqualTo(c.domain1);
    assertThat(m.isApplicable()).isEqualTo(true);
    assertGet(c, m, c.validInDomain1);
    assertThat(m.getValue()).isEqualTo(c.validInDomain1);
    assertThat(m.getRawValue()).isEqualTo(c.validInDomain1);

    c.set(m, c.validInBoth);

    assertGet(c, m, c.validInBoth);
    assertThat(m.getValue()).isEqualTo(c.validInBoth);
    assertThat(m.getRawValue()).isEqualTo(c.validInBoth);

    m.setValue(null);

    if(m.getDomain().allowsNull()) {
      assertThat(c.isNull(m)).isTrue();

      if(c.primitive) {
        assertThatThrownBy(() -> c.get(m)).isInstanceOf(NullValueException.class);
      }
      else {
        assertThat(c.get(m)).isNull();
      }

      assertThat(m.getValue()).isNull();
      assertThat(m.getRawValue()).isNull();
    }
    else {
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(InvalidValueException.class);
      assertThat(m.getValue()).isNull();
      assertThat(m.getRawValue()).isNull();
    }

    m.setDomain(c.domain1);

    assertThat(m.getDomain()).isEqualTo(c.domain1);
    assertThat(m.isApplicable()).isEqualTo(true);

    if(m.getDomain().allowsNull()) {
      assertThat(c.isNull(m)).isTrue();

      if(c.primitive) {
        assertThatThrownBy(() -> c.get(m)).isInstanceOf(NullValueException.class);
      }
      else {
        assertThat(c.get(m)).isNull();
      }

      assertThat(m.getValue()).isNull();
      assertThat(m.getRawValue()).isNull();
    }
    else {
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(InvalidValueException.class);
      assertThat(m.getValue()).isNull();
      assertThat(m.getRawValue()).isNull();
    }

    m.setValue(c.validInNone);

    if(c.validInNone == null && !m.getDomain().allowsNull()) {
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(InvalidValueException.class);
      assertThat(m.getValue()).isNull();
      assertThat(m.getRawValue()).isEqualTo(c.validInNone);

      m.setDomain(c.domain2);

      assertThat(m.getDomain()).isEqualTo(c.domain2);
      assertThat(m.isApplicable()).isEqualTo(true);
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(InvalidValueException.class);
      assertThat(m.getValue()).isNull();
      assertThat(m.getRawValue()).isEqualTo(c.validInNone);
    }

    m.setDomain(Domain.empty());

    assertThat(m.getDomain()).isEqualTo(Domain.empty());
    assertThat(m.isApplicable()).isEqualTo(false);
    if(c.primitive) {
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(NullValueException.class);
    }
    else {
      assertThat(c.get(m)).isNull();
    }
    assertThat(m.getValue()).isNull();
    assertThat(m.getRawValue()).isEqualTo(c.validInNone);

    m.setValue(c.validInDomain1);

    assertThat(m.getDomain()).isEqualTo(Domain.empty());
    assertThat(m.isApplicable()).isEqualTo(false);
    if(c.primitive) {
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(NullValueException.class);
    }
    else {
      assertThat(c.get(m)).isNull();
    }
    assertThat(m.getValue()).isNull();
    assertThat(m.getRawValue()).isEqualTo(c.validInDomain1);
  }

  @ParameterizedTest
  @ArgumentsSource(Models.class)
  <M extends ValueModel<T>, T, D extends Domain<T>> void trySetTest(Case<M, T, D> c) {
    M m = c.creator.apply(c.validInDomain1, c.domain1);

    assertThat(m.trySet(c.validInBoth, v -> v)).isTrue();
    assertThat(m.isValid()).isTrue();

    assertThat(m.trySet(c.validInBoth, v -> { throw new NumberFormatException(); })).isFalse();
    assertThat(m.isValid()).isFalse();

    assertThat(m.trySet(c.validInBoth, v -> v)).isTrue();
    assertThat(m.isValid()).isTrue();

    assertThat(m.trySet(c.validInDomain1, v -> v)).isTrue();
    assertThat(m.isValid()).isTrue();

    assertThat(m.trySet(c.validInDomain1, v -> v)).isFalse();
    assertThat(m.isValid()).isTrue();
  }

  private static <M extends ValueModel<T>, T, D extends Domain<T>> void assertGet(Case<M, T, D> c, M m, T expected) {
    if(c.primitive && expected == null) {
      assertThat(c.isNull(m)).isTrue();
      assertThatThrownBy(() -> c.get(m)).isInstanceOf(NullValueException.class);
    }
    else {
      assertThat(c.get(m)).isEqualTo(expected);
    }
  }

  record Case<M extends ValueModel<T>, T, D extends Domain<T>>(
    Class<M> cls,
    boolean primitive,
    D domain1,
    D domain2,
    T validInDomain1,
    T validInBoth,
    T validInNone,
    BiFunction<T, D, M> creator,
    Function<M, T> getter,
    BiConsumer<M, T> setter,
    Predicate<M> isNull
  ) {

    boolean isNull(M model) {
      return isNull.test(model);
    }

    T get(M model) {
      return getter.apply(model);
    }

    void set(M model, T value) {
      setter.accept(model, value);
    }
  }
}
