package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;
import javafx.util.Subscription;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.values.domain.ContinuousView;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.LongModel;
import org.int4.fx.values.model.ValueModel;

/**
 * Builder for {@link Slider} instances.
 */
public final class SliderBuilder extends AbstractControlBuilder<Slider, SliderBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
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
   * <p>
   * The slider itself operates on a normalized {@code [0,1]} range, while
   * tick marks, labels, and value conversions are derived from the domain views.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public SliderBuilder model(DoubleModel model) {
    return model(model, Function.identity());
  }

  /**
   * Binds the slider to a {@link IntegerModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   * <p>
   * The slider itself operates on a normalized {@code [0,1]} range, while
   * tick marks, labels, and value conversions are derived from the domain views.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public SliderBuilder model(IntegerModel model) {
    return model(model, Double::intValue);
  }

  /**
   * Binds the slider to a {@link LongModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   * <p>
   * The slider itself operates on a normalized {@code [0,1]} range, while
   * tick marks, labels, and value conversions are derived from the domain views.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public SliderBuilder model(LongModel model) {
    return model(model, Double::longValue);
  }

  private <T extends Number> SliderBuilder model(ValueModel<T> model, Function<Double, T> toModel) {
    Objects.requireNonNull(model, "model");

    return apply(node -> {
      ModelLinker<Slider, T, T> link = link(model, node, toModel);

      link.addSubscriber(() -> model.domainProperty().subscribe(d -> {
        switch(d.view(ContinuousView.class, IndexedView.class)) {
          case ContinuousView<T> cv -> {
            node.setMin(cv.get(0).doubleValue());
            node.setMax(cv.get(1).doubleValue());
          }
          case IndexedView<T> iv -> {
            node.setMin(0);
            node.setMax(iv.size() - 1);
          }
          default -> {}
        }
      }));
    });
  }

  private static <T extends Number> ModelLinker<Slider, T, T> link(ValueModel<T> model, Slider node, Function<Double, T> toModel) {
    return ModelLinker.link(
      node,
      model,
      () -> switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
        case ContinuousView<T> cv -> toModel.apply(node.getValue());
        case IndexedView<T> iv -> iv.get(Math.round(node.getValue()));
        default -> null;
      },
      v -> updateSlider(model, node),
      v -> v,
      r -> Subscription.combine(
        node.valueProperty().subscribe((ov, nv) -> r.run()),
        node.valueChangingProperty().subscribe((ov, nv) -> {
          if(!nv) {  // when dragging stops, normalize value via model
            updateSlider(model, node);
          }
        })
      )
    );
  }

  private static <T extends Number> void updateSlider(ValueModel<T> model, Slider node) {
    node.setValue(switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
      case ContinuousView<T> cv -> model.getValue().doubleValue();
      case IndexedView<T> iv -> iv.indexOf(model.getValue());
      default -> model.getValue().doubleValue();
    });
  }
}
