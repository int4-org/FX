package org.int4.fx.values.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Describes how the successor domain was produced from its predecessor.
 *
 * @param <T> the value type
 */
public sealed interface Edit<T> {

  /**
   * Indicates that no incremental edit information is available.
   * Consumers should use the successor domain directly.
   *
   * @param <T> the value type
   */
  record Replaced<T>() implements Edit<T> {}

  /**
   * An edit expressed in terms of an indexed view.
   * <p>
   * The changes are applied in order. Each index is relative to the list state
   * immediately before that change.
   *
   * @param <T> the value type
   * @param changes the changes, cannot be {@code null} but can be empty
   */
  record Indexed<T>(List<IndexedChange<T>> changes) implements Edit<T> {

    /**
     * Constructs a new instance.
     */
    public Indexed {
      Objects.requireNonNull(changes, "changes");

      changes = Collections.unmodifiableList(new ArrayList<>(changes));
    }
  }

  /**
   * A single change to an indexed view.
   * <p>
   * Changes carry the items they affect so that consumers can apply them
   * without access to intermediate views.
   *
   * @param <T> the value type
   */
  sealed interface IndexedChange<T> permits IndexedChange.Insert, IndexedChange.Remove, IndexedChange.Set {

    /**
     * Inserts one or more items at the given index.
     * <p>
     * The items are not copied; the supplied list must not be modified after
     * construction.
     *
     * @param <T> the value type
     * @param index the insertion index
     * @param items the inserted items, cannot be {@code null} or empty
     */
    record Insert<T>(long index, List<T> items) implements IndexedChange<T> {

      /**
       * Constructs a new instance.
       */
      public Insert {
        if(index < 0) {
          throw new IllegalArgumentException("index must not be negative");
        }

        items = readOnly(Objects.requireNonNull(items, "items"));

        if(items.isEmpty()) {
          throw new IllegalArgumentException("items must not be empty");
        }
      }
    }

    /**
     * Removes one or more items at the given index.
     * <p>
     * The items are not copied; the supplied list must not be modified after
     * construction.
     *
     * @param <T> the value type
     * @param index the removal index
     * @param items the removed items, cannot be {@code null} or empty
     */
    record Remove<T>(long index, List<T> items) implements IndexedChange<T> {

      /**
       * Constructs a new instance.
       */
      public Remove {
        if(index < 0) {
          throw new IllegalArgumentException("index must not be negative");
        }

        items = readOnly(Objects.requireNonNull(items, "items"));

        if(items.isEmpty()) {
          throw new IllegalArgumentException("items must not be empty");
        }
      }
    }

    /**
     * Replaces the item at the given index.
     *
     * @param <T> the value type
     * @param index the replacement index
     * @param previous the replaced item, can be {@code null}
     * @param item the replacement item, can be {@code null}
     */
    record Set<T>(long index, T previous, T item) implements IndexedChange<T> {

      /**
       * Constructs a new instance.
       */
      public Set {
        if(index < 0) {
          throw new IllegalArgumentException("index must not be negative");
        }
      }
    }

    private static <T> List<T> readOnly(List<T> items) {
      return Collections.unmodifiableList(items);
    }
  }
}
