package org.int4.fx.core.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A container object which may or may not contain a value, including {@code null}.
 * <p>
 * This makes it particularly useful for representing raw model states where a distinction
 * must be made between "no value provided" ({@link Absent}) and "a value of null was
 * explicitly provided" ({@link Present} with {@code null}).
 * <p>
 * As a sealed interface implemented by records, it is optimized for pattern matching:
 * <pre>{@code
 * switch (model.getRawValue()) {
 *   case Value.Present(var v) -> System.out.println("Value is: " + v);
 *   case Value.Absent()       -> System.out.println("No value present");
 * }
 * }</pre>
 *
 * @param <T> the type of the contained value
 */
public sealed interface Value<T> permits Value.Present, Value.Absent {

  /**
   * Returns a {@code Value} describing the specified value, which may be {@code null}.
   *
   * @param <T> the type of the value
   * @param value the value to be present, may be {@code null}
   * @return a {@code Value} with the value present, never {@code null}
   */
  static <T> Value<T> present(T value) {
    return new Present<>(value);
  }

  /**
   * Returns an empty {@code Value} instance. No value is present for this {@code Value}.
   *
   * @param <T> the type of the non-existent value
   * @return an empty {@code Value}, never {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T> Value<T> absent() {
    return (Absent<T>)Absent.INSTANCE;
  }

  /**
   * Returns {@code true} if there is no value present, otherwise {@code false}.
   *
   * @return {@code true} if there is no value present, otherwise {@code false}
   */
  default boolean isAbsent() {
    return !isPresent();
  }

  /**
   * Returns {@code true} if there is a value present, otherwise {@code false}.
   *
   * @return {@code true} if there is a value present, otherwise {@code false}
   */
  boolean isPresent();

  /**
   * If a value is present, applies the given mapping function to it and returns
   * a {@code Value} describing the result, otherwise returns {@link Absent}.
   * <p>
   * The mapping function may return {@code null}, which will be represented as
   * a {@link Present} containing {@code null}.
   *
   * @param <U> the type of the mapped value
   * @param mapper the mapping function to apply to the value, if present
   * @return a {@code Value} describing the mapped value if a value is present,
   *   otherwise an empty {@code Value}
   * @throws NullPointerException if the mapping function is {@code null}
   */
  default <U> Value<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);

    return switch(this) {
      case Present<T>(T value) -> present(mapper.apply(value));
      case Absent() -> absent();
    };
  }

  /**
   * If a value is present, applies the given {@code Value}-bearing mapping
   * function to it and returns the result, otherwise returns {@link Absent}.
   * <p>
   * This method is similar to {@link #map(Function)}, but the mapping function
   * itself returns a {@code Value}, preventing nested {@code Value} instances.
   *
   * @param <U> the type of the mapped value
   * @param mapper the mapping function to apply to the value, if present
   * @return the result of applying the mapping function if a value is present,
   *   otherwise an empty {@code Value}
   * @throws NullPointerException if the mapping function is {@code null}
   */
  default <U> Value<U> flatMap(Function<? super T, ? extends Value<? extends U>> mapper) {
    Objects.requireNonNull(mapper);

    return switch(this) {
      case Present<T>(T value) -> {
        @SuppressWarnings("unchecked")
        Value<U> result = (Value<U>) mapper.apply(value);

        yield result;
      }
      case Absent() -> absent();
    };
  }

  /**
   * Returns the value if present, otherwise returns {@code other}.
   *
   * @param other the value to be returned if there is no value present, may be {@code null}
   * @return the value, if present, otherwise {@code other}
   */
  T orElse(T other);

  /**
   * Returns the value if present, otherwise throws {@code NoSuchElementException}.
   *
   * @return the value held by this {@code Value}
   * @throws NoSuchElementException if there is no value present
   */
  T orElseThrow();

  /**
   * A {@link Value} implementation representing a present state.
   *
   * @param <T> the type of the contained value
   * @param value the contained value, may be {@code null}
   */
  record Present<T>(T value) implements Value<T> {
    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public T orElse(T other) {
      return value;
    }

    @Override
    public T orElseThrow() {
      return value;
    }
  }

  /**
   * A {@link Value} implementation representing an absent state.
   *
   * @param <T> the type of the contained value
   */
  record Absent<T>() implements Value<T> {
    private static final Absent<?> INSTANCE = new Absent<>();

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public T orElse(T other) {
      return other;
    }

    @Override
    public T orElseThrow() {
      throw new NoSuchElementException();
    }
  }
}
