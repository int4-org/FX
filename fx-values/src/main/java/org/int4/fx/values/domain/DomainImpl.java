package org.int4.fx.values.domain;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class DomainImpl<T> implements Domain<T> {
  static final Domain<?> NON_NULL = Domain.where(v -> true);
  static final Domain<?> ANY = NON_NULL.nullable();
  static final Domain<?> INAPPLICABLE = new DomainImpl<>(Rule.of(v -> false, DomainTemplates.INAPPLICABLE), false);

  private static final View<?> NOOP_VIEW = new View<>() {};

  /**
   * The rules for this domain. In order for a value to be considered to
   * be part of this domain, it must either be {@code null} and the domain allows
   * {@code null}, or it must pass all the rules. Note that {@code null} is never
   * passed to any rule. If {@code null} is allowed, and the value is {@code null}
   * this bypasses all other rules.
   */
  private final List<Rule<T>> rules;

  private final Map<Class<?>, View<T>> views = new LinkedHashMap<>();  // order is important when making a copy for nullable
  private final boolean includesNull;

  @SafeVarargs
  DomainImpl(Rule<T> rule, boolean includesNull, View<T>... views) {
    this(List.of(rule), includesNull, views);
  }

  @SafeVarargs
  DomainImpl(List<Rule<T>> rules, boolean includesNull, View<T>... views) {
    this.rules = List.copyOf(rules);
    this.includesNull = includesNull;

    // TODO probably should only be looking for View interfaces (perhaps sealed type?)
    for(View<T> view : views) {
      ArrayDeque<Class<?>> toScan = new ArrayDeque<>();
      Class<?> cls = view.getClass();

      while (cls != null) {
        toScan.addAll(Arrays.asList(cls.getInterfaces()));
        cls = cls.getSuperclass();
      }

      while(!toScan.isEmpty()) {
        Class<?> iface = toScan.pop();

        this.views.putIfAbsent(iface, view);

        toScan.addAll(Arrays.asList(iface.getInterfaces()));
      }
    }
  }

  @Override
  public boolean contains(T value) {
    if(value == null) {
      return includesNull;
    }

    for(Predicate<T> rule : rules) {
      if(!rule.test(value)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public Membership evaluate(T value) {
    if(value == null) {
      return includesNull ? new Membership.Member() : new Membership.Excluded(DomainTemplates.MISSING);
    }

    for(Rule<T> rule : rules) {
      if(!rule.test(value)) {
        return new Membership.Excluded(rule.template());
      }
    }

    return new Membership.Member();
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
      rules,
      true,
      views.values().stream().map(this::toNullable).toArray(View[]::new)
    );
  }

  @Override
  public String toString() {
    return this == INAPPLICABLE
      ? "Domain[inapplicable]"
      : "Domain[includesNull=" + includesNull + "; views=" + views + "]";  // TODO once there are rules, consider logging those instead of the views
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
