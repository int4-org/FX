package org.int4.fx.builders.control;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputControl;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.strategy.TextStrategy;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.LongModel;
import org.int4.fx.values.model.StringModel;
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
   * <p>
   * Any object is accepted, and provided to the active {@link TextStrategy}.
   *
   * @param text the prompt text
   * @return the fluent builder, never {@code null}
   * @see TextInputControl#promptTextProperty()
   */
  public final B promptText(Object text) {
    return applyStrategy(TextStrategy::base, (s, node) -> s.apply(node, text, node::setPromptText));
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
   * @return the fluent builder, never {@code null}
   * @see TextInputControl#textProperty()
   */
  public final B value(String initialValue) {
    return apply(node -> node.setText(initialValue));
  }

  /**
   * Creates the text input control and binds its value bidirectionally
   * to the given string property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see TextInputControl#textProperty()
   */
  public final B value(StringProperty property) {
    Objects.requireNonNull(property, "property");

    return apply(node -> node.textProperty().bindBidirectional(property));
  }

  /**
   * Creates the text input control and links it to the given integer model.
   *
   * @param model the integer model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(IntegerModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> link(node, model, v -> Integer.toString(v), AbstractTextInputControlBuilder::parseInt));
  }

  /**
   * Creates the text input control and links it to the given long model.
   *
   * @param model the long model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(LongModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> link(node, model, v -> Long.toString(v), AbstractTextInputControlBuilder::parseLong));
  }

  /**
   * Creates the text input control and links it to the given double model.
   *
   * @param model the double model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(DoubleModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> {
      DecimalFormat formatter = new DecimalFormat();  // TODO should support locales

      link(node, model, formatter::format, s -> parseDouble(formatter, s));
    });
  }

  /**
   * Creates the text input control and links it to the given string model.
   *
   * @param model the string model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(StringModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> link(node, model, Function.identity(), Function.identity()));
  }

  private <T> void link(C node, ValueModel<T> model, Function<T, String> printer, Function<String, T> parser) {
    ModelLinker.link(
      node,
      model,
      () -> node.getText(),
      v -> node.setText(v == null ? "" : printer.apply(v)),
      r -> {

        /*
         * The suppplied parser will:
         * - Return a parsed value if it was parseable
         * - Return null if it was unparseable but input was blank
         * - Throw an exception if it was unparseable and input was not blank
         */

        T modelValue = parser.apply(r);  // if parsing throws exception, model is marked invalid via trySet

        /*
         * Special exception for blank strings. If the domain does NOT accept the current blank string,
         * but it does accept null, then it is coerced to null:
         */

        if(modelValue instanceof String s && s.isBlank() && model.getDomain().allowsNull()) {
          @SuppressWarnings("unchecked")
          T cast = (T)s;  // safe cast, model value was a string after parsing, so domain must be for strings

          if(!model.getDomain().contains(cast)) {
            return null;
          }
        }

        return modelValue;
      },
      r -> node.textProperty().subscribe((ov, nv) -> r.run())
    );
  }

  /*
   * All parse functions:
   * - Must consume all input, and throw an exception if they can't
   * - Should return null when input is blank or null
   */

  private static Integer parseInt(String input) {
    if(input == null || input.isBlank()) {
      return null;
    }

    return Integer.parseInt(input);
  }

  private static Long parseLong(String input) {
    if(input == null || input.isBlank()) {
      return null;
    }

    return Long.parseLong(input);
  }

  private static Double parseDouble(DecimalFormat formatter, String input) {
    if(input == null || input.isBlank()) {
      return null;
    }

    ParsePosition pp = new ParsePosition(0);
    Number n = formatter.parse(input, pp);

    if(n != null && pp.getIndex() == input.length()) {
      return n.doubleValue();
    }

    throw new NumberFormatException("Unable to parse: " + input);
  }
}

