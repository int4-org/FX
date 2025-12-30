package org.int4.fx.builders.common;

import javafx.event.Event;
import javafx.scene.Node;

/**
 * A specialized variant of {@link javafx.event.EventHandler} that receives
 * both the node it is associated with and the event itself.
 * <p>
 * This is particularly useful in fluent builder patterns, where the node
 * may not yet be fully constructed when the handler is defined.
 *
 * @param <N> the type of {@link Node} the handler is associated with
 * @param <E> the type of {@link Event} to handle
 * @see javafx.event.EventHandler
 */
public interface NodeEventHandler<N extends Node, E extends Event> {

  /**
   * Handles the given event in the context of the specified node.
   *
   * @param node the node the event is associated with, never {@code null}
   * @param event the event to handle, never {@code null}
   */
  void handle(N node, E event);
}

