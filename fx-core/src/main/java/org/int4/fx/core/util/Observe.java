package org.int4.fx.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.int4.common.function.QuadFunction;
import org.int4.common.function.TriFunction;

/**
 * Utility class providing factory methods for constructing composite
 * {@link ObservableValue} instances. All derived observables use lazy
 * observation: listeners are attached to dependencies only while the
 * derived observable itself is being observed, and detached once it has
 * no listeners. This yields deterministic listener management without the
 * need for weak listeners.
 */
public class Observe {

  /**
   * Creates a builder for boolean aggregation logic over the
   * given observable boolean flags.
   * <p>
   * The returned builder can construct lazily evaluated observables such
   * as "all true" or "any false". All produced observables are lazily
   * listening.
   *
   * @param booleans observable boolean values to aggregate, cannot be {@code null} or contain {@code null}s
   * @return a builder providing boolean aggregation operations, never {@code null}
   * @throws NullPointerException when any argument or array element is {@code null}
   */
  @SafeVarargs
  public static BooleansBuilder booleans(ObservableValue<Boolean>... booleans) {
    return new BooleansBuilder(booleans);
  }

  /**
   * Creates a builder for combining two observable values.
   * <p>
   * The resulting builder can construct lazily evaluated derived
   * observables based on both dependencies.
   *
   * @param a the first observable value, cannot be {@code null}
   * @param b the second observable value, cannot be {@code null}
   * @param <A> type of the first value
   * @param <B> type of the second value
   * @return a builder for constructing derived two-value observables, never {@code null}
   * @throws NullPointerException when any argument is {@code null}
   */
  public static <A, B> Builder2<A, B> values(ObservableValue<? extends A> a, ObservableValue<? extends B> b) {
    return new Builder2<>(a, b);
  }

  /**
   * Creates a builder for combining three observable values.
   * <p>
   * The resulting builder can define derived observables that depend on
   * all three inputs and compute lazily.
   *
   * @param a the first observable value, cannot be {@code null}
   * @param b the second observable value, cannot be {@code null}
   * @param c the third observable value, cannot be {@code null}
   * @param <A> type of the first value
   * @param <B> type of the second value
   * @param <C> type of the third value
   * @return a builder for constructing derived three-value observables, never {@code null}
   * @throws NullPointerException when any argument is {@code null}
   */
  public static <A, B, C> Builder3<A, B, C> values(ObservableValue<? extends A> a, ObservableValue<? extends B> b, ObservableValue<? extends C> c) {
    return new Builder3<>(a, b, c);
  }

  /**
   * Creates a {@link Builder4} for combining four observable values.
   * <p>
   * The resulting builder constructs lazily evaluated observables based
   * on all four dependencies.
   *
   * @param a the first observable value, cannot be {@code null}
   * @param b the second observable value, cannot be {@code null}
   * @param c the third observable value, cannot be {@code null}
   * @param d the fourth observable value, cannot be {@code null}
   * @param <A> type of the first value
   * @param <B> type of the second value
   * @param <C> type of the third value
   * @param <D> type of the fourth value
   * @return a builder for constructing derived four-value observables, never {@code null}
   * @throws NullPointerException when any argument is {@code null}
   */
  public static <A, B, C, D> Builder4<A, B, C, D> values(ObservableValue<? extends A> a, ObservableValue<? extends B> b, ObservableValue<? extends C> c, ObservableValue<? extends D> d) {
    return new Builder4<>(a, b, c, d);
  }

  /**
   * Builder providing boolean aggregation operations over a fixed set of
   * {@link ObservableValue} instances. All generated observables use lazy
   * listener attachment and detach when no longer observed.
   */
  public static class BooleansBuilder {
    private final ObservableValue<Boolean>[] observables;

    @SafeVarargs
    private BooleansBuilder(ObservableValue<Boolean>... observables) {
      this.observables = Objects.requireNonNull(observables, "observables");

      if(Arrays.stream(observables).anyMatch(Objects::isNull)) {
        throw new NullPointerException("observables contains nulls");
      }
    }

    /**
     * Creates a lazily evaluated observable that is {@code true} only when
     * every supplied boolean observable evaluates to {@code true}.
     *
     * @return an observable indicating whether all values are true, never {@code null}
     */
    public ObservableValue<Boolean> allTrue() {
      return combine(() -> checkAll(true), observables);
    }

