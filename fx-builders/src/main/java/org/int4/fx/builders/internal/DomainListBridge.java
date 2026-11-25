package org.int4.fx.builders.internal;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.util.Subscription;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource;
import org.int4.fx.values.domain.DomainSource.DomainTransition;
import org.int4.fx.values.domain.Edit;
import org.int4.fx.values.domain.Edit.IndexedChange;
import org.int4.fx.values.domain.IndexedView;

/**
 * A read-only {@link ObservableList} that mirrors the items of a domain.
 * <p>
 * Item access delegates to the current domain's {@link IndexedView}, so the
 * items are not copied. Domain changes are published to listeners as list
 * change events: indexed edits are replayed exactly, while edits without
 * incremental information are reported as a replacement of the range that
 * differs between the previous and successor views.
 * <p>
 * Instances are created through one of two factories:
 * <ul>
 *   <li>{@link #of(Domain)} creates a plain mirror at the given domain; it holds
 *       no subscription and does not follow any source.</li>
 *   <li>{@link #link(DomainSource)} makes the list follow a source. This
 *       instance is retained by the source until {@link #close()} is called, so
 *       the caller must close it when the list is no longer needed.</li>
 * </ul>
 *
 * @param <T> the element type
 */
public final class DomainListBridge<T> extends ObservableListBase<T> {

  /**
   * Creates a mirror at the given domain that does not follow a source.
   * <p>
   * Changes can be supplied by the caller through
   * {@link #apply(DomainTransition)}.
   *
   * @param <T> the element type
   * @param domain the initial domain, cannot be {@code null}
   * @return a list showing the items of the supplied domain, never {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  public static <T> DomainListBridge<T> of(Domain<T> domain) {
    return new DomainListBridge<>(domain);
  }

  /**
   * Creates a mirror that follows the given source until it is closed.
   * <p>
   * The returned instance is referenced by the source, so dropping it without
   * calling {@link #close()} keeps it alive.
   *
   * @param <T> the element type
   * @param source the source to follow, cannot be {@code null}
   * @return a list following the source, never {@code null}
   * @throws NullPointerException if {@code source} is {@code null}
   */
  static <T> DomainListBridge<T> link(DomainSource<T> source) {
    Objects.requireNonNull(source, "source");

    DomainListBridge<T> bridge = new DomainListBridge<>(source.get());

    bridge.subscription = source.transitions(bridge::apply);

    return bridge;
  }

  private List<T> items;
  private Subscription subscription = Subscription.EMPTY;

  private DomainListBridge(Domain<T> domain) {
    items = itemsOf(Objects.requireNonNull(domain, "domain"));
  }

  /**
   * Stops following the source this list was linked to, if any. Calling this on
   * a list created with {@link #of(Domain)} has no effect.
   */
  void close() {
    subscription.unsubscribe();
  }

  @Override
  public T get(int index) {
    return items.get(index);
  }

  @Override
  public int size() {
    return items.size();
  }

  /**
   * Applies a domain transition to this list.
   *
   * @param transition the transition, cannot be {@code null}
   * @throws NullPointerException if {@code transition} is {@code null}
   */
  public void apply(DomainTransition<T> transition) {
    Objects.requireNonNull(transition, "transition");

    List<T> previous = items;

    items = itemsOf(transition.successor());

    beginChange();

    try {
      switch(transition.edit()) {
        case Edit.Indexed<T> indexed -> apply(indexed);
        case Edit.Replaced<T> _ -> replace(previous);
      }
    }
    finally {
      endChange();
    }
  }

  private void apply(Edit.Indexed<T> edit) {
    for(IndexedChange<T> change : edit.changes()) {
      switch(change) {
        case IndexedChange.Insert<T> insert -> {
          int index = (int)insert.index();

          nextAdd(index, index + insert.items().size());
        }
        case IndexedChange.Remove<T> remove -> nextRemove((int)remove.index(), remove.items());
        case IndexedChange.Set<T> set -> nextSet((int)set.index(), set.previous());
      }
    }
  }

  private void replace(List<T> previous) {
    int prefix = commonPrefix(previous, items);
    int suffix = commonSuffix(previous, items, prefix);
    int previousEnd = previous.size() - suffix;
    int itemsEnd = items.size() - suffix;

    if(prefix < previousEnd) {
      nextRemove(prefix, previous.subList(prefix, previousEnd));
    }
    if(prefix < itemsEnd) {
      nextAdd(prefix, itemsEnd);
    }
  }

  private static <T> int commonPrefix(List<T> left, List<T> right) {
    int max = Math.min(left.size(), right.size());
    int index = 0;

    while(index < max && Objects.equals(left.get(index), right.get(index))) {
      index++;
    }

    return index;
  }

  private static <T> int commonSuffix(List<T> left, List<T> right, int prefix) {
    int max = Math.min(left.size(), right.size()) - prefix;
    int suffix = 0;

    while(suffix < max && Objects.equals(left.get(left.size() - 1 - suffix), right.get(right.size() - 1 - suffix))) {
      suffix++;
    }

    return suffix;
  }

  private static <T> List<T> itemsOf(Domain<T> domain) {
    return switch(domain.view(IndexedView.class)) {
      case IndexedView<T> view -> view.asList();
      default -> List.of();
    };
  }
}
