package org.int4.fx.values.domain;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

final class DomainImpl<T> implements Domain<T> {
  static final Domain<?> NON_NULL = Domain.of(v -> true);
  static final Domain<?> ANY = NON_NULL.nullable();
  static final Domain<?> EMPTY = new DomainImpl<>(v -> false, false, true);

  private static final View<?> NOOP_VIEW = new View<>() {};

  private final Predicate<T> validator;
  private final Map<Class<?>, View<T>> views = new LinkedHashMap<>();  // order is important when making a copy for nullable
  private final boolean includesNull;
  private final boolean empty;

  @SafeVarargs
  DomainImpl(Predicate<T> validator, boolean includesNull, boolean empty, View<T>... views) {
    this.validator = validator;
    this.includesNull = includesNull;
    this.empty = empty;

    for(View<T> view : views) {
      ArrayDeque<Class<?>> toScan = new ArrayDeque<>(Arrays.asList(view.getClass().getInterfaces()));

      while(!toScan.isEmpty()) {
        Class<?> iface = toScan.pop();

        this.views.putIfAbsent(iface, view);

        toScan.addAll(Arrays.asList(iface.getInterfaces()));
      }
    }
  }

  @Override
  public boolean contains(T value) {
    return value == null ? includesNull : validator.test(value);
  }

  @Override
  public View<T> view(Class<?>... classes) {
    for(Class<?> cls : classes) {
      View<T> view = views.get(cls);

      if(view != null) {
        return view;
      }
    }

    @SuppressWarnings("unchecked")
    View<T> castView = (View<T>)NOOP_VIEW;

    return castView;
  }

  @Override
  public <V extends View<T>> V requireView(Class<?> viewClass) {
    View<T> view = view(viewClass);

    if(viewClass.isInstance(view)) {
      @SuppressWarnings("unchecked")
      V cast = (V)viewClass.cast(view);

      return cast;
    }

    throw new IllegalStateException("Domain does not support view: " + viewClass.getSimpleName());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Domain<T> nullable() {
    return new DomainImpl<>(
      validator,
      true,
      false,
      views.values().stream().map(this::toNullable).toArray(View[]::new)
    );
  }

  @Override
  public boolean isEmpty() {
    return empty;
  }

  @Override
  public String toString() {
    return "Domain[includesNull=" + includesNull + "; views=" + views + "]";
  }

  private View<T> toNullable(View<T> view) {
    return switch(view) {
      case NormalizedView<T> nv -> new NormalizedView<>() {
        @Override
        public T snap(T value) {
          return value == null ? null : nv.snap(value);
        }
      };
      default -> view;
    };
  }
}
