package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.common.NodeEventHandler;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link MenuButton} instances.
 */
public final class MenuButtonBuilder extends AbstractControlBuilder<MenuButton, MenuButtonBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public MenuButtonBuilder(String... styleClasses) {
    super(MenuButton::new, styleClasses);
  }

  /**
   * Sets the text of the menu button.
   *
   * @param text the text to set, may be {@code null}
   * @return the fluent builder, never {@code null}
   * @see MenuButton#setText(String)
   */
  public MenuButtonBuilder text(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Sets the graphic of the menu button.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see MenuButton#setGraphic(javafx.scene.Node)
   */
  public MenuButtonBuilder graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return apply(c -> c.setGraphic(Builders.toNode(graphic)));
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