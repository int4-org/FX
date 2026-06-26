package org.int4.fx.builders.control;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.control.ModelLinker.ControlCommand;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.domain.StepperView;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.WritableModel;

/**
 * Abstract base builder for {@link Spinner} instances.
 * <p>
 * This contains any untyped methods.
 *
 * @param <C> the concrete {@link Spinner} type
 * @param <B> the concrete builder type
 */
public abstract class SpinnerBuilder<C extends Spinner<?>, B extends SpinnerBuilder<C, B>> extends AbstractControlBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public SpinnerBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Enables editing of the spinner value via its text editor.
   *
   * @return the fluent builder, never {@code null}
   * @see Spinner#setEditable(boolean)
   */
  public final B editable() {
    return apply(c -> c.setEditable(true));
  }

  // don't make this public without thinking about whether or not the user should bind the property to the given factory themselves in this case
  static <T> Typed<T> value(Typed<T> builder, Property<T> property, SpinnerValueFactory<T> factory) {
    Objects.requireNonNull(property, "property");
    Objects.requireNonNull(factory, "factory");

    return builder.apply(node -> {
      factory.valueProperty().bindBidirectional(property);
      node.setValueFactory(factory);
    });
  }

  @SuppressWarnings("unchecked")
  final <T> Typed<T> toTyped() {
    Typed<T> builder = new Typed<>();

    return builder.apply(c -> this.initialize((C)c));  // effectively "copies" all previous options
  }

  /**
   * Raw builder variant for {@link Spinner} instances.
   * <p>
   * This contains typed methods that result in a {@link Typed} variant of the builder.
   */
  public static final class Raw extends SpinnerBuilder<Spinner<?>, Raw> {

    /**
     * Creates a new builder with optional style classes.
     *
     * @param styleClasses the style classes, cannot be {@code null} but can be empty
     * @throws NullPointerException if any argument is {@code null}
     */
    public Raw(String... styleClasses) {
      super(Spinner::new, styleClasses);
    }

    /**
     * Binds an integer spinner to the given property using a default step size of {@code 1}.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param property the property to bind to, cannot be {@code null}
     * @param min the minimum value
     * @param max the maximum value
     * @return the fluent builder, typed to {@code Integer}, never {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see Spinner#valueFactoryProperty()
     * @see IntegerSpinnerValueFactory
     */
    public Typed<Integer> value(Property<Integer> property, int min, int max) {
      return value(property, min, max, 1);
    }

    /**
     * Binds an integer spinner to the given property.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param property the property to bind to, cannot be {@code null}
     * @param min the minimum value
     * @param max the maximum value
     * @param step the step size
     * @return the fluent builder, typed to {@code Integer}, never {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see Spinner#valueFactoryProperty()
     * @see IntegerSpinnerValueFactory
     */
    public Typed<Integer> value(Property<Integer> property, int min, int max, int step) {
      Objects.requireNonNull(property, "property");

      return value(toTyped(), property, new IntegerSpinnerValueFactory(min, max, property.getValue(), step));
    }

    /**
     * Binds a double spinner to the given property using a default step size of {@code 1.0}.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param property the property to bind to, cannot be {@code null}
     * @param min the minimum value
     * @param max the maximum value
     * @return the fluent builder, typed to {@code Double}, never {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see Spinner#valueFactoryProperty()
     * @see DoubleSpinnerValueFactory
     */
    public Typed<Double> value(Property<Double> property, double min, double max) {
      return value(property, min, max, 1.0);
    }

    /**
     * Binds a double spinner to the given property.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param property the property to bind to, cannot be {@code null}
     * @param min the minimum value
     * @param max the maximum value
     * @param step the step size
     * @return the fluent builder, typed to {@code Double}, never {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see Spinner#valueFactoryProperty()
     * @see DoubleSpinnerValueFactory
     */
    public Typed<Double> value(Property<Double> property, double min, double max, double step) {
      Objects.requireNonNull(property, "property");

      return value(toTyped(), property, new DoubleSpinnerValueFactory(min, max, property.getValue(), step));
    }

    /**
     * Binds a spinner to the given property using a fixed list of values.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of values and the new type of the builder
     * @param property the property to bind to, cannot be {@code null}
     * @param values the available values, cannot be {@code null}
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code property} or {@code values} is {@code null}
     * @see Spinner#valueFactoryProperty()
     * @see ListSpinnerValueFactory
     */
    public <T> Typed<T> value(Property<T> property, ObservableList<T> values) {
      Objects.requireNonNull(values, "values");

      return value(toTyped(), property, new ListSpinnerValueFactory<>(values));
    }

    /**
     * Binds an integer spinner to an {@link IntegerModel}.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param model the model to bind to, cannot be {@code null}
     * @return the fluent builder, typed to {@code Integer}, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see Spinner#valueFactoryProperty()
     */
    public Typed<Integer> model(IntegerModel model) {
      return create(toTyped(), model, v -> Integer.toString(v), SpinnerBuilder::parseInt);
    }

    /**
     * Binds a double spinner to a {@link DoubleModel}.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param model the model to bind to, cannot be {@code null}
     * @return the fluent builder, typed to {@code Double}, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see Spinner#valueFactoryProperty()
     */
    public Typed<Double> model(DoubleModel model) {
      DecimalFormat formatter = new DecimalFormat();  // TODO should support locales

      return create(toTyped(), model, formatter::format, s -> parseDouble(formatter, s));
    }

    /**
     * Binds a spinner to a {@link WritableModel} and keeps the editor and spinner
     * values synchronized with the model.
     * <p>
     * The spinner is automatically configured to reflect the model's domain:
     * <ul>
     *   <li>If the domain provides a {@link StepperView}, the spinner increments
     *       using the step logic.</li>
     *   <li>If the domain provides an {@link IndexedView}, the spinner increments
     *       through the indexed values.</li>
     *   <li>If the domain is empty or unsupported, the spinner has no value factory.</li>
     * </ul>
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param model the value model to bind to, cannot be {@code null}
     * @return the fluent builder, typed to {@code String}, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see WritableModel
     * @see IndexedView
     * @see StepperView
     * @see Spinner#valueFactoryProperty()
     */
    public Typed<String> model(WritableModel<String> model) {
      return create(toTyped(), model, Object::toString, Function.identity());
    }

    /**
     * Binds a spinner to a {@link WritableModel} using a custom converter to
     * translate between editor text and model values.
     * <p>
     * The spinner is automatically configured to reflect the model's domain
     * similarly to {@link #model(WritableModel)}, but uses the provided converter
     * for value conversion.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the values and the new type of the builder
     * @param model the value model to bind to, cannot be {@code null}
     * @param converter converts editor text to model values, cannot be {@code null}
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code model} or {@code converter} is {@code null}
     * @see WritableModel
     * @see IndexedView
     * @see StepperView
     * @see Spinner#valueFactoryProperty()
     */
    public <T> Typed<T> model(WritableModel<T> model, Function<String, T> converter) {
      return create(toTyped(), model, Object::toString, converter);
    }
  }

  /**
   * Typed builder variant for {@link Spinner} instances.
   * <p>
   * This contains methods available after the type has been established.
   *
   * @param <T> the type of the spinner
   */
  public static final class Typed<T> extends SpinnerBuilder<Spinner<T>, Typed<T>> {
    Typed() {
      super(Spinner::new);
    }
  }

  private static abstract class SimpleValueFactory<U> extends SpinnerValueFactory<U> {
    @Override
    public void decrement(int steps) {
      increment(-steps);
    }
  }

  static <T> Typed<T> create(Typed<T> builder, WritableModel<T> model, Function<T, String> printer, Function<String, T> parser) {
    Objects.requireNonNull(model, "model");
    Objects.requireNonNull(parser, "parser");

    return builder.apply(node -> {

      /*
       * Spinners have two separate values that should be kept in sync at
       * all times, but this is not satisfactory by default. Setting the
       * value to null for example will forcibly clear the editor text, while
       * having an unparseable text will leave the Spinner's value untouched
       * instead of set to null.
       *
       * The listener below enforces the desired invariants with the editor
       * value in the lead:
       * - If editor is null, empty or unparseable, spinner value is null
       * - If editor is parseable, spinner value reflects editor value
       */

      node.getEditor().textProperty().subscribe(new Consumer<String>() {
        private boolean ignoreNestedChanges;

        @Override
        public void accept(String v) {
          if(ignoreNestedChanges || node.getValueFactory() == null) {
            return;
          }

          try {
            ignoreNestedChanges = true;

            try {
              T value = parser.apply(v);

              node.getValueFactory().setValue(value);
            }
            catch(Exception e) {
              node.getValueFactory().setValue(null);  // this clears the editor value unfortunately
            }
            finally {
              node.getEditor().setText(v);  // ensure editor always ends up with its notified value
            }
          }
          finally {
            ignoreNestedChanges = false;
          }
        }
      });

      ModelLinker<Spinner<T>, String, T> linker = ModelLinker.sync(
        node,
        model,
        () -> node.getEditor().getText(),
        v -> node.getEditor().setText(
          switch(v) {
            case ControlCommand.ModelValue(T value) -> value == null ? "" : printer.apply(value);
            case ControlCommand.RawValue(String value) -> value;
            case ControlCommand.Inapplicable() -> null;
          }
        ),
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
        r -> node.getEditor().textProperty().subscribe((ov, nv) -> r.run())
      );

      linker.addSubscriber(() -> model.domain().subscribe(d -> linker.doModelInitiatedChange(() -> {
        String oldRawValue = node.getEditor().getText();

        SpinnerValueFactory<T> vf = switch(d.view(StepperView.class, IndexedView.class)) {
          case StepperView<T> sv -> new SimpleValueFactory<>() {
            @Override
            public void increment(int steps) {
              setValue(sv.step(getValue(), steps));
            }
          };
          case IndexedView<T> iv -> new SimpleValueFactory<>() {
            int index;
            boolean indexValid;

            {
              valueProperty().subscribe(() -> indexValid = false);
            }

            @Override
            public void increment(int steps) {
              if(!indexValid) {
                index = (int)iv.indexOf(getValue()) + steps;
              }

              T value = iv.size() == 0
                ? getValue()
                : iv.get(Math.clamp(index, 0, (int)iv.size() - 1));

              setValue(value);

              indexValid = true;  // must be after setValue
            }
          };
          default -> null;
        };

        node.setValueFactory(vf);

        /*
         * Domain change is handled. Because changing the value factory can have other effects on
         * Spinner, restore the current value if it was dirty (as a domain change should not
         * interfere with user input) or ensure the Spinner still reflects the current model value:
         */

        if(linker.isDirty()) {
          node.getEditor().setText(oldRawValue);
        }
        else {
          if(d.equals(Domain.inapplicable())) {
            node.getEditor().setText(null);
          }
          else if(vf != null) {
            switch(model.getRawValue()) {
              case RawValue.Valid<T>(T value) -> vf.setValue(value);
              case RawValue.Invalid<T>(T value, Template _) -> vf.setValue(value);
              case RawValue.Incompatible(Template _) -> vf.setValue(null);
            }
          }
        }
      })));
    });
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
