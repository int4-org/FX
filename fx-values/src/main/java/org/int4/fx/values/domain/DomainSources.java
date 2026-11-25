package org.int4.fx.values.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.util.Subscription;

/**
 * Factory methods for standard {@link DomainSource} implementations.
 */
public final class DomainSources {

  /**
   * Creates a source with a fixed domain that never publishes transitions.
   *
   * @param <T> the value type
   * @param domain the domain, cannot be {@code null}
   * @return a source with a fixed domain, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  public static <T> DomainSource<T> of(Domain<T> domain) {
    return new Fixed<>(Objects.requireNonNull(domain, "domain"));
  }

  /**
   * Creates a source with the inapplicable domain that never publishes
   * transitions.
   *
   * @param <T> the value type
   * @return a source with the inapplicable domain, never {@code null}
   */
  public static <T> DomainSource<T> inapplicable() {
    return of(Domain.inapplicable());
  }

  /**
   * Creates a source whose domain can be replaced.
   *
   * @param <T> the value type
   * @param domain the initial domain, cannot be {@code null}
   * @return a source with a replaceable domain, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  public static <T> Mutable<T> mutable(Domain<T> domain) {
    return new MutableSource<>(Objects.requireNonNull(domain, "domain"));
  }

  /**
   * A {@link DomainSource} whose domain can be replaced.
   * <p>
   * Each replacement publishes a transition with an {@link Edit.Replaced} edit.
   *
   * @param <T> the value type
   */
  public interface Mutable<T> extends DomainSource<T> {

    /**
     * Replaces the current domain.
     *
     * @param domain the new domain, cannot be {@code null}
     * @throws NullPointerException if {@code domain} is {@code null}
     */
    void set(Domain<T> domain);
  }

  private static final class Fixed<T> implements DomainSource<T> {
    private final Domain<T> domain;

    Fixed(Domain<T> domain) {
      this.domain = domain;
    }

    @Override
    public Domain<T> get() {
      return domain;
    }

    @Override
    public Subscription transitions(Consumer<DomainTransition<T>> consumer) {
      Objects.requireNonNull(consumer, "consumer");

      return Subscription.EMPTY;
    }
  }

  private static final class MutableSource<T> implements Mutable<T> {
    private final List<Consumer<DomainTransition<T>>> subscribers = new ArrayList<>();

    private Domain<T> domain;

    MutableSource(Domain<T> domain) {
      this.domain = domain;
    }

    @Override
    public Domain<T> get() {
      return domain;
    }

    @Override
    public void set(Domain<T> domain) {
      Objects.requireNonNull(domain, "domain");

      Domain<T> previous = this.domain;

      if(domain == previous) {
        return;  // no change
      }

      this.domain = domain;

      if(!subscribers.isEmpty()) {
        DomainTransition<T> transition = new DomainTransition<>(previous, domain, new Edit.Replaced<>());

        for(Consumer<DomainTransition<T>> subscriber : List.copyOf(subscribers)) {
          subscriber.accept(transition);
        }
      }
    }

    @Override
    public Subscription transitions(Consumer<DomainTransition<T>> consumer) {
      Objects.requireNonNull(consumer, "consumer");

      subscribers.add(consumer);

      return () -> subscribers.remove(consumer);
    }
  }

  private DomainSources() {}
}
