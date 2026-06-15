package org.int4.fx.core.util;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents the state of a model value along with its validation context.
 * <p>
 * A {@code RawValue} encapsulates both the data value and the reason for its
 * current status, ensuring that value and validation feedback are updated
 * atomically.
 * <p>
 * This interface is sealed and implemented by three distinct records, each
 * representing a specific state of a value within a model:
 * <ul>
 *   <li>{@link Valid} – a successfully parsed value that satisfies domain constraints.</li>
 *   <li>{@link Invalid} – a successfully parsed value that violates domain constraints.</li>
 *   <li>{@link Incompatible} – a failure to parse or convert an input value.</li>
 * </ul>
 *
 * @param <T> the type of the contained value
 */
public sealed interface RawValue<T> {

  /**
   * Creates a {@code RawValue} representing a valid state.
   *
   * @param <T> the value type
   * @param value the valid value, may be {@code null}
   * @return a valid {@code RawValue}, never {@code null}
   */
  static <T> RawValue<T> valid(T value) {
    return new Valid<>(value);
  }

  /**
   * Creates a {@code RawValue} representing an invalid state.
   *
   * @param <T> the value type
   * @param value the invalid value, may be {@code null}
   * @param reason the reason why the value is invalid, never {@code null}
   * @return an invalid {@code RawValue}, never {@code null}
   * @throws NullPointerException if {@code reason} is {@code null}
   */
  static <T> RawValue<T> invalid(T value, Template reason) {
    return new Invalid<>(value, reason);
  }

  /**
   * Creates a {@code RawValue} representing an incompatible state where conversion failed.
   *
   * @param <T> the value type
   * @param reason the reason for the conversion failure, never {@code null}
   * @return an incompatible {@code RawValue}, never {@code null}
   * @throws NullPointerException if {@code reason} is {@code null}
   */
  static <T> RawValue<T> incompatible(Template reason) {
    return new Incompatible<>(reason);
  }

  /**
   * Returns {@code true} if a value is present and that value is {@code null}.
   *
   * @return {@code true} if the contained value is {@code null}
   */
  boolean isNull();

  /**
   * Returns {@code true} if a value is present. A value is present in both
   * {@link Valid} and {@link Invalid} states.
   *
   * @return {@code true} if a value is present, otherwise {@code false}
   */
  boolean isPresent();

  /**
   * Returns the value if present, otherwise returns {@code other}.
   *
   * @param other the value to be returned if there is no value present, may be {@code null}
   * @return the value, if present, otherwise {@code other}
   */
  T orElse(T other);

  /**
   * Returns the value if present, otherwise throws {@link NoSuchElementException}.
   *
   * @return the value held by this {@code RawValue}
   * @throws NoSuchElementException if there is no value present
   */
  T orElseThrow();

  /**
   * Represents a value that satisfies all current constraints.
   *
   * @param <T> the value type
   * @param value the valid value, may be {@code null}
   */
  record Valid<T>(T value) implements RawValue<T> {
    @Override
    public boolean isNull() {
      return value == null;
    }

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
   * Represents a value that was successfully converted but violates domain constraints.
   *
   * @param <T> the value type
   * @param value the invalid value, may be {@code null}
   * @param reason the reason for the validation failure, never {@code null}
   */
  record Invalid<T>(T value, Template reason) implements RawValue<T> {
    public Invalid {
      Objects.requireNonNull(reason, "reason");
    }

    @Override
    public boolean isNull() {
      return value == null;
    }

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
   * Represents a failure to convert an input to the required type.
   *
   * @param <T> the target value type
   * @param reason the reason for the conversion failure, never {@code null}
   */
  record Incompatible<T>(Template reason) implements RawValue<T> {
    public Incompatible {
      Objects.requireNonNull(reason, "reason");
    }

    @Override
    public boolean isNull() {
      return false;
    }

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
      throw new NoSuchElementException("value was incompatible: " + reason);
    }
  }
}
