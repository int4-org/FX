package org.int4.fx.builders.control;

import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputControl;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.strategy.TextStrategy;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.LongModel;
import org.int4.fx.values.model.StringModel;
import org.int4.fx.values.model.WritableModel;

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
   * Sets the text property to the given initial value.
   *
   * @param initialValue the initial text value
   * @return the fluent builder, never {@code null}
   * @see TextInputControl#textProperty()
   */
  public final B text(String initialValue) {
    return apply(node -> node.setText(initialValue));
  }

  /**
   * Binds the text property bidirectionally to the given string property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see TextInputControl#textProperty()
   */
  public final B text(StringProperty property) {
    Objects.requireNonNull(property, "property");

    return apply(node -> node.textProperty().bindBidirectional(property));
  }

  /**
   * Attaches the given model to the text input control.
   *
   * @param model the integer model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(IntegerModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> {
      NumberFormat formatter = NumberFormat.getIntegerInstance();  // TODO should support locales

      link(node, model, formatter::format, s -> Parsers.parseInt(formatter, s));
    });
  }

  /**
   * Attaches the given model to the text input control.
   *
   * @param model the long model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(LongModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> {
      NumberFormat formatter = NumberFormat.getIntegerInstance();  // TODO should support locales

      link(node, model, formatter::format, s -> Parsers.parseLong(formatter, s));
    });
  }

  /**
   * Attaches the given model to the text input control.
   *
   * @param model the double model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(DoubleModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> {
      NumberFormat formatter = NumberFormat.getNumberInstance();  // TODO should support locales

      link(node, model, formatter::format, s -> Parsers.parseDouble(formatter, s));
    });
  }

  /**
   * Attaches the given model to the text input control.
   *
   * @param model the string model, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public final B model(StringModel model) {
    Objects.requireNonNull(model, "model");

    return apply(node -> link(node, model, Function.identity(), Function.identity()));
  }

  /**
   * Attaches any {@link WritableModel} to the text input control.
   * <p>
   * Two converter functions must be provided. The {@code printer} function converts
   * a model value to a string, and is not allowed to throw an exception. The
   * {@code parser} function converts a string to a model value {@code T}. In case
   * conversion is not possible because the value is incompatible, then this must be
   * signaled with an exception.
   *
   * @param <T> the model type
   * @param model a {@link WritableModel}, cannot be {@code null}
   * @param printer a function to convert a model value to a string, cannot be {@code null}
   * @param parser a function to convert a string to a model value, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public final <T> B model(WritableModel<T> model, Function<T, String> printer, Function<String, T> parser) {
    Objects.requireNonNull(model, "model");
    Objects.requireNonNull(printer, "printer");
    Objects.requireNonNull(parser, "parser");

    return apply(node -> link(node, model, printer, parser));
  }

  private <T> void link(C node, WritableModel<T> model, Function<T, String> printer, Function<String, T> parser) {
    ModelLinker<C, T> linker = ModelLinker.sync(
      node,
      model,
      rv -> node.setText(switch(rv) {
        case RawValue.Converted<T> c -> c.value() == null ? "" : printer.apply(c.value());
        case RawValue.Incompatible(_) -> "";
      })
    );

    linker.onDomainChanged(d -> {
      if(d.equals(Domain.inapplicable())) {
        node.setText(null);
      }
    });

    node.textProperty().subscribe((ov, nv) -> {
      try {
        linker.push(convert(model, parser, nv));
      }
      catch(Exception e) {
        linker.pushIncompatible(nv);
      }
    });
  }

  private static <T> T convert(WritableModel<T> model, Function<String, T> parser, String raw) {

    /*
     * The supplied parser will:
     * - Return a parsed value if it was parseable
     * - Return null if it was unparseable but input was blank
     * - Throw an exception if it was unparseable and input was not blank
     */

    T modelValue = parser.apply(raw);  // could throw exception, which we should push to linker as incompatible

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
  }
}