    /**
     * Creates a lazily evaluated observable that is {@code true} only when
     * every supplied boolean observable evaluates to {@code false}.
     *
     * @return an observable indicating whether all values are false, never {@code null}
     */
    public ObservableValue<Boolean> allFalse() {
      return combine(() -> checkAll(false), observables);
    }

    /**
     * Creates a lazily evaluated observable that is {@code true} when at
     * least one supplied boolean observable evaluates to {@code true}.
     *
     * @return an observable indicating whether any value is true, never {@code null}
     */
    public ObservableValue<Boolean> anyTrue() {
      return combine(() -> checkAny(true), observables);
    }

    /**
     * Creates a lazily evaluated observable that is {@code true} when at
     * least one supplied boolean observable evaluates to {@code false}.
     *
     * @return an observable indicating whether any value is false, never {@code null}
     */
    public ObservableValue<Boolean> anyFalse() {
      return combine(() -> checkAny(false), observables);
    }

    private boolean checkAll(boolean expected) {
      for(ObservableValue<Boolean> flag : observables) {
        Boolean v = flag.getValue();

        if(!Objects.equals(v, expected)) {
          return false;
        }
      }

      return true;
    }

    private boolean checkAny(boolean expected) {
      for(ObservableValue<Boolean> flag : observables) {
        Boolean v = flag.getValue();

        if(Objects.equals(v, expected)) {
          return true;
        }
      }

      return false;
    }
  }

  /**
   * Builder for creating lazily computed derived observables based on two
   * observable dependencies.
   *
   * @param <A> type of the first dependency
   * @param <B> type of the second dependency
   */
  public static class Builder2<A, B> {
    private final ObservableValue<? extends A> a;
    private final ObservableValue<? extends B> b;

    private Builder2(
      ObservableValue<? extends A> a,
      ObservableValue<? extends B> b
    ) {
      this.a = Objects.requireNonNull(a, "a");
      this.b = Objects.requireNonNull(b, "b");
    }

    /**
     * Creates a lazily evaluated derived observable computed from the values
     * of the two dependencies using the supplied function.
     * <p>
     * The mapping function is invoked only if both dependency values are
     * non-null. If either dependency evaluates to {@code null}, the mapping
     * function is <strong>not called</strong> and the derived observable's
     * value is {@code null}.
     * <p>
     * The derived observable recomputes its value lazily whenever it has
     * active listeners and when either dependency changes.
     *
     * @param <T> the type of the derived value
     * @param mapper a mapping function combining both dependency values, cannot be {@code null}
     * @return a lazily evaluated observable value produced by the mapping, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public <T> ObservableValue<T> map(BiFunction<A, B, T> mapper) {
      Objects.requireNonNull(mapper, "mapper");

      return combine(() -> {
        A v1 = a.getValue();
        B v2 = b.getValue();

        return v1 != null && v2 != null
          ? mapper.apply(v1, v2)
          : null;
      }, a, b);
    }

    /**
     * Creates a lazily evaluated derived observable computed from the values
     * of the two dependencies using the supplied function.
     * <p>
     * Unlike {@link #map(BiFunction)}, this {@code compute} variant will
     * always call the supplied mapping function even if one or both
     * dependencies are {@code null}. The function therefore must be able
     * to handle {@code null} arguments if those are possible.
     * <p>
     * The derived observable recomputes its value lazily whenever it has
     * active listeners and when either dependency changes.
     *
     * @param <T> the type of the derived value
     * @param mapper a mapping function combining both dependency values, cannot be {@code null}
     * @return a lazily evaluated observable value produced by the mapping, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public <T> ObservableValue<T> compute(BiFunction<A, B, T> mapper) {
      Objects.requireNonNull(mapper, "mapper");

      return combine(() -> mapper.apply(a.getValue(), b.getValue()), a, b);
    }
  }

  /**
   * Builder for constructing lazily computed derived observables based on
   * three observable dependencies.
   *
   * @param <A> type of the first dependency
   * @param <B> type of the second dependency
   * @param <C> type of the third dependency
   */
  public static class Builder3<A, B, C> {
    private final ObservableValue<? extends A> a;
    private final ObservableValue<? extends B> b;
    private final ObservableValue<? extends C> c;

    private Builder3(
      ObservableValue<? extends A> a,
      ObservableValue<? extends B> b,
      ObservableValue<? extends C> c
    ) {
      this.a = Objects.requireNonNull(a, "a");
      this.b = Objects.requireNonNull(b, "b");
      this.c = Objects.requireNonNull(c, "c");
    }

