package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;

import org.int4.fx.builders.common.NodeEventHandler;

/**
 * Base builder for {@link ButtonBase} controls.
 *
 * @param <C> the concrete {@link ButtonBase} type
 * @param <B> the concrete builder type
 */
public abstract class AbstractButtonBaseBuilder<C extends ButtonBase, B extends AbstractButtonBaseBuilder<C, B>> extends AbstractLabeledBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code instantiator} or {@code styleClasses} is {@code null}
   */
  protected AbstractButtonBaseBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets an {@link EventHandler} for {@link ActionEvent}s.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see ButtonBase#setOnAction(EventHandler)
   */
  public final B onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }

  /**
   * Sets a {@link NodeEventHandler} for {@link ActionEvent}s, which receives
   * both the button instance and the event.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see ButtonBase#setOnAction(EventHandler)
   */
  public final B onAction(NodeEventHandler<C, ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(e -> eventHandler.handle(c, e)));
  }
}