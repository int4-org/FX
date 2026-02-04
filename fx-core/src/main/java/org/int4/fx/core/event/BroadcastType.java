package org.int4.fx.core.event;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Represents a type associated with a {@link BroadcastEvent}.
 * <p>
 * Broadcast types form a hierarchy with {@link #ROOT} as its root. A handler
 * may be registered on any event type to receive that type and all its subtypes.
 *
 * @param <T> the broadcast event class to which this type applies
 */
public class BroadcastType<T extends BroadcastEvent> {
  private static final Map<Key, Object> KNOWN_TYPES = new WeakHashMap<>();

  /**
   * The root broadcast type.
   */
  public static final BroadcastType<BroadcastEvent> ROOT = new BroadcastType<>(null, "ROOT");

  private final BroadcastType<? super T> parent;
  private final String name;

  /**
   * Creates a new broadcast type, with the given name, directly under the root type.
   *
   * @param <T> the broadcast event class to which this type applies
   * @param name a name, cannot be {@code null}
   * @return a new broadcast type, never {@code null}
   * @throws IllegalArgumentException if a type with the same name already exists as a subtype of the root type
   */
  public static <T extends BroadcastEvent> BroadcastType<T> of(String name) {
    return new BroadcastType<>(ROOT, name);
  }

  /**
   * Creates a new broadcast type, with the given name, directly under the given parent type.
   *
   * @param <T> the broadcast event class to which this type applies
   * @param parent a parent broadcast type, cannot be {@code null}
   * @param name a name, cannot be {@code null}
   * @return a new broadcast type, never {@code null}
   * @throws IllegalArgumentException if a type with the same name already exists as a subtype of the given parent type
   */
  public static <T extends BroadcastEvent> BroadcastType<T> of(BroadcastType<? super T> parent, String name) {
    return new BroadcastType<>(Objects.requireNonNull(parent, "parent"), name);
  }

  private BroadcastType(BroadcastType<? super T> parent, String name) {
    this.parent = parent;
    this.name = Objects.requireNonNull(name, "name");

    if(KNOWN_TYPES.putIfAbsent(new Key(parent, name), ROOT) != null) {
      throw new IllegalArgumentException("broadcast type already exists: " + parent + " > " + name);
    }
  }

  /**
   * Returns the name of this broadcast type.
   *
   * @return the name of this broadcast type, never {@code null}
   */
  public String name() {
    return name;
  }

  /**
   * Returns the parent broadcast type of this broadcast type. If this type is
   * the root, returns {@code null}.
   *
   * @return the parent broadcast type, can be {@code null} if this is the root type
   */
  public BroadcastType<? super T> parent() {
    return parent;
  }

  @Override
  public String toString() {
    return parent == null ? "ROOT" : parent.toString() + " > " + name;
  }

  record Key(BroadcastType<?> parent, String name) {}
}