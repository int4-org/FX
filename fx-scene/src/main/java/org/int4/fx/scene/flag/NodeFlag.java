package org.int4.fx.scene.flag;

import java.util.Objects;

/**
 * A typed key for a value stored on a {@link javafx.scene.Node} with
 * {@link NodeFlags}.
 * <p>
 * The key can be any stable object. If the key is a
 * {@link javafx.css.PseudoClass}, {@link NodeFlags#apply} also applies or
 * clears that pseudo-class on the node. A flag itself is only a typed view;
 * values are stored under {@link #key()}, so a raw key and a flag wrapping the
 * same key address the same value.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public final class NodeFlag<K, V> {
  private final K key;

  private NodeFlag(K key) {
    this.key = Objects.requireNonNull(key, "key");
  }

  /**
   * Creates a flag using the given key.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param key the stable key, cannot be {@code null}
   * @return a new flag, never {@code null}
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public static <K, V> NodeFlag<K, V> of(K key) {
    return new NodeFlag<>(key);
  }

  /**
   * Returns the key for this flag.
   *
   * @return the key, never {@code null}
   */
  public K key() {
    return key;
  }
}