    /**
     * Creates a lazily evaluated derived observable computed from the values
     * of the three dependencies using the supplied function.
     * <p>
     * The mapping function is invoked only if all dependency values are
     * non-null. If any dependency evaluates to {@code null}, the mapping
     * function is <strong>not called</strong> and the derived observable's
     * value is {@code null}.
     * <p>
     * The derived observable recomputes its value lazily whenever it has
     * active listeners and when either dependency changes.
     *
     * @param <T> the type of the derived value
     * @param mapper a mapping function combining all dependency values, cannot be {@code null}
     * @return a lazily evaluated observable value produced by the mapping, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public <T> ObservableValue<T> map(TriFunction<A, B, C, T> mapper) {
      Objects.requireNonNull(mapper, "mapper");

      return combine(() -> {
        A v1 = a.getValue();
        B v2 = b.getValue();
        C v3 = c.getValue();

        return v1 != null && v2 != null && v3 != null
          ? mapper.apply(v1, v2, v3)
          : null;
      }, a, b, c);
    }

    /**
     * Creates a lazily evaluated derived observable computed from the values
     * of the three dependencies using the supplied function.
     * <p>
     * Unlike {@link #map(TriFunction)}, this {@code compute} variant will
     * always call the supplied mapping function even if one or more
     * dependencies are {@code null}. The function therefore must be able
     * to handle {@code null} arguments if those are possible.
     * <p>
     * The derived observable recomputes its value lazily whenever it has
     * active listeners and when any dependency changes.
     *
     * @param <T> the type of the derived value
     * @param mapper a mapping function combining all dependency values, cannot be {@code null}
     * @return a lazily evaluated observable value produced by the mapping, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public <T> ObservableValue<T> compute(TriFunction<A, B, C, T> mapper) {
      Objects.requireNonNull(mapper, "mapper");

      return combine(() -> mapper.apply(a.getValue(), b.getValue(), c.getValue()), a, b, c);
    }
  }

  /**
   * Builder for constructing lazily computed derived observables based on
   * four observable dependencies.
   *
   * @param <A> type of the first dependency
   * @param <B> type of the second dependency
   * @param <C> type of the third dependency
   * @param <D> type of the fourth dependency
   */
  public static class Builder4<A, B, C, D> {
    private final ObservableValue<? extends A> a;
    private final ObservableValue<? extends B> b;
    private final ObservableValue<? extends C> c;
    private final ObservableValue<? extends D> d;

    private Builder4(
      ObservableValue<? extends A> a,
      ObservableValue<? extends B> b,
      ObservableValue<? extends C> c,
      ObservableValue<? extends D> d
    ) {
      this.a = Objects.requireNonNull(a, "a");
      this.b = Objects.requireNonNull(b, "b");
      this.c = Objects.requireNonNull(c, "c");
      this.d = Objects.requireNonNull(d, "d");
    }

