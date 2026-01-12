package org.int4.fx.values.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * A domain of values with optional helper views.
 * <p>
 * The {@code Domain} interface exposes a set of static factory methods
 * for commonly used domain kinds.
 *
 * @param <T> the value type governed by this domain
 */
public sealed interface Domain<T> permits DomainImpl {

  /**
   * Creates a {@link Domain} that validates strings against the supplied
   * regular expression. The returned domain accepts exactly those strings
   * for which {@link java.util.regex.Matcher#matches()} returns {@code true}.
   *
   * @param regex the regular expression to compile, not {@code null}
   * @return a domain accepting strings that match {@code regex}, never {@code null}
   * @throws java.util.regex.PatternSyntaxException if the regex is invalid
   * @throws NullPointerException when any argument is {@code null}
   */
  static Domain<String> regex(String regex) {
    Pattern pattern = Pattern.compile(regex);

    return of(v -> pattern.matcher(v).matches());
  }

  /**
   * Creates a domain defined by an arbitrary predicate. The predicate
   * controls which values (excluding {@code null}) are considered part of
   * the domain.
   *
   * @param <T> the value type
   * @param predicate a predicate that returns {@code true} for valid values
   * @return a domain validated by {@code predicate}, never {@code null}
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  static <T> Domain<T> of(Predicate<T> predicate) {
    return new DomainImpl<>(predicate, false, false);
  }

  /**
   * Creates an enumerated domain from the provided items. The returned
   * domain supports an {@link IndexedView} that exposes the items as an
   * immutable list.
   *
   * @param <T> the value type
   * @param items the allowed items, cannot be {@code null} but can be empty
   * @return a domain enumerating the supplied items, never {@code null}
   * @throws NullPointerException if the items array is {@code null}
   */
  static <T> Domain<T> of(@SuppressWarnings("unchecked") T... items) {
    return of(Arrays.asList(items));
  }

  /**
   * Creates an enumerated domain backed by the supplied list. The list should
   * not be modified. Behavior is undefined if the list is modified.
   *
   * @param <T> the value type
   * @param items a list of allowed items, cannot be {@code null} but can be empty
   * @return a domain enumerating the supplied list, never {@code null}
   * @throws NullPointerException if {@code items} is {@code null}
   */
  static <T> Domain<T> of(List<T> items) {
    List<T> readOnlyItems = Collections.unmodifiableList(Objects.requireNonNull(items, "items"));

    return new DomainImpl<>(
      readOnlyItems::contains,
      false,
      readOnlyItems.isEmpty(),
      new IndexedView<T>() {
        @Override
        public T get(long index) {
          Objects.checkIndex(index, size());

          return readOnlyItems.get((int)index);
        }

        @Override
        public long indexOf(T value) {
          return readOnlyItems.indexOf(value);
        }

        @Override
        public long size() {
          return readOnlyItems.size();
        }

        @Override
        public List<T> asList() {
          return readOnlyItems;
        }
      }
    );
  }

  /**
   * Returns a domain that includes all values of type {@code T}, including
   * {@code null}. This is the most permissive domain.
   *
   * @param <T> the type of values this domain governs
   * @return an unrestricted domain, never {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T> Domain<T> any() {
    return (Domain<T>)DomainImpl.ANY;
  }

  /**
   * Returns a domain that includes all values of type {@code T}, excluding
   * {@code null}.
   *
   * @param <T> the type of values this domain governs
   * @return an unrestricted domain that disallows {@code null}; never {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T> Domain<T> nonNull() {
    return (Domain<T>)DomainImpl.NON_NULL;
  }

  /**
   * Returns an empty domain: no values (including {@code null}) are
   * permitted. All invocations of {@link #contains(Object)} will return
   * {@code false}.
   *
   * @param <T> the type of values this domain governs
   * @return an empty domain, never {@code null}
   */
  @SuppressWarnings("unchecked")
  public static <T> Domain<T> empty() {
    return (Domain<T>)DomainImpl.EMPTY;
  }

