package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;

import org.int4.fx.values.model.BooleanModel;

/**
 * Builder for {@link CheckBox} instances.
 */
public final class CheckBoxBuilder extends AbstractButtonBaseBuilder<CheckBox, CheckBoxBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public CheckBoxBuilder(String... styleClasses) {
    super(CheckBox::new, styleClasses);
  }

  /**
   * Builds the CheckBox and binds its selected property to a {@link BooleanProperty}.
   *
   * @param property the boolean property to bind, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see CheckBox#selectedProperty()
   */
  public CheckBoxBuilder value(BooleanProperty property) {
    Objects.requireNonNull(property, "property");

    return apply(node -> node.selectedProperty().bindBidirectional(property));
  }

  /**
   * Builds the CheckBox and links its selected property to a {@link BooleanModel}.
   *
   * @param model the boolean model to link, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see CheckBox#selectedProperty()
   */
  public CheckBoxBuilder model(BooleanModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> ModelLinker.link(
      node,
      model,
      node.selectedProperty()
    ));
  }
}