    /**
     * Creates a lazily evaluated derived observable computed from the values
     * of the four dependencies using the supplied function.
     * <p>
     * The mapping function is invoked only if all dependency values are
     * non-null. If any dependency evaluates to {@code null}, the mapping
     * function is <strong>not called</strong> and the derived observable's
     * value is {@code null}.
     * <p>
     * The derived observable recomputes its value lazily whenever it has
     * active listeners and when either dependency changes.
     *
     * @param <T> the type of the derived value
     * @param mapper a mapping function combining all dependency values, cannot be {@code null}
     * @return a lazily evaluated observable value produced by the mapping, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public <T> ObservableValue<T> map(QuadFunction<A, B, C, D, T> mapper) {
      Objects.requireNonNull(mapper, "mapper");

      return combine(() -> {
        A v1 = a.getValue();
        B v2 = b.getValue();
        C v3 = c.getValue();
        D v4 = d.getValue();

        return v1 != null && v2 != null && v3 != null && v4 != null
          ? mapper.apply(v1, v2, v3, v4)
          : null;
      }, a, b, c, d);
    }

    /**
     * Creates a lazily evaluated derived observable computed from the values
     * of the four dependencies using the supplied function.
     * <p>
     * Unlike {@link #map(QuadFunction)}, this {@code compute} variant will
     * always call the supplied mapping function even if one or more
     * dependencies are {@code null}. The function therefore must be able
     * to handle {@code null} arguments if those are possible.
     * <p>
     * The derived observable recomputes its value lazily whenever it has
     * active listeners and when any dependency changes.
     *
     * @param <T> the type of the derived value
     * @param mapper a mapping function combining all dependency values, cannot be {@code null}
     * @return a lazily evaluated observable value produced by the mapping, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public <T> ObservableValue<T> compute(QuadFunction<A, B, C, D, T> mapper) {
      Objects.requireNonNull(mapper, "mapper");

      return combine(() -> mapper.apply(a.getValue(), b.getValue(), c.getValue(), d.getValue()), a, b, c, d);
    }
  }

  /**
   * Internal helper that constructs a lazily-evaluated {@link ObservableValue}
   * whose value is computed by the supplied {@link Supplier} and which
   * attaches listeners to the given dependency {@link Observable}s only while
   * the returned observable itself is observed.
   *
   * <p>Behavior summary:
   * <ul>
   *   <li>The returned observable recomputes its value lazily on {@link #getValue()} when it is not valid.</li>
   *   <li>Listeners passed to the returned observable cause it to start
   *       observing the declared dependencies: an internal
   *       {@link InvalidationListener} is registered on each dependency when
   *       the first listener is added, and removed once the last listener is
   *       removed.</li>
   *   <li>When a dependency invalidates, the returned observable marks its
   *       value invalid and notifies registered {@link InvalidationListener}s
   *       and {@link ChangeListener}s. When there are registered
   *       {@code ChangeListener}s the new value is computed before invoking
   *       the change listeners so they receive both old and new values.
   *       Invalidation listeners are invoked after recomputation (if any).
   *   </li>
   *   <li>The returned observable intentionally does not attempt to be
   *       thread-safe; it is designed for use on the JavaFX application
   *       thread.</li>
   * </ul>
   *
   * @param <T> the computed value type
   * @param valueComputer supplier that computes the current value
   * @param observables dependency observables that will be lazily listened to
   * @return an {@link ObservableValue} which computes values via the supplier and lazily observes dependencies
   */
  private static <T> ObservableValue<T> combine(Supplier<T> valueComputer, Observable... observables) {
    return new ObservableValue<>() {
      private final List<InvalidationListener> invalidationListeners = new ArrayList<>();
      private final List<ChangeListener<? super T>> changeListeners = new ArrayList<>();
      private final InvalidationListener invalidationListener = obs -> invalidated();

      private T value;
      private boolean valid;
      private boolean observed;

      @Override
      public void addListener(InvalidationListener listener) {
        if(invalidationListeners.isEmpty() && changeListeners.isEmpty()) {
          startObserving();
        }

        invalidationListeners.add(listener);
      }

      @Override
      public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);

        if(invalidationListeners.isEmpty() && changeListeners.isEmpty()) {
          stopObserving();
        }
      }

      @Override
      public void addListener(ChangeListener<? super T> listener) {
        if(changeListeners.isEmpty()) {
          getValue();  // make valid and ensure we have an old value

          if(invalidationListeners.isEmpty()) {
            startObserving();
          }
        }

        changeListeners.add(listener);
      }

      @Override
      public void removeListener(ChangeListener<? super T> listener) {
        changeListeners.remove(listener);

        if(invalidationListeners.isEmpty() && changeListeners.isEmpty()) {
          stopObserving();
        }
      }

      private void invalidated() {
        valid = false;

        T oldValue = value;

        if(!changeListeners.isEmpty()) {
          value = getValue();
        }

        for(InvalidationListener listener : invalidationListeners) {
          listener.invalidated(this);
        }

        if(!Objects.equals(oldValue, value)) {
          for(ChangeListener<? super T> listener : changeListeners) {
            listener.changed(this, oldValue, value);
          }
        }
      }

      private void startObserving() {
        for(Observable obs : observables) {
          obs.addListener(invalidationListener);
        }

        observed = true;
      }

      private void stopObserving() {
        for(Observable obs : observables) {
          obs.removeListener(invalidationListener);
        }

        observed = false;
        valid = false;
      }

      private T computeValue() {
        return valueComputer.get();
      }

      @Override
      public T getValue() {
        if(!valid) {
          value = computeValue();
          valid = observed;  // don't become valid if not observed, recompute each time
        }

        return value;
      }
    };
  }

  private Observe() {}
}