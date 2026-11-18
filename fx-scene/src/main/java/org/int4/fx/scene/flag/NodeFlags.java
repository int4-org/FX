package org.int4.fx.scene.flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Subscription;

/**
 * Stores optional, typed values on nodes and notifies observers of changes.
 * <p>
 * Values are stored in the node's properties map, so they survive scene
 * changes and can be queried from anywhere. An optional pseudo-class is
 * applied when the key is a {@link PseudoClass}. Changes are delivered as
 * change notifications: on value changes and when a flagged node is
 * attached to a scene. On scene detach the notification is fired on the old
 * scene.
 * <p>
 * All methods must be called on the JavaFX application thread.
 */
public final class NodeFlags {
  private static final Object STATE_KEY = new Object();

  private NodeFlags() {
  }

  /**
   * Stores the given value for the given flag on the node.
   * <p>
   * A {@code null} value removes the flag. If the flag's key is a
   * {@link PseudoClass}, the pseudo-class is applied for a non-null value and
   * cleared for a {@code null} value.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param node the node to update, cannot be {@code null}
   * @param flag the flag to update, cannot be {@code null}
   * @param value the new value, can be {@code null}
   * @throws NullPointerException if {@code node} or {@code flag} is {@code null}
   */
  public static <K, V> void apply(Node node, NodeFlag<K, V> flag, V value) {
    apply(node, flag.key(), value);
  }

  /**
   * Stores the given value for the given key on the node.
   * <p>
   * This is the untyped variant of {@link #apply(Node, NodeFlag, Object)}.
   *
   * @param node the node to update, cannot be {@code null}
   * @param key the key to update, cannot be {@code null}
   * @param value the new value, can be {@code null}
   * @throws NullPointerException if {@code node} or {@code key} is {@code null}
   */
  private static void apply(Node node, Object key, Object value) {
    Objects.requireNonNull(node, "node");
    Objects.requireNonNull(key, "key");

    State state = state(node, value != null);

    if(value == null) {
      if(state == null) {
        return;
      }

      Object oldValue = state.values.remove(key);

      if(oldValue == null) {
        return;
      }

      if(key instanceof PseudoClass pseudoClass) {
        node.pseudoClassStateChanged(pseudoClass, false);
      }

      fire(node, key, null);

      if(state.values.isEmpty()) {
        node.getProperties().remove(STATE_KEY);
        state.sceneSubscription.unsubscribe();
      }
    }
    else {
      Object oldValue = state.values.put(key, value);

      if(key instanceof PseudoClass pseudoClass) {
        node.pseudoClassStateChanged(pseudoClass, true);
      }

      if(!Objects.equals(oldValue, value)) {
        fire(node, key, value);
      }
    }
  }

  /**
   * Returns the value stored for the given flag, or {@code null} if absent.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param node the node to query, cannot be {@code null}
   * @param flag the flag to query, cannot be {@code null}
   * @return the value, or {@code null} if absent
   * @throws NullPointerException if {@code node} or {@code flag} is {@code null}
   */
  public static <K, V> V get(Node node, NodeFlag<K, V> flag) {
    return cast(get(node, flag.key()));
  }

  /**
   * Returns the value stored for the given key, or {@code null} if absent.
   *
   * @param node the node to query, cannot be {@code null}
   * @param key the key to query, cannot be {@code null}
   * @return the value, or {@code null} if absent
   * @throws NullPointerException if {@code node} or {@code key} is {@code null}
   */
  private static Object get(Node node, Object key) {
    Objects.requireNonNull(node, "node");
    Objects.requireNonNull(key, "key");

    State state = (State)node.getProperties().get(STATE_KEY);

    return state == null ? null : state.values.get(key);
  }

  /**
   * Returns all nodes in the subtree rooted at {@code root} that have a value
   * for the given flag.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param root the subtree root, cannot be {@code null}
   * @param flag the flag to search for, cannot be {@code null}
   * @return the matching nodes, never {@code null}
   * @throws NullPointerException if {@code root} or {@code flag} is {@code null}
   */
  public static <K, V> List<Node> nodes(Node root, NodeFlag<K, V> flag) {
    return nodes(root, flag.key());
  }

