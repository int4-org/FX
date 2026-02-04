package org.int4.fx.core.event;

/**
 * Interface for broadcast events.
 */
public interface BroadcastEvent {

  /**
   * Returns the type of the broadcast event.
   *
   * @return the type of the broadcast event, never {@code null}
   */
  BroadcastType<?> type();
}