package org.int4.fx.builders.event;

import java.util.Objects;

import javafx.stage.Window;

import org.int4.fx.core.event.BroadcastEvent;
import org.int4.fx.core.event.BroadcastType;

/**
 * Broadcast event related to window visibility.
 */
public class WindowEvent implements BroadcastEvent {

  /**
   * Broadcast event type for all window events.
   */
  public static final BroadcastType<WindowEvent> ANY = BroadcastType.of("WINDOW");

  /**
   * Broadcast event type indicating an associated Window is about to become visible.
   */
  public static final BroadcastType<WindowEvent> SHOWING = BroadcastType.of(ANY, "WINDOW_SHOWING");

  /**
   * Broadcast event type indicating an associated Window has been hidden.
   */
  public static final BroadcastType<WindowEvent> HIDDEN = BroadcastType.of(ANY, "WINDOW_HIDDEN");

  /**
   * Creates a new {@link #SHOWING} {@link WindowEvent} with the given window as subject.
   *
   * @param window a {@link Window}, cannot be {@code null}
   * @return a {@link WindowEvent}, never {@code null}
   * @throws NullPointerException when any argument is {@code null}
   */
  public static WindowEvent showing(Window window) {
    return new WindowEvent(SHOWING, window);
  }

  /**
   * Creates a new {@link #HIDDEN} {@link WindowEvent} with the given window as subject.
   *
   * @param window a {@link Window}, cannot be {@code null}
   * @return a {@link WindowEvent}, never {@code null}
   * @throws NullPointerException when any argument is {@code null}
   */
  public static WindowEvent hidden(Window window) {
    return new WindowEvent(HIDDEN, window);
  }

  private final BroadcastType<WindowEvent> type;
  private final Window window;

  private WindowEvent(BroadcastType<WindowEvent> type, Window window) {
    this.type = Objects.requireNonNull(type, "type");
    this.window = Objects.requireNonNull(window, "window");
  }

  @Override
  public BroadcastType<?> type() {
    return type;
  }

  /**
   * Returns the {@link Window} associated with this event.
   *
   * @return the {@link Window}, never {@code null}
   */
  public Window window() {
    return window;
  }
}