  /**
   * Returns all nodes in the subtree rooted at {@code root} that have a value
   * for the given key.
   *
   * @param root the subtree root, cannot be {@code null}
   * @param key the key to search for, cannot be {@code null}
   * @return the matching nodes, never {@code null}
   * @throws NullPointerException if {@code root} or {@code key} is {@code null}
   */
  private static List<Node> nodes(Node root, Object key) {
    Objects.requireNonNull(root, "root");
    Objects.requireNonNull(key, "key");

    List<Node> result = new ArrayList<>();

    collect(root, key, result);

    return result;
  }

  /**
   * Observes the given subtree for nodes with the given flag.
   * <p>
   * The callbacks are invoked for nodes present at registration time, for
   * subsequent changes, and when a flagged node leaves the observed subtree
   * or the subtree's scene.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param root the subtree root, cannot be {@code null}
   * @param flag the flag to observe, cannot be {@code null}
   * @param present called with the node and value when present or changed, cannot be {@code null}
   * @param absent called with the node when the value is removed or the node leaves the subtree or scene, cannot be {@code null}
   * @return a subscription that stops observing, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <K, V> Subscription observe(Node root, NodeFlag<K, V> flag, BiConsumer<Node, V> present, Consumer<Node> absent) {
    Objects.requireNonNull(root, "root");
    Objects.requireNonNull(flag, "flag");
    Objects.requireNonNull(present, "present");
    Objects.requireNonNull(absent, "absent");

    return new SubtreeObservation<>(root, flag.key(), present, absent).start();
  }

  /**
   * Observes the given scene for nodes with the given flag.
   * <p>
   * The callbacks are invoked for nodes present at registration time, for
   * subsequent changes, and when flagged nodes are attached to or detached
   * from the scene.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param scene the scene to observe, cannot be {@code null}
   * @param flag the flag to observe, cannot be {@code null}
   * @param present called with the node and value when present or changed, cannot be {@code null}
   * @param absent called with the node when the value is removed or the node leaves the scene, cannot be {@code null}
   * @return a subscription that stops observing, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <K, V> Subscription observe(Scene scene, NodeFlag<K, V> flag, BiConsumer<Node, V> present, Consumer<Node> absent) {
    Objects.requireNonNull(scene, "scene");
    Objects.requireNonNull(flag, "flag");
    Objects.requireNonNull(present, "present");
    Objects.requireNonNull(absent, "absent");

    Object key = flag.key();
    Node root = scene.getRoot();

    if(root != null) {
      for(Node node : nodes(root, key)) {
        present.accept(node, cast(get(node, key)));
      }
    }

    EventHandler<FlagChangedEvent> handler = event -> {
      if(key.equals(event.key())) {
        V value = cast(event.newValue());

        if(value == null) {
          absent.accept(event.node());
        }
        else {
          present.accept(event.node(), value);
        }
      }
    };

    scene.addEventFilter(FlagChangedEvent.CHANGED, handler);

    return () -> scene.removeEventFilter(FlagChangedEvent.CHANGED, handler);
  }

  private static final class SubtreeObservation<V> {
    private final Node root;
    private final Object key;
    private final BiConsumer<Node, V> present;
    private final Consumer<Node> absent;
    private final Map<Node, Object> reported = new HashMap<>();
    private final EventHandler<FlagChangedEvent> rootHandler;
    private final EventHandler<FlagChangedEvent> sceneHandler;

    private Subscription sceneFilter = Subscription.EMPTY;
    private Subscription rootSceneSubscription;

    SubtreeObservation(Node root, Object key, BiConsumer<Node, V> present, Consumer<Node> absent) {
      this.root = root;
      this.key = key;
      this.present = present;
      this.absent = absent;

      this.rootHandler = event -> {
        if(key.equals(event.key())) {
          V value = cast(event.newValue());

          if(value == null) {
            absentNode(event.node());
          }
          else {
            presentNode(event.node(), value);
          }
        }
      };

      this.sceneHandler = event -> {
        if(key.equals(event.key()) && event.newValue() == null && reported.containsKey(event.node())) {
          absentNode(event.node());
        }
      };
    }

    Subscription start() {
      root.addEventFilter(FlagChangedEvent.CHANGED, rootHandler);

      Scene scene = root.getScene();

      if(scene != null) {
        installSceneFilter(scene);
      }

      rootSceneSubscription = root.sceneProperty().subscribe((oldScene, newScene) -> sceneChanged(oldScene, newScene));

      scan();

      return this::stop;
    }

    private void sceneChanged(Scene oldScene, Scene newScene) {
      if(oldScene == newScene) {
        return;
      }

      removeSceneFilter();

      if(oldScene != null) {
        absentAll();
      }

      if(newScene != null) {
        installSceneFilter(newScene);
        scan();
      }
    }

    private void scan() {
      List<Node> current = nodes(root, key);

      for(Node node : List.copyOf(reported.keySet())) {
        if(!current.contains(node)) {
          absentNode(node);
        }
      }

      for(Node node : current) {
        presentNode(node, cast(get(node, key)));
      }
    }

    private void presentNode(Node node, V value) {
      Object previous = reported.put(node, value);

      if(!Objects.equals(previous, value)) {
        present.accept(node, value);
      }
    }

    private void absentNode(Node node) {
      if(reported.remove(node) != null) {
        absent.accept(node);
      }
    }

    private void absentAll() {
      for(Node node : List.copyOf(reported.keySet())) {
        absentNode(node);
      }
    }

    private void installSceneFilter(Scene scene) {
      scene.addEventFilter(FlagChangedEvent.CHANGED, sceneHandler);
      sceneFilter = () -> scene.removeEventFilter(FlagChangedEvent.CHANGED, sceneHandler);
    }

    private void removeSceneFilter() {
      sceneFilter.unsubscribe();
      sceneFilter = Subscription.EMPTY;
    }

    private void stop() {
      root.removeEventFilter(FlagChangedEvent.CHANGED, rootHandler);
      rootSceneSubscription.unsubscribe();
      removeSceneFilter();
      reported.clear();
    }
  }

  private static void collect(Node node, Object key, List<Node> result) {
    if(get(node, key) != null) {
      result.add(node);
    }

    if(node instanceof Parent parent) {
      for(Node child : parent.getChildrenUnmodifiable()) {
        collect(child, key, result);
      }
    }
  }

  private static State state(Node node, boolean create) {
    State state = (State)node.getProperties().get(STATE_KEY);

    if(state == null && create) {
      state = new State();
      node.getProperties().put(STATE_KEY, state);

      State newState = state;

      state.sceneSubscription = node.sceneProperty().subscribe(
        (oldScene, newScene) -> sceneChanged(node, newState, oldScene, newScene)
      );
    }

    return state;
  }

  private static void sceneChanged(Node node, State state, Scene oldScene, Scene newScene) {
    if(oldScene == newScene) {
      return;
    }

    if(oldScene != null) {
      for(Map.Entry<Object, Object> entry : state.values.entrySet()) {
        Event.fireEvent(oldScene, new FlagChangedEvent(node, entry.getKey(), null));
      }
    }

    if(newScene != null) {
      for(Map.Entry<Object, Object> entry : state.values.entrySet()) {
        fire(node, entry.getKey(), entry.getValue());
      }
    }
  }

  private static void fire(Node node, Object key, Object newValue) {
    Event.fireEvent(node, new FlagChangedEvent(node, key, newValue));
  }

  @SuppressWarnings("unchecked")
  private static <V> V cast(Object value) {
    return (V)value;
  }

  private static final class FlagChangedEvent extends Event {
    static final EventType<FlagChangedEvent> CHANGED = new EventType<>(Event.ANY, "FLAG_CHANGED");

    private final Node node;
    private final Object key;
    private final Object newValue;

    FlagChangedEvent(Node node, Object key, Object newValue) {
      super(CHANGED);

      this.node = Objects.requireNonNull(node, "node");
      this.key = Objects.requireNonNull(key, "key");
      this.newValue = newValue;
    }

    Node node() {
      return node;
    }

    Object key() {
      return key;
    }

    Object newValue() {
      return newValue;
    }
  }

  private static final class State {
    private final Map<Object, Object> values = new HashMap<>();

    private Subscription sceneSubscription;
  }
}
