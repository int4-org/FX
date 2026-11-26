package org.int4.fx.values.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.util.Subscription;

import org.int4.common.collection.cow.BTreeList;
import org.int4.fx.values.domain.Edit.IndexedChange;

/**
 * A domain of values backed by an ordered, immutable list.
 * <p>
 * A list domain is editable: {@link #editor()} returns an editor through which
 * items can be added, removed or replaced. Each edit produces a new domain and
 * publishes the change as a transition.
 *
 * @param <T> the value type
 */
public final class ListDomain<T> implements Domain<T> {
  private static final Membership.Excluded MISSING_EXCLUSION = new Membership.Excluded(DomainTemplates.MISSING);
  private static final View<?> NOOP_VIEW = new View<>() {};

  /**
   * Creates a list domain from the supplied items.
   *
   * @param <T> the value type
   * @param items the items, cannot be {@code null} but can be empty
   * @return a new list domain, never {@code null}
   * @throws NullPointerException if {@code items} is {@code null}
   */
  @SafeVarargs
  public static <T> ListDomain<T> of(T... items) {
    return from(Arrays.asList(items));
  }

  /**
   * Creates a list domain backed by the supplied list. The list is copied, so
   * later modifications to the supplied list do not affect the returned domain.
   *
   * @param <T> the value type
   * @param items the items, cannot be {@code null} but can be empty
   * @return a new list domain, never {@code null}
   * @throws NullPointerException if {@code items} is {@code null}
   */
  public static <T> ListDomain<T> from(List<? extends T> items) {
    return new ListDomain<>(new BTreeList<>(Objects.requireNonNull(items, "items")), false);
  }

  /**
   * The items in this domain. This list is frozen, so it can be shared with
   * views and other domains without copying; a modification requires a fork
   * or snapshot first.
   */
  private final BTreeList<T> items;

  private final boolean includesNull;
  private final Membership.Excluded notContained;

  private volatile IndexedView<T> indexedView;

  private ListDomain(BTreeList<T> items, boolean includesNull) {
    items.freeze();

    this.items = items;
    this.includesNull = includesNull;
    this.notContained = new Membership.Excluded(DomainTemplates.notContained(items));
  }

  @Override
  public boolean contains(T value) {
    return evaluate(value).isMember();
  }

  @Override
  public Membership evaluate(T value) {
    if(value == null) {
      return includesNull ? Membership.MEMBER : MISSING_EXCLUSION;
    }

    return items.contains(value) ? Membership.MEMBER : notContained;
  }

  @Override
  public ListDomain<T> nullable() {
    return new ListDomain<>(items, true);
  }

