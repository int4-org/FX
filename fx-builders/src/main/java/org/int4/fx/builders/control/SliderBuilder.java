package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.values.domain.ContinuousView;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.LongModel;
import org.int4.fx.values.model.WritableModel;

/**
 * Builder for {@link Slider} instances.
 */
public final class SliderBuilder extends AbstractControlBuilder<Slider, SliderBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public SliderBuilder(String... styleClasses) {
    super(Slider::new, styleClasses);
  }

  /**
   * Enables tick marks and configures their spacing.
   *
   * @param majorTickUnit the major tick unit
   * @param minorTickCount the number of minor ticks
   * @return the fluent builder, never {@code null}
   * @see Slider#setShowTickMarks(boolean)
   */
  public SliderBuilder showTickMarks(double majorTickUnit, int minorTickCount) {
    majorTickUnit(majorTickUnit);
    minorTickCount(minorTickCount);

    return showTickMarks();
  }

  /**
   * Enables snapping the slider value to tick marks.
   *
   * @return the fluent builder, never {@code null}
   * @see Slider#setSnapToTicks(boolean)
   */
  public SliderBuilder snapToTicks() {
    return apply(c -> c.setSnapToTicks(true));
  }

  /**
   * Sets the major tick unit.
   *
   * @param value the major tick unit
   * @return the fluent builder, never {@code null}
   * @see Slider#setMajorTickUnit(double)
   */
  public SliderBuilder majorTickUnit(double value) {
    return apply(c -> c.setMajorTickUnit(value));
  }

  /**
   * Sets the number of minor ticks between major ticks.
   *
   * @param count the minor tick count
   * @return the fluent builder, never {@code null}
   * @see Slider#setMinorTickCount(int)
   */
  public SliderBuilder minorTickCount(int count) {
    return apply(c -> c.setMinorTickCount(count));
  }

  /**
   * Enables display of tick marks.
   *
   * @return the fluent builder, never {@code null}
   * @see Slider#setShowTickMarks(boolean)
   */
  public SliderBuilder showTickMarks() {
    return apply(c -> c.setShowTickMarks(true));
  }

  /**
   * Enables display of tick labels.
   *
   * @return the fluent builder, never {@code null}
   * @see Slider#setShowTickLabels(boolean)
   */
  public SliderBuilder showTickLabels() {
    return apply(c -> c.setShowTickLabels(true));
  }

  /**
   * Binds the slider value bidirectionally to a {@link DoubleProperty},
   * configuring the slider range explicitly.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @param min the minimum slider value
   * @param max the maximum slider value
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see Slider#valueProperty()
   * @see Slider#setMin(double)
   * @see Slider#setMax(double)
   */
  public SliderBuilder value(DoubleProperty property, double min, double max) {
    Objects.requireNonNull(property, "property");

    return apply(node -> {
      node.setMin(min);
      node.setMax(max);
      node.valueProperty().bindBidirectional(property);
    });
  }

  /**
   * Binds the slider to a {@link DoubleModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public SliderBuilder model(DoubleModel model) {
    return model(model, Function.identity(), Function.identity());
  }

  /**
   * Binds the slider to a {@link IntegerModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public SliderBuilder model(IntegerModel model) {
    return model(model, Double::intValue, Number::doubleValue);
  }

  /**
   * Binds the slider to a {@link LongModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public SliderBuilder model(LongModel model) {
    return model(model, Double::longValue, Number::doubleValue);
  }

  /**
   * Binds the slider to any {@link WritableModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   * <p>
   * The provided model is assumed to not be numeric, and therfore the slider
   * operates on a normalized {@code [0,1]} range.
   *
   * @param <T> the model type
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public <T> SliderBuilder model(WritableModel<T> model) {
    return model(model, null, null);
  }

  private <T> SliderBuilder model(WritableModel<T> model, Function<Double, T> toModel, Function<T, Double> toPosition) {
    Objects.requireNonNull(model, "model");

    if((toModel == null && toPosition != null) || (toModel != null && toPosition == null)) {
      throw new IllegalArgumentException("toModel and toPosition must either both be provided or not be provided");
    }

    return apply(node -> {
      ModelLinker<Slider, T> linker = link(model, node, toPosition);

      // start with clean slate:
      node.setMin(0);
      node.setMax(0);

      node.valueProperty().subscribe((ov, nv) -> {
        double value = nv.doubleValue();

        switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
          case ContinuousView<T> cv when toModel != null -> linker.push(cv.snap(toModel.apply(value)));
          case ContinuousView<T> cv -> linker.push(cv.get(value));
          case IndexedView<T> iv -> linker.push(iv.get(Math.round(value)));
          default -> {}
        }
      });

      node.valueChangingProperty().subscribe((ov, nv) -> {
        if(!nv) {  // when dragging stops, normalize value via model
          updateSlider(model, node, toPosition);
        }
      });

      linker.onDomainChanged(d -> applyLimits(node, d, toPosition));
    });
  }

  private static <T> ModelLinker<Slider, T> link(WritableModel<T> model, Slider node, Function<T, Double> toPosition) {
    return ModelLinker.link(
      node,
      model,
      v -> {
        if(v == null) {  // value was inapplicable

          /*
           * Sliders when not connected will have some initial range (defaults to 0-100) or after
           * being connected they will have whatever the last range was from the last set domain.
           * This leaves model remnants behind, which the subscriber below cleans up:
           */

          node.setMin(0);
          node.setMax(0);

          return;
        }

        double value = switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
          case ContinuousView<T> cv when toPosition != null -> toPosition.apply(cv.snap(v));
          case ContinuousView<T> cv -> cv.fractionOf(v);
          case IndexedView<T> iv -> Math.max(0, iv.indexOf(v));
          default -> node.getValue();
        };

        applyLimits(node, model.getDomain(), toPosition);  // ensure that value to be set won't be auto-corrected if domain was changed

        node.setValue(value);
      }
    );
  }

  private static <T> void applyLimits(Slider node, Domain<T> domain, Function<T, Double> toPosition) {
    switch(domain.view(ContinuousView.class, IndexedView.class)) {
      case ContinuousView<T> cv when toPosition != null -> {
        node.setMin(toPosition.apply(cv.get(0)));
        node.setMax(toPosition.apply(cv.get(1)));
      }
      case ContinuousView<T> _ -> {
        node.setMin(0);
        node.setMax(1);
      }
      case IndexedView<T> iv -> {
        node.setMin(0);
        node.setMax(iv.size() - 1);
      }
      default -> {
        // when no suitable view is present, best reset min/max (instead of leaving remnants)
        // bonus: this hides the slider knob (although you can still drag it invisibly, bug?)
        node.setMin(0);
        node.setMax(0);
      }
    }
  }

  private static <T> void updateSlider(WritableModel<T> model, Slider node, Function<T, Double> toPosition) {
    model.getRawValue().ifPresent(v ->
      node.setValue(switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
        case ContinuousView<T> cv when toPosition != null -> toPosition.apply(cv.snap(v));
        case ContinuousView<T> cv -> cv.fractionOf(v);
        case IndexedView<T> iv -> iv.indexOf(v);
        default -> 0.0;
      }
    ));
  }
}
