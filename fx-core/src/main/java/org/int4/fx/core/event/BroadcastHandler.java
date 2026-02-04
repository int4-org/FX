package org.int4.fx.core.event;

/**
 * Handler for a broadcast event of the given type.
 *
 * @param <T> the broadcast type this handler handles
 */
public interface BroadcastHandler<T extends BroadcastEvent> {

  /**
   * Invoked when a specific event of the type for which this handler is
   * registered happens.
   *
   * @param event the event which occured, cannot be {@code null}
   */
  void handle(T event);
}