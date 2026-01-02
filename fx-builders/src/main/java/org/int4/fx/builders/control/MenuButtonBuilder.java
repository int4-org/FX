package org.int4.fx.builders.control;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;

import org.int4.fx.builders.common.NodeEventHandler;

/**
 * Builder for {@link MenuButton} instances.
 */
public final class MenuButtonBuilder extends AbstractButtonBaseBuilder<MenuButton, MenuButtonBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public MenuButtonBuilder(String... styleClasses) {
    super(MenuButton::new, styleClasses);
  }

  /**
   * Sets the handler invoked when the menu button is about to be shown.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see MenuButton#setOnShowing(EventHandler)
   */
  public MenuButtonBuilder onShowing(EventHandler<Event> eventHandler) {
    return apply(c -> c.setOnShowing(eventHandler));
  }

  /**
   * Sets the handler invoked when the menu button is about to be shown, providing
   * both the menu button and the event to the handler.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see MenuButton#setOnShowing(EventHandler)
   */
  public MenuButtonBuilder onShowing(NodeEventHandler<MenuButton, Event> eventHandler) {
    return apply(c -> c.setOnShowing(e -> eventHandler.handle(c, e)));
  }
}