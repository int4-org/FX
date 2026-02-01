package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBoxBase;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.strategy.TextStrategy;

/**
 * Base builder for {@link ComboBoxBase} controls.
 *
 * @param <C> the concrete {@link ComboBoxBase} type
 * @param <B> the concrete builder type
 */
public abstract class AbstractComboBoxBaseBuilder<C extends ComboBoxBase<?>, B extends AbstractComboBoxBaseBuilder<C, B>> extends AbstractControlBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code instantiator} or {@code styleClasses} is {@code null}
   */
  protected AbstractComboBoxBaseBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets the prompt text displayed when nothing is selected.
   * <p>
   * Any object is accepted, and provided to the active {@link TextStrategy}.
   *
   * @param text the prompt text
   * @return the fluent builder, never {@code null}
   * @see ComboBoxBase#promptTextProperty()
   */
  public final B promptText(Object text) {
    return applyStrategy(TextStrategy::base, (s, node) -> s.apply(node, text, node::setPromptText));
  }

  /**
   * Sets the action handler invoked when the selected color changes.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see ComboBoxBase#setOnAction(EventHandler)
   */
  public final B onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }
}