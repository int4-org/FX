package org.int4.fx.core.util;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

/**
 * A read-only observable value designed for coordinated updates across
 * multiple related values.
 * <p>
 * {@code UpdatableValue} exposes state as a standard {@link ObservableValue}
 * while ensuring that observers never observe intermediate or partially
 * updated state.
 * <p>
 * Updates are grouped into atomic update operations. An update operation is:
 * <ul>
 *   <li>a single-value update via {@code set(value)}</li>
 *   <li>a multi-value update via one of the {@code set(...)} overloads</li>
 *   <li>a batched sequence of updates via {@link #batch(Runnable)}</li>
 * </ul>
 * <p>
 * Within a single update operation, all value changes are applied before any
 * change notifications are dispatched, ensuring observers always observe a
 * consistent final state.
 * <p>
 * By default, each update operation triggers notifications immediately after
 * completion. When {@link #batch(Runnable)} is active on the current thread,
 * notifications from multiple update operations are accumulated and delivered
 * as a single consolidated notification phase after the batch completes.
 * <p>
 * Batching is thread-local and supports nested batch scopes. Nested batches
 * do not create additional scopes but participate in the existing one.
 * <p>
 * Instances are typically owned privately and exposed only via their
 * read-only observable view.
 *
 * @param <T> the type of value
 */
public final class UpdatableValue<T> {
  private static final ThreadLocal<Set<UpdatableValue<?>>> PENDING = new ThreadLocal<>();

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
   * Executes the given action while batching change notifications.
   * <p>
   * During execution of the provided {@code Runnable}, notifications from
   * {@link UpdatableValue} instances are not immediately dispatched. Instead,
   * affected values are recorded and a single consolidated notification phase
   * is executed after the action completes.
   * <p>
   * This ensures that observers only see the final state after all intermediate
   * updates, preventing transient or inconsistent observation.
   * <p>
   * Batching is scoped to the current thread. Nested calls to {@code batch}
   * do not create additional batching scopes; instead, they execute within the
   * existing batch.
   * <p>
   * Only notification delivery is deferred. Value updates occur immediately,
   * and no rollback or state isolation is performed.
   * <p>
   * If an exception is thrown by the action, all accumulated notifications are
   * still dispatched before the exception propagates.
   *
   * @param runnable the action to execute within a batched notification scope
   * @throws NullPointerException if {@code runnable} is {@code null}
   */
  public static void batch(Runnable runnable) {
    Objects.requireNonNull(runnable, "runnable");

    boolean nested = PENDING.get() != null;

    if(nested) {
      runnable.run();
    }
    else {
      Set<UpdatableValue<?>> updatableValues = new LinkedHashSet<>();

      PENDING.set(updatableValues);

      try {
        runnable.run();
      }
      finally {
        PENDING.remove();

        for(UpdatableValue<?> uv : updatableValues) {
          uv.observableValue.fire();
        }
      }
    }
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
    Set<UpdatableValue<?>> pending = PENDING.get();

    if(pending == null) {
      observableValue.fire();
    }
    else {
      pending.add(this);
    }
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
