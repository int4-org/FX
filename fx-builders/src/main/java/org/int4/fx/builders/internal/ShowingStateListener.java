package org.int4.fx.builders.internal;

/**
 * Holds the scene property key used to mark a scene as managing show state.
 * <p>
 * A scene carrying this property broadcasts
 * {@link org.int4.fx.builders.event.WindowEvent}s when its show state changes,
 * so linked controls can terminate and restore their subscriptions accordingly.
 */
public interface ShowingStateListener {

  /**
   * Scene property key marking a scene as managing show state.
   */
  static final Object SHOW_STATE_MANAGING_SCENE = new Object();
}
