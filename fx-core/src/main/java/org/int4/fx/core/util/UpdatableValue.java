package org.int4.fx.core.util;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

/**
 * A read-only observable value designed for coordinated atomic updates across
 * multiple related values.
 * <p>
 * {@code UpdatableValue} exposes state as a standard {@link ObservableValue},
 * while ensuring that groups of related values can be updated together without
 * observers ever seeing intermediate or inconsistent states.
 * <p>
 * All updates are applied as a single logical change, after which observers
 * are notified.
 * <p>
 * This type is intended for models where multiple observable properties must
 * remain consistent from the perspective of observers.
 * <p>
 * Instances are typically owned privately and exposed only via their
 * read-only observable view.
 *
 * @param <T> the type of value
 */
public final class UpdatableValue<T> {

  /**
   * Creates a new {@code UpdatableValue} initialized to {@code null}.
   *
   * @param <T> the type of value
   * @return a new instance, never {@code null}
   */
  public static <T> UpdatableValue<T> of() {
    return new UpdatableValue<>(null);
  }

  /**
   * Creates a new {@code UpdatableValue} initialized with the given value.
   *
   * @param <T> the type of value
   * @param initialValue the initial value, can be {@code null}
   * @return a new instance, never {@code null}
   */
  public static <T> UpdatableValue<T> of(T initialValue) {
    return new UpdatableValue<>(initialValue);
  }

  /**
   * Updates the given {@code UpdatableValue} and notifies its observers.
   *
   * @param <A> the value type
   * @param uva the value to update, must not be {@code null}
   * @param a the new value, can be {@code null}
   * @throws NullPointerException if {@code uva} is {@code null}
   */
  public static <A> void set(UpdatableValue<A> uva, A a) {
    uva.set(a);

    uva.fire();
  }

  /**
   * Updates multiple {@code UpdatableValue} instances as a single logical change.
   * <p>
   * All values are updated before any observers are notified, ensuring that
   * observers never observe intermediate or partially updated state across the
   * affected values.
   *
   * @param <A> first value type
   * @param <B> second value type
   * @param uva first value to update, must not be {@code null}
   * @param a new value for first holder, can be {@code null}
   * @param uvb second value to update, must not be {@code null}
   * @param b new value for second holder, can be {@code null}
   * @throws NullPointerException if {@code uva} or {@code uvb} is {@code null}
   */
  public static <A, B> void set(
    UpdatableValue<A> uva, A a,
    UpdatableValue<B> uvb, B b
  ) {
    uva.set(a);
    uvb.set(b);

    uva.fire();
    uvb.fire();
  }

  /**
   * Updates multiple {@code UpdatableValue} instances as a single logical change.
   * <p>
   * All values are updated before any observers are notified, ensuring that
   * observers never observe intermediate or partially updated state across the
   * affected values.
   *
   * @param <A> first value type
   * @param <B> second value type
   * @param <C> third value type
   * @param uva first value to update, must not be {@code null}
   * @param a new value for first holder, can be {@code null}
   * @param uvb second value to update, must not be {@code null}
   * @param b new value for second holder, can be {@code null}
   * @param uvc third value to update, must not be {@code null}
   * @param c new value for third holder, can be {@code null}
   * @throws NullPointerException if any of {@code uva}, {@code uvb}, or {@code uvc} is {@code null}
   */
  public static <A, B, C> void set(
    UpdatableValue<A> uva, A a,
    UpdatableValue<B> uvb, B b,
    UpdatableValue<C> uvc, C c
  ) {
    uva.set(a);
    uvb.set(b);
    uvc.set(c);

    uva.fire();
    uvb.fire();
    uvc.fire();
  }

  /**
   * Updates multiple {@code UpdatableValue} instances as a single logical change.
   * <p>
   * All values are updated before any observers are notified, ensuring that
   * observers never observe intermediate or partially updated state across the
   * affected values.
   *
   * @param <A> first value type
   * @param <B> second value type
   * @param <C> third value type
   * @param <D> fourth value type
   * @param uva first value to update, must not be {@code null}
   * @param a new value for first holder, can be {@code null}
   * @param uvb second value to update, must not be {@code null}
   * @param b new value for second holder, can be {@code null}
   * @param uvc third value to update, must not be {@code null}
   * @param c new value for third holder, can be {@code null}
   * @param uvd fourth value to update, must not be {@code null}
   * @param d new value for fourth holder, can be {@code null}
   * @throws NullPointerException if any of {@code uva}, {@code uvb}, {@code uvc}, or {@code uvd} is {@code null}
   */
  public static <A, B, C, D> void set(
    UpdatableValue<A> uva, A a,
    UpdatableValue<B> uvb, B b,
    UpdatableValue<C> uvc, C c,
    UpdatableValue<D> uvd, D d
  ) {
    uva.set(a);
    uvb.set(b);
    uvc.set(c);
    uvd.set(d);

    uva.fire();
    uvb.fire();
    uvc.fire();
    uvd.fire();
  }

  private final ManualObservableValue<T> observableValue = new ManualObservableValue<>();

  UpdatableValue(T initialValue) {
    set(initialValue);
  }

  /**
   * Returns a read-only {@link ObservableValue} view of this value.
   * <p>
   * The returned observable can be used for binding or listening.
   *
   * @return a read-only observable view, never {@code null}
   */
  public ObservableValue<T> asObservableValue() {
    return observableValue;
  }

  /**
   * Returns the current value.
   *
   * @return the current value, may be {@code null}
   */
  public T getValue() {
    return observableValue.getValue();
  }

  void set(T value) {
    observableValue.value = value;
  }

  void fire() {
    observableValue.fire();
  }

  private static final class ManualObservableValue<T> extends ObservableValueBase<T> {
    T value;

    void fire() {
      fireValueChangedEvent();
    }

    @Override
    public T getValue() {
      return value;
    }

    @Override
    public String toString() {
      return "ObservableValue(value=" + value + ")";
    }
  }
}
