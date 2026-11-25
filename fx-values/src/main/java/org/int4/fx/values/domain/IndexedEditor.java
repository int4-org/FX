package org.int4.fx.values.domain;

import java.util.function.Consumer;

import javafx.util.Subscription;

/**
 * An {@link Editor} for a list-backed domain that supports indexed edits.
 * <p>
 * An indexed editor edits the items of a list domain. Each edit advances the
 * domain and is published as a {@link org.int4.fx.values.domain.DomainSource.DomainTransition}, which makes the editor
 * a {@link DomainSource}: the current domain is available through {@link #get()}
 * and changes can be observed through {@link #transitions(Consumer)}.
 * <p>
 * Multiple edits can be grouped with {@link #batch(Runnable)}, so subscribers
 * see their combined effect as a single transition.
 *
 * @param <T> the element type
 */
public interface IndexedEditor<T> extends Editor<T>, DomainSource<T> {

  /**
   * Inserts an element at the start of the list.
   *
   * @param element the element to insert, can be {@code null} if the domain allows null
   */
  default void addFirst(T element) {
    add(0, element);
  }

  /**
   * Appends an element to the end of the list.
   *
   * @param element the element to append, can be {@code null} if the domain allows null
   */
  default void addLast(T element) {
    add(element);
  }

  /**
   * Removes and returns the first element.
   *
   * @return the removed element, can be {@code null}
   * @throws IndexOutOfBoundsException if the list is empty
   */
  default T removeFirst() {
    return remove(0);
  }

  /**
   * Removes and returns the last element.
   *
   * @return the removed element, can be {@code null}
   * @throws IndexOutOfBoundsException if the list is empty
   */
  default T removeLast() {
    return remove(size() - 1);
  }

  /**
   * Appends an element to the end of the list.
   *
   * @param element the element to append, can be {@code null} if the domain allows null
   */
  default void add(T element) {
    add(size(), element);
  }

  /**
   * Runs multiple edits as one transaction.
   * <p>
   * Implementations may publish only a single transition after the action
   * completes. If the action throws, implementations should leave the editor
   * unchanged.
   *
   * @param action the action containing the edits, cannot be {@code null}
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default void batch(Runnable action) {
    throw new UnsupportedOperationException("batch not supported");
  }

  /**
   * Subscribes to transitions produced by edits.
   * <p>
   * The subscriber is not called for the editor's current state; it only receives
   * transitions caused by later edits.
   *
   * @param consumer a consumer for transitions, cannot be {@code null}
   * @return a subscription that can be used to cancel the subscription, never {@code null}
   * @throws NullPointerException if {@code consumer} is {@code null}
   */
  @Override
  default Subscription transitions(Consumer<DomainTransition<T>> consumer) {
    throw new UnsupportedOperationException("transitions not supported");
  }

  /**
   * Returns the number of elements in the list.
   *
   * @return the number of elements
   */
  long size();

  /**
   * Removes all elements from the list.
   */
  void clear();

  /**
   * Returns the element at the given index.
   *
   * @param index the index of the element
   * @return the element, can be {@code null}
   * @throws IndexOutOfBoundsException if {@code index} is negative or not less than {@link #size()}
   */
  T get(long index);

  /**
   * Inserts an element at the given index.
   *
   * @param index the insertion index, from {@code 0} to {@link #size()} inclusive
   * @param element the element to insert, can be {@code null} if the domain allows null
   * @throws IndexOutOfBoundsException if {@code index} is negative or greater than {@link #size()}
   */
  void add(long index, T element);

  /**
   * Removes and returns the element at the given index.
   *
   * @param index the index of the element to remove
   * @return the removed element, can be {@code null}
   * @throws IndexOutOfBoundsException if {@code index} is negative or not less than {@link #size()}
   */
  T remove(long index);
}
