package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Labeled;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.strategy.TextStrategy;

/**
 * Base builder for {@link Labeled} controls.
 *
 * @param <C> the concrete {@link Labeled} type
 * @param <B> the concrete builder type
 */
public abstract class AbstractLabeledBuilder<C extends Labeled, B extends AbstractLabeledBuilder<C, B>> extends AbstractControlBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code instantiator} or {@code styleClasses} is {@code null}
   */
  protected AbstractLabeledBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets the text of the label.
   * <p>
   * Any object is accepted, and provided to the active {@link TextStrategy}.
   *
   * @param text the label text
   * @return the fluent builder, never {@code null}
   * @see Labeled#setText(String)
   */
  public final B text(Object text) {
    return applyStrategy(TextStrategy::base, (s, node) -> s.apply(node, text, node::setText));
  }

  /**
   * Binds the text of the label to the given observable value.
   *
   * @param value the observable text value, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Labeled#textProperty()
   */
  public final B text(ObservableValue<String> value) {
    return apply(c -> c.textProperty().bind(value));
  }

  /**
   * Enables text wrapping on the label.
   *
   * @return the fluent builder, never {@code null}
   * @see Labeled#setWrapText(boolean)
   */
  public final B wrapText() {
    return apply(c -> c.setWrapText(true));
  }

  /**
   * Sets the graphic of the label.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see Labeled#setGraphic(javafx.scene.Node)
   */
  public final B graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return applyContentStrategy(graphic, Labeled::setGraphic);
  }
}