  @Override
  public View<T> view(Class<?>... classes) {
    for(Class<?> cls : classes) {
      if(View.class.isAssignableFrom(cls) && cls.isAssignableFrom(IndexedView.class)) {
        return indexedView();
      }
    }

    @SuppressWarnings("unchecked")
    View<T> noop = (View<T>)NOOP_VIEW;

    return noop;
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

  @Override
  public <E extends Editor<T>> E requireEditor(Class<?> editorClass) {
    if(editorClass == ListEditor.class) {
      @SuppressWarnings("unchecked")
      E editor = (E)editor();

      return editor;
    }

    throw new IllegalStateException("Domain does not support editor: " + editorClass.getSimpleName());
  }

  /**
   * Creates a new editor for this domain.
   * <p>
   * Each call returns a new, independent editor. Editors never see each other's
   * edits, although all of them start from this domain.
   *
   * @return a new list editor, never {@code null}
   */
  public ListEditor<T> editor() {
    return new ListEditorImpl<>(this);
  }

  @Override
  public String toString() {
    return "ListDomain[includesNull=" + includesNull + "; size=" + items.size() + "]";
  }

  private IndexedView<T> indexedView() {
    IndexedView<T> view = indexedView;

    if(view == null) {
      indexedView = view = new ListIndexedView<>(items);
    }

    return view;
  }

  private static final class ListIndexedView<T> extends AbstractIndexedView<T> {
    private final List<T> items;

    ListIndexedView(List<T> items) {
      this.items = items;
    }

    @Override
    public T get(long index) {
      Objects.checkIndex(index, size());

      return items.get((int)index);
    }

    @Override
    public long indexOf(T value) {
      return items.indexOf(value);
    }

    @Override
    public long size() {
      return items.size();
    }

    @Override
    public List<T> asList() {
      return items;
    }
  }

  private static final class ListEditorImpl<T> implements ListEditor<T> {
    private final List<Consumer<DomainTransition<T>>> subscribers = new ArrayList<>();

    private BTreeList<T> items;
    private ListDomain<T> domain;

    private int batchDepth;
    private List<IndexedChange<T>> batchChanges;

    ListEditorImpl(ListDomain<T> domain) {
      this.domain = domain;
      this.items = domain.items.fork();
    }

    @Override
    public Domain<T> get() {
      return domain;
    }

    @Override
    public long size() {
      return items.size();
    }

    @Override
    public T get(long index) {
      Objects.checkIndex(index, items.size());

      return items.get((int)index);
    }

    @Override
    public void add(long index, T element) {
      Objects.checkIndex(index, items.size() + 1);

      items.add((int)index, element);

      if(batchDepth > 0) {
        batchChanges.add(new IndexedChange.Insert<>(index, Collections.singletonList(element)));

        return;
      }

      update(new Edit.Indexed<>(List.of(
        new IndexedChange.Insert<>(index, Collections.singletonList(element))
      )));
    }

    @Override
    public T remove(long index) {
      Objects.checkIndex(index, items.size());

      T removed = items.remove((int)index);

      if(batchDepth > 0) {
        batchChanges.add(new IndexedChange.Remove<>(index, Collections.singletonList(removed)));

        return removed;
      }

      update(new Edit.Indexed<>(List.of(
        new IndexedChange.Remove<>(index, Collections.singletonList(removed))
      )));

      return removed;
    }

    @Override
    public void clear() {
      if(items.isEmpty()) {
        return;
      }

      List<T> removed = items.snapshot();

      items.clear();

      if(batchDepth > 0) {
        batchChanges.add(new IndexedChange.Remove<>(0, removed));

        return;
      }

      update(new Edit.Indexed<>(List.of(
        new IndexedChange.Remove<>(0, removed)
      )));
    }

    @Override
    public void batch(Runnable action) {
      Objects.requireNonNull(action, "action");

      if(batchDepth++ > 0) {
        try {
          action.run();
        }
        finally {
          batchDepth--;
        }

        return;
      }

      List<IndexedChange<T>> changes = batchChanges = new ArrayList<>();

      try {
        action.run();
      }
      catch(Throwable t) {

        /*
         * Edits are applied in place; a fork of the last committed domain
         * restores the working list and leaves it modifiable.
         */

        items = domain.items.fork();

        throw t;
      }
      finally {
        batchDepth = 0;
        batchChanges = null;
      }

      if(!changes.isEmpty()) {
        update(new Edit.Indexed<>(changes));
      }
    }

    @Override
    public Subscription transitions(Consumer<DomainTransition<T>> consumer) {
      Objects.requireNonNull(consumer, "consumer");

      subscribers.add(consumer);

      return () -> subscribers.remove(consumer);
    }

    private void update(Edit<T> edit) {
      Domain<T> previous = domain;

      domain = new ListDomain<>(items.snapshot(), previous.allowsNull());

      publish(previous, edit);
    }

    private void publish(Domain<T> previous, Edit<T> edit) {
      if(!subscribers.isEmpty()) {
        DomainTransition<T> transition = new DomainTransition<>(previous, domain, edit);

        for(Consumer<DomainTransition<T>> subscriber : List.copyOf(subscribers)) {
          subscriber.accept(transition);
        }
      }
    }
  }
}
