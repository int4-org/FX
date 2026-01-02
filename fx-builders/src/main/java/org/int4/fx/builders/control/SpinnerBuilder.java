package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Function;

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
import org.int4.fx.values.model.ChoiceModel;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.ValueModel;

/**
 * Builder for {@link Spinner} instances.
 */
public final class SpinnerBuilder extends AbstractControlBuilder<Spinner<?>, SpinnerBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public SpinnerBuilder(String... styleClasses) {
    super(Spinner::new, styleClasses);
  }

  /**
   * Enables editing of the spinner value via its text editor.
   *
   * @return the fluent builder, never {@code null}
   * @see Spinner#setEditable(boolean)
   */
  public SpinnerBuilder editable() {
    return apply(c -> c.setEditable(true));
  }

  /**
   * Binds an integer spinner to the given property using a default step size of {@code 1}.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @param min the minimum value
   * @param max the maximum value
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   */
  public Spinner<Integer> value(Property<Integer> property, int min, int max) {
    return value(property, min, max, 1);
  }

  /**
   * Binds an integer spinner to the given property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @param min the minimum value
   * @param max the maximum value
   * @param step the step size
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   */
  public Spinner<Integer> value(Property<Integer> property, int min, int max, int step) {
    return value(property, new IntegerSpinnerValueFactory(min, max, property.getValue(), step));
  }

  /**
   * Binds a double spinner to the given property using a default step size of {@code 1.0}.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @param min the minimum value
   * @param max the maximum value
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   */
  public Spinner<Double> value(Property<Double> property, double min, double max) {
    return value(property, min, max, 1.0);
  }

  /**
   * Binds a double spinner to the given property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @param min the minimum value
   * @param max the maximum value
   * @param step the step size
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   */
  public Spinner<Double> value(Property<Double> property, double min, double max, double step) {
    return value(property, new DoubleSpinnerValueFactory(min, max, property.getValue(), step));
  }

  /**
   * Binds a spinner to the given property using a fixed list of values.
   *
   * @param <T> the value type
   * @param property the property to bind to, cannot be {@code null}
   * @param values the available values, cannot be {@code null}
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code property} or {@code values} is {@code null}
   * @see ListSpinnerValueFactory
   */
  public <T> Spinner<T> value(Property<T> property, ObservableList<T> values) {
    Objects.requireNonNull(values, "values");

    return value(property, new ListSpinnerValueFactory<>(values));
  }

  /**
   * Binds a spinner to the given property using a custom value factory.
   *
   * @param <T> the value type
   * @param property the property to bind to, cannot be {@code null}
   * @param factory the value factory to use, cannot be {@code null}
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code property} or {@code factory} is {@code null}
   * @see Spinner#setValueFactory(SpinnerValueFactory)
   */
  public <T> Spinner<T> value(Property<T> property, SpinnerValueFactory<T> factory) {
    Objects.requireNonNull(property, "property");
    Objects.requireNonNull(factory, "factory");

    @SuppressWarnings("unchecked")
    Spinner<T> node = (Spinner<T>)build();

    factory.valueProperty().bindBidirectional(property);

    return node;
  }

  /**
   * Binds an integer spinner to an {@link IntegerModel}.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public Spinner<Integer> model(IntegerModel model) {
    return create(model, Integer::parseInt);
  }

  /**
   * Binds a double spinner to a {@link DoubleModel}.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public Spinner<Double> model(DoubleModel model) {
    return create(model, Double::parseDouble);
  }

  /**
   * Binds a spinner to a {@link ChoiceModel} of strings.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   */
  public Spinner<String> model(ChoiceModel<String> model) {
    return create(model, Function.identity());
  }

  /**
   * Binds a spinner to a {@link ChoiceModel} using a custom string-to-value converter.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @param converter converts editor text to model values, cannot be {@code null}
   * @param <T> the value type
   * @return the created spinner, never {@code null}
   * @throws NullPointerException if {@code model} or {@code converter} is {@code null}
   */
  public <T> Spinner<T> model(ChoiceModel<T> model, Function<String, T> converter) {
    return create(model, converter);
  }

  private <T> Spinner<T> create(ValueModel<T> model, Function<String, T> converter) {
    @SuppressWarnings("unchecked")
    Spinner<T> node = (Spinner<T>)build();

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

    return node;
  }

  abstract class SimpleValueFactory<T> extends SpinnerValueFactory<T> {
    @Override
    public void decrement(int steps) {
      increment(-steps);
    }
  }
}
