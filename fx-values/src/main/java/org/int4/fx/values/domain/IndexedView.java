package org.int4.fx.values.domain;

import java.util.AbstractList;
import java.util.List;

/**
 * A view exposing index-based access to discrete domains.
 * <p>
 * The {@link IndexedView} provides methods to obtain the value for a
 * particular index, determine the index of a value (snapping is allowed),
 * and enumerate the domain as a {@link List} via {@link #asList()}.
 *
 * @param <T> the type of values in the domain
 */
public interface IndexedView<T> extends View<T> {

  /**
   * Returns the element at the given index.
   *
   * @param index an index between 0 and {@link #size()} - 1, cannot be negative
   * @return the element at the given index
   * @throws IndexOutOfBoundsException if index is negative or not smaller than {@link #size()}
   */
  T get(long index);

  /**
   * Returns the index of the given value within this view. If the value is
   * invalid it may be normalized (snapped) before an index is returned; if
   * it cannot be normalized {@code -1} is returned.
   *
   * @param value a value to locate in this view
   * @return the index or {@code -1} when not found
   */
  long indexOf(T value);

  /**
   * Returns the size of the associated domain.
   *
   * @return the size of the associated domain, never negative
   */
  long size();

  /**
   * Returns an immutable list representation of this indexed view, if its
   * size does not exceed {@code Integer#MAX_VALUE}.
   *
   * @return an immutable list of values, never {@code null}
   * @throws UnsupportedOperationException if list size would exceed {@code Integer#MAX_VALUE}
   */
  default List<T> asList() {
    if(size() > Integer.MAX_VALUE) {
      throw new UnsupportedOperationException("view is too large to represent as a List: " + size());
    }

    return new AbstractList<>() {  // TODO could calculate this once
      @Override
      public T get(int index) {
        return IndexedView.this.get(index);
      }

      @SuppressWarnings("unchecked")
      @Override
      public int indexOf(Object o) {
        try {
          return (int)IndexedView.this.indexOf((T)o);
        }
        catch(ClassCastException e) {
          return -1;
        }
      }

      @Override
      public int size() {
        return (int)IndexedView.this.size();
      }
    };
  }
}