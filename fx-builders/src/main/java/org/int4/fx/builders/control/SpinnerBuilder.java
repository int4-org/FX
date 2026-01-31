package org.int4.fx.builders.control;

import java.util.Objects;
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
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.domain.StepperView;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.ValueModel;

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

  static <T> Typed<T> create(Typed<T> builder, ValueModel<T> model, Function<String, T> converter) {
    Objects.requireNonNull(model, "model");
    Objects.requireNonNull(converter, "converter");

    return builder.apply(node -> {
      ModelLinker<Spinner<T>, String, T> link = ModelLinker.link(
        node,
        model,
        () -> node.getEditor().getText(),
        v -> {
          SpinnerValueFactory<T> factory = node.getValueFactory();

          if(factory != null) {
            factory.setValue(v);
          }
        },
        converter
      );

      link.addSubscriber(() -> model.domainProperty().subscribe(d -> {
        switch(d.view(StepperView.class, IndexedView.class)) {
          case StepperView<T> sv -> {
            node.setValueFactory(new SimpleValueFactory<>() {
              @Override
              public void increment(int steps) {
                setValue(sv.step(getValue(), steps));
              }
            });
          }
          case IndexedView<T> iv -> {
            node.setValueFactory(new SimpleValueFactory<>() {
              int index;
              boolean indexValid;

              {
                valueProperty().subscribe(() -> indexValid = false);
              }

              @Override
              public void increment(int steps) {
                if(!indexValid) {
                  index = (int)iv.indexOf(getValue());
                }

                index = Math.clamp(index + steps, 0, (int)iv.size() - 1);

                setValue(iv.get(index));

                indexValid = true;  // must be after setValue
              }
            });
          }
          default -> node.setValueFactory(null);
        }

        if(node.getValueFactory() != null) {
          node.getValueFactory().setValue(model.getValue());
        }
      }));
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
      return create(toTyped(), model, Integer::parseInt);
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
      return create(toTyped(), model, Double::parseDouble);
    }

    /**
     * Binds a spinner to a {@link ValueModel} and keeps the editor and spinner
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
     * @see ValueModel
     * @see IndexedView
     * @see StepperView
     * @see Spinner#valueFactoryProperty()
     */
    public Typed<String> model(ValueModel<String> model) {
      return create(toTyped(), model, Function.identity());
    }

    /**
     * Binds a spinner to a {@link ValueModel} using a custom converter to
     * translate between editor text and model values.
     * <p>
     * The spinner is automatically configured to reflect the model's domain
     * similarly to {@link #model(ValueModel)}, but uses the provided converter
     * for value conversion.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the values and the new type of the builder
     * @param model the value model to bind to, cannot be {@code null}
     * @param converter converts editor text to model values, cannot be {@code null}
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code model} or {@code converter} is {@code null}
     * @see ValueModel
     * @see IndexedView
     * @see StepperView
     * @see Spinner#valueFactoryProperty()
     */
    public <T> Typed<T> model(ValueModel<T> model, Function<String, T> converter) {
      return create(toTyped(), model, converter);
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
}
