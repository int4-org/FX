package org.int4.fx.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * An event to indicate that controls should commit their contents to underlying
 * models, despite having the focus.
 */
public class CommitEvent extends Event {
  public static final EventType<CommitEvent> COMMIT = new EventType<>(Event.ANY, "COMMIT");

  /**
   * Constructs a new instance.
   */
  public CommitEvent() {
    super(COMMIT);
  }
}