  /**
   * Creates a new integer domain with the given minimum and maximum values.
   * <p>
   * The returned domain exposes an {@link IndexedView} enumerating the
   * allowed integers (including both ends) and a {@link StepperView}
   * implementing logical stepping semantics.
   *
   * @param min inclusive minimum value
   * @param max inclusive maximum value
   * @return a domain enumerating integers between {@code min} and {@code max}, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static Domain<Integer> bounded(int min, int max) {
    return bounded(min, max, 1);
  }

  /**
   * Creates a new stepped integer domain with the given minimum and maximum values.
   * <p>
   * The returned domain exposes an {@link IndexedView} enumerating the
   * allowed integers (including both ends) and a {@link StepperView}
   * implementing logical stepping semantics. The supplied {@code step}
   * value must be positive and align with the range boundaries.
   *
   * @param min inclusive minimum value
   * @param max inclusive maximum value
   * @param step positive step size
   * @return a domain for stepped integers, never {@code null}
   * @throws IllegalArgumentException when {@code step <= 0}, {@code min > max}
   *   or {@code (max-min) % step != 0}
   */
  static Domain<Integer> bounded(int min, int max, int step) {
    if(step <= 0) {
      throw new IllegalArgumentException("step must be positive");
    }
    if(min > max) {
      throw new IllegalArgumentException("min must not exceed max");
    }
    if((max - min) % step != 0) {
      throw new IllegalArgumentException("max not aligned to step");
    }

    UnaryOperator<Integer> normalizer = v -> Math.clamp((((long)v - (long)min + step / 2L) / step) * step + min, min, max);
    Predicate<Integer> validator = v -> normalizer.apply(v).equals(v);
    int size = (max - min) / step + 1;

    return new DomainImpl<>(
      validator,
      false,
      false,
      new IndexedView<Integer>() {
        @Override
        public Integer get(long index) {
          Objects.checkIndex(index, size());

          return min + (int)index * step;
        }

        @Override
        public long indexOf(Integer value) {
          return value != null ? (normalizer.apply(value) - min) / step : -1;
        }

        @Override
        public long size() {
          return size;
        }
      },
      new StepperView<Integer>() {
        @Override
        public Integer step(Integer value, int steps) {
          if(steps == 0 || value == null) {
            return value;
          }

          int normalized = normalizer.apply(value);
          int stepsToApply = normalized == value ? steps : steps > 0 ? steps - 1 : steps + 1;

          return normalizer.apply(normalized + step * stepsToApply);
        }
      }
    );
  }

  /**
   * Creates a new long domain with the given minimum and maximum values.
   * <p>
   * The returned domain exposes an {@link IndexedView} enumerating the
   * allowed longs (including both ends) and a {@link StepperView}
   * implementing logical stepping semantics.
   *
   * @param min inclusive minimum value
   * @param max inclusive maximum value
   * @return a domain enumerating longs between {@code min} and {@code max}, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static Domain<Long> bounded(long min, long max) {
    return bounded(min, max, 1);
  }

  /**
   * Creates a new stepped long domain with the given minimum and maximum values.
   * <p>
   * The returned domain exposes an {@link IndexedView} enumerating the
   * allowed longs (including both ends) and a {@link StepperView}
   * implementing logical stepping semantics. The supplied {@code step}
   * value must be positive and align with the range boundaries.
   *
   * @param min inclusive minimum value
   * @param max inclusive maximum value
   * @param step positive step size
   * @return a domain for stepped longs, never {@code null}
   * @throws IllegalArgumentException when {@code step <= 0}, {@code min > max}
   *   or {@code (max-min) % step != 0}
   */
  static Domain<Long> bounded(long min, long max, long step) {
    if(step <= 0) {
      throw new IllegalArgumentException("step must be positive");
    }
    if(min > max) {
      throw new IllegalArgumentException("min must not exceed max");
    }
    if((max - min) % step != 0) {
      throw new IllegalArgumentException("max not aligned to step");
    }

    UnaryOperator<Long> normalizer = v -> Math.clamp(((v - min + step / 2L) / step) * step + min, min, max);
    Predicate<Long> validator = v -> normalizer.apply(v).equals(v);
    long size = (max - min) / step + 1;

    return new DomainImpl<>(
      validator,
      false,
      false,
      new IndexedView<Long>() {
        @Override
        public Long get(long index) {
          Objects.checkIndex(index, size());

          return min + index * step;
        }

        @Override
        public long indexOf(Long value) {
          return value != null ? (normalizer.apply(value) - min) / step : -1;
        }

        @Override
        public long size() {
          return size;
        }
      },
      new StepperView<Long>() {
        @Override
        public Long step(Long value, int steps) {
          if(steps == 0 || value == null) {
            return value;
          }

          long normalized = normalizer.apply(value);
          int stepsToApply = normalized == value ? steps : steps > 0 ? steps - 1 : steps + 1;

          return normalizer.apply(normalized + step * stepsToApply);
        }
      }
    );
  }

