package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.internal.Builders;
import org.int4.fx.values.model.BooleanModel;

/**
 * Builder for {@link CheckBox} instances.
 */
public final class CheckBoxBuilder extends AbstractControlBuilder<CheckBox, CheckBoxBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public CheckBoxBuilder(String... styleClasses) {
    super(CheckBox::new, styleClasses);
  }

  /**
   * Sets the text of the CheckBox.
   *
   * @param text the text to display
   * @return the fluent builder, never {@code null}
   * @see CheckBox#setText(String)
   */
  public CheckBoxBuilder text(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Binds the text of the CheckBox to an observable value.
   *
   * @param text an observable value providing text, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see CheckBox#textProperty()
   */
  public CheckBoxBuilder text(ObservableValue<String> text) {
    return apply(c -> c.textProperty().bind(text));
  }

  /**
   * Sets the graphic of the CheckBox.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see CheckBox#setGraphic(javafx.scene.Node)
   */
  public CheckBoxBuilder graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return apply(c -> c.setGraphic(Builders.toNode(graphic)));
  }

  /**
   * Sets the action handler for the CheckBox.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see CheckBox#setOnAction(EventHandler)
   */
  public CheckBoxBuilder onAction(EventHandler<ActionEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(c -> c.setOnAction(eventHandler));
  }

  /**
   * Builds the CheckBox and binds its selected property to a {@link BooleanProperty}.
   *
   * @param property the boolean property to bind, cannot be {@code null}
   * @return the built CheckBox, never {@code null}
   * @see CheckBox#selectedProperty()
   */
  public CheckBox value(BooleanProperty property) {
    CheckBox node = build();

    node.selectedProperty().bindBidirectional(property);

    return node;
  }

  /**
   * Builds the CheckBox and links its selected property to a {@link BooleanModel}.
   *
   * @param model the boolean model to link, cannot be {@code null}
   * @return the built CheckBox, never {@code null}
   * @see CheckBox#selectedProperty()
   */
  public CheckBox value(BooleanModel model) {
    CheckBox node = build();

    ModelLinker.link(
      node,
      model,
      node.selectedProperty()
    );

    return node;
  }
}