package org.int4.fx.scene.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Subscription;

import org.int4.fx.core.event.BroadcastEvent;
import org.int4.fx.core.event.BroadcastHandler;
import org.int4.fx.core.event.BroadcastType;

/**
 * Utility class for broadcasting {@link BroadcastEvent}s to JavaFX nodes.
 * <p>
 * Broadcasts allow an event to be sent to a node and all its descendants.
 * This is especially useful for infrequent events where many nodes may be
 * interested, such as window visibility changes or application locale updates.
 * Handlers are invoked for the specified {@link BroadcastType} and all its
 * subtypes.
 */
public class Broadcasts {
  private static final Object KEY = new Object();

  /**
   * Broadcasts the given event to all nodes in the given scene.
   * <p>
   * The broadcast starts at the scene's root node and traverses the entire
   * scene graph. For each node, handlers registered for the event's {@link BroadcastType}
   * or any of its parent types are invoked.
   *
   * @param scene the scene whose nodes should receive the event, cannot be {@code null}
   * @param event the event to broadcast, cannot be {@code null}
   * @throws NullPointerException if {@code scene} or {@code event} is {@code null}
   */
  public static void broadcast(Scene scene, BroadcastEvent event) {
    broadcast(scene.getRoot(), event);
  }

  /**
   * Broadcasts the given event starting at the specified node.
   * <p>
   * The event is delivered to the given node and all of its descendants.
   * For each node, handlers registered for the event's {@link BroadcastType}
   * or any of its parent types are invoked.
   *
   * @param node the root node of the broadcast, cannot be {@code null}
   * @param event the event to broadcast, cannot be {@code null}
   * @throws NullPointerException if {@code node} or {@code event} is {@code null}
   */
  public static void broadcast(Node node, BroadcastEvent event) {
    Objects.requireNonNull(node, "node");
    Objects.requireNonNull(event, "event");

    if(node instanceof Parent p) {
      for(Node child : p.getChildrenUnmodifiable()) {
        broadcast(child, event);
      }
    }

    if(node.hasProperties() && node.getProperties().get(KEY) instanceof Dispatcher handler) {
      handler.accept(event);
    }
  }

  /**
   * Registers a broadcast handler on the given node and returns a subscription
   * that can be used to remove it.
   *
   * @param <T> the concrete event type
   * @param node the node on which to register the handler, cannot be {@code null}
   * @param type the broadcast type to listen for, cannot be {@code null}
   * @param handler the handler to invoke, cannot be {@code null}
   * @return a {@link Subscription} that removes the handler when unsubscribed, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <T extends BroadcastEvent> Subscription subscribe(Node node, BroadcastType<T> type, BroadcastHandler<T> handler) {
    addHandler(node, type, handler);

    return () -> removeHandler(node, type, handler);
  }

  /**
   * Registers a broadcast handler on the given node for the specified event type.
   * <p>
   * The handler will be invoked when an event of the given type, or any subtype,
   * is broadcast to this node.
   *
   * @param <T> the concrete event type
   * @param node the node on which to register the handler, cannot be {@code null}
   * @param type the broadcast type to listen for, cannot be {@code null}
   * @param handler the handler to invoke, cannot be {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <T extends BroadcastEvent> void addHandler(Node node, BroadcastType<T> type, BroadcastHandler<T> handler) {
    Objects.requireNonNull(node, "node");
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(handler, "handler");

    Dispatcher dispatcher = (Dispatcher)node.getProperties().computeIfAbsent(KEY, k -> new Dispatcher());

    dispatcher.add(type, handler);
  }

  /**
   * Removes a previously registered broadcast handler from the given node.
   *
   * @param node the node from which to remove the handler, cannot be {@code null}
   * @param type the broadcast type the handler was registered for, cannot be {@code null}
   * @param handler the handler to remove, cannot be {@code null}
   * @param <T> the concrete event type
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <T extends BroadcastEvent> void removeHandler(Node node, BroadcastType<T> type, BroadcastHandler<T> handler) {
    Objects.requireNonNull(node, "node");
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(handler, "handler");

    if(node.hasProperties()) {
      if(node.getProperties().get(KEY) instanceof Dispatcher d) {
        d.remove(type, handler);

        if(d.hasNoHandlers()) {
          node.getProperties().remove(KEY);
        }
      }
    }
  }

  static class Dispatcher {
    Map<BroadcastType<?>, List<BroadcastHandler<?>>> handlers = new HashMap<>();

    <T extends BroadcastEvent> void add(BroadcastType<T> type, BroadcastHandler<T> handler) {
      handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    <T extends BroadcastEvent> void remove(BroadcastType<T> type, BroadcastHandler<T> handler) {
      handlers.computeIfPresent(type, (k, v) -> v.remove(handler) && v.isEmpty() ? null : v);
    }

    boolean hasNoHandlers() {
      return handlers.isEmpty();
    }

    void accept(BroadcastEvent event) {
      BroadcastType<?> type = event.type();

      while(type != null) {
        List<BroadcastHandler<?>> list = handlers.get(type);

        if(list != null) {
          for(int i = 0; i < list.size(); i++) {
            try {
              @SuppressWarnings("unchecked")  // safe, verified by broadcast type
              BroadcastHandler<BroadcastEvent> broadcastHandler = (BroadcastHandler<BroadcastEvent>)list.get(i);

              broadcastHandler.handle(event);
            }
            catch(Exception e) {
              Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
          }
        }

        type = type.parent();
      }
    }
  }
}
