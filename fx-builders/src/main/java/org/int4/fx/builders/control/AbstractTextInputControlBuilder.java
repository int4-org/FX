package org.int4.fx.builders.control;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputControl;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.LongModel;
import org.int4.fx.values.model.ValueModel;

/**
 * Base builder for {@link TextInputControl} instances.
 *
 * @param <C> the concrete {@link TextInputControl} type
 * @param <B> the concrete builder type
 */
public abstract class AbstractTextInputControlBuilder<C extends TextInputControl, B extends AbstractTextInputControlBuilder<C, B>> extends AbstractControlBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the supplier used to create the control, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code instantiator} or {@code styleClasses} is {@code null}
   */
  protected AbstractTextInputControlBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets the prompt text of the text input control.
   *
   * @param text the prompt text
   * @return the fluent builder, never {@code null}
   * @see TextInputControl#promptTextProperty()
   */
  public final B promptText(String text) {
    return apply(c -> c.setPromptText(text));
  }

  /**
   * Makes the text input control read-only.
   *
   * @return the fluent builder, never {@code null}
   * @see TextInputControl#editableProperty()
   */
  public final B readOnly() {
    return apply(c -> c.setEditable(false));
  }

  /**
   * Creates the text input control with the given initial value.
   *
   * @param initialValue the initial text value
   * @return the created control, never {@code null}
   * @see TextInputControl#textProperty()
   */
  public final C value(String initialValue) {
    C node = build();

    node.setText(initialValue);

    return node;
  }

  /**
   * Creates the text input control and binds its value bidirectionally
   * to the given string property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see TextInputControl#textProperty()
   */
  public final C value(StringProperty property) {
    C node = build();

    node.textProperty().bindBidirectional(property);

    return node;
  }

  /**
   * Creates the text input control and links it to the given integer model.
   *
   * @param model the integer model, cannot be {@code null}
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final C model(IntegerModel model) {
    C node = build();

    link(node, model, v -> Integer.toString(v), Integer::parseInt);

    return node;
  }

  /**
   * Creates the text input control and links it to the given long model.
   *
   * @param model the long model, cannot be {@code null}
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final C model(LongModel model) {
    C node = build();

    link(node, model, v -> Long.toString(v), Long::parseLong);

    return node;
  }

  /**
   * Creates the text input control and links it to the given double model.
   *
   * @param model the double model, cannot be {@code null}
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final C model(DoubleModel model) {
    C node = build();

    link(node, model, v -> Double.toString(v), Double::parseDouble);

    return node;
  }

  /**
   * Creates the text input control and links it to the given string model.
   *
   * @param model the string model, cannot be {@code null}
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final C model(ValueModel<String> model) {
    C node = build();

    link(node, model, Function.identity(), Function.identity());

    return node;
  }

  private <T> void link(C node, ValueModel<T> model, Function<T, String> printer, Function<String, T> parser) {

    /*
     * As null is not representable in a text control, all nulls are translated to empty strings when displaying
     * and blank strings are translated to null when updating the model.
     */

    ModelLinker.link(
      node,
      model,
      () -> node.getText(),
      v -> node.setText(v == null ? "" : printer.apply(v)),
      r -> r == null || r.isBlank() ? null : parser.apply(r),
      r -> node.textProperty().subscribe((ov, nv) -> r.run())
    );
  }
}