  /**
   * Creates a new stepped double domain with the given minimum and maximum values.
   *
   * The returned domain exposes an {@link IndexedView} and a {@link StepperView}.
   * The supplied {@code step} value must be positive.
   *
   * @param min inclusive minimum value
   * @param max inclusive maximum value
   * @param step a positive finite step size
   * @return a domain of stepped double values, never {@code null}
   * @throws IllegalArgumentException when {@code step <= 0}, non-finite, or {@code min > max}
   */
  static Domain<Double> bounded(double min, double max, double step) {
    if(step <= 0 || !Double.isFinite(step)) {
      throw new IllegalArgumentException("step must be positive and finite");
    }
    if(min > max) {
      throw new IllegalArgumentException("min must not exceed max");
    }

    double inverse = 1 / step;  // use symmetric scale/round/unscale to preserve idempotent snapping

    UnaryOperator<Double> normalizer = v -> Math.clamp(Math.round(v * inverse) / inverse, min, max);
    Predicate<Double> validator = v -> normalizer.apply(v).equals(v);
    int size = (int)Math.floor((max - min) / step) + 1;

    return new DomainImpl<>(
      validator,
      false,
      false,
      new IndexedView<Double>() {
        @Override
        public Double get(long index) {
          Objects.checkIndex(index, size());

          return normalizer.apply(min + index * step);
        }

        @Override
        public long indexOf(Double value) {
          return value != null ? (int)Math.round((normalizer.apply(value) - min) * inverse) : -1;
        }

        @Override
        public long size() {
          return size;
        }
      },
      new StepperView<Double>() {
        @Override
        public Double step(Double value, int steps) {
          if(steps == 0 || value == null) {
            return value;
          }

          double normalized = normalizer.apply(value);
          int stepsToApply = normalized == value ? steps : steps > 0 ? steps - 1 : steps + 1;

          return normalizer.apply(normalized + step * stepsToApply);
        }
      }
    );
  }

  /**
   * Creates a bounded domain for types that are {@link Comparable}.
   * The domain accepts values between {@code first} and {@code last}
   * inclusive.
   * <p>
   * The returned domain supports a {@link NormalizedView} that can
   * optionally snap values into the allowed range.
   *
   * @param <T> element type
   * @param first inclusive lower bound, cannot be {@code null}
   * @param last inclusive upper bound, cannot be {@code null}
   * @return a bounded domain for {@code T}, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  static <T extends Comparable<? super T>> Domain<T> bounded(T first, T last) {
    return bounded(first, last, T::compareTo);
  }

  /**
   * Creates a bounded domain using the provided comparator. The domain
   * accepts values between {@code first} and {@code last} inclusive.
   * <p>
   * The returned domain supports a {@link NormalizedView} that can
   * optionally snap values into the allowed range.
   *
   * @param <T> element type
   * @param first inclusive lower bound, not {@code null}
   * @param last inclusive upper bound, not {@code null}
   * @param comparator comparator used to compare values
   * @return a bounded domain for {@code T}, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  static <T> Domain<T> bounded(T first, T last, Comparator<T> comparator) {
    Objects.requireNonNull(comparator, "comparator");
    Objects.requireNonNull(first, "first");
    Objects.requireNonNull(last, "last");

    Predicate<T> validator = v -> comparator.compare(v, first) >= 0 && comparator.compare(v, last) <= 0;

    return new DomainImpl<>(
      validator,
      false,
      false,
      (NormalizedView<T>)v -> v == null ? first : comparator.compare(v, first) < 0 ? first : comparator.compare(v, last) > 0 ? last : v
    );
  }

  /**
   * Creates a continuous numeric domain over the closed interval
   * {@code [min, max]}. The returned domain exposes a
   * {@link ContinuousView} which maps normalized fractions in [0,1] to
   * domain values and vice versa.
   *
   * @param min inclusive minimum
   * @param max inclusive maximum
   * @return a continuous numeric domain, never {@code null}
   * @throws IllegalArgumentException if {@code min > max}
   */
  static Domain<Double> continuous(double min, double max) {
    if(min > max) {
      throw new IllegalArgumentException("min must not exceed max");
    }

    UnaryOperator<Double> normalizer = v -> Math.clamp(v, min, max);
    Predicate<Double> validator = v -> v != null && v >= min && v <= max;

    return new DomainImpl<>(
      validator,
      false,
      false,
      new ContinuousView<Double>() {
        @Override
        public Double get(double fraction) {
          return min + Math.clamp(fraction, 0, 1) * (max - min);
        }

        @Override
        public double fractionOf(Double value) {
          return value == null ? 0.0 : (normalizer.apply(value) - min) / (max - min);
        }
      }
    );
  }

  /**
   * Returns {@code true} if {@code null} is permitted by this domain.
   *
   * @return {@code true} when {@code null} is allowed
   */
  default boolean allowsNull() {
    return contains(null);
  }

  /**
   * Tests if the given value is part of this domain.
   *
   * @param value a value to test, can be {@code null}
   * @return {@code true} if the value is part of this domain, otherwise {@code false}
   */
  boolean contains(T value);

  /**
   * Returns {@code true} if this domain contains one or more values, otherwise {@code false}.
   *
   * @return {@code true} if this domain contains one or more values, otherwise {@code false}
   */
  default boolean isNotEmpty() {
    return !isEmpty();
  }

  /**
   * Returns {@code true} if this domain does not contain any values, otherwise {@code false}.
   *
   * @return {@code true} if this domain does not contain any values, otherwise {@code false}
   */
  boolean isEmpty();

  /**
   * Returns a new domain, that contains {@code null} as a value.
   *
   * @return a new domain, that contains {@code null} as a value; never {@code null}
   */
  Domain<T> nullable();

  /**
   * Returns the first view that matches the given classes. If no view matches,
   * a default no-op view is returned.
   *
   * @param classes the view classes to search for
   * @return a view, never {@code null}
   */
  View<T> view(Class<?>... classes);

  /**
   * Returns the view of the given type.
   * <p>
   * The returned view is guaranteed to be non-null if the domain actually
   * provides a view of the requested class. If the domain does not support
   * the requested class, an {@link IllegalStateException} is thrown.
   * <p>
   * <strong>Type safety:</strong> The compiler cannot verify that the
   * class literal matches the generic target type. The returned view is cast
   * to the target type {@code V} based on the assignment. If the target type
   * is incompatible with the actual view type, a {@link ClassCastException}
   * may occur at runtime.</p>
   *
   * <p>Example:</p>
   * <pre>{@code
   * IndexedView<Integer> a = domain.requireView(IndexedView.class); // OK
   * IndexedView<String> b = domain.requireView(IndexedView.class);  // compile-time error
   * String c = domain.requireView(IndexedView.class);               // compiles, throws CCE on assignment
   * }</pre>
   *
   * @param <V> the view type
   * @param viewClass the required view class, cannot be {@code null}
   * @return the matching view, never {@code null}
   * @throws IllegalStateException if the view is not supported by this domain
   * @throws NullPointerException if any argument is {@code null}
   */
  <V extends View<T>> V requireView(Class<?> viewClass);
}
