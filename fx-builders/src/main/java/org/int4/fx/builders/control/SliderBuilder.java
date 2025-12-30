package org.int4.fx.builders.control;

import java.text.DecimalFormat;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.values.domain.ContinuousView;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.model.DoubleModel;

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
   * @return the created slider, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see Slider#valueProperty()
   * @see Slider#setMin(double)
   * @see Slider#setMax(double)
   */
  public Slider value(DoubleProperty property, double min, double max) {
    Slider node = build();

    node.setMin(min);
    node.setMax(max);
    node.valueProperty().bindBidirectional(property);

    return node;
  }

  /**
   * Binds the slider to a {@link DoubleModel}, interpreting the model domain
   * through supported {@link ContinuousView} or {@link IndexedView} instances.
   * <p>
   * The slider itself operates on a normalized {@code [0,1]} range, while
   * tick marks, labels, and value conversions are derived from the domain views.
   *
   * @param model the model to bind to, cannot be {@code null}
   * @return the created slider, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ContinuousView
   * @see IndexedView
   */
  public Slider value(DoubleModel model) {
    Slider node = build();

    node.setMin(0);
    node.setMax(1);

    double majorTickUnit = node.getMajorTickUnit();

    ModelLinker<Slider, Double, Double> link = link(model, node);

    link.addSubscriber(() -> model.domainProperty().subscribe(d -> {
      switch(d.view(ContinuousView.class, IndexedView.class)) {
        case ContinuousView<Double> cv -> {
          node.setMajorTickUnit(cv.fractionOf(majorTickUnit));
          node.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double v) {
              return new DecimalFormat().format(cv.get(v));
            }

            @Override
            public Double fromString(String s) {
              return null;
            }
          });
        }
        case IndexedView<Double> iv -> {
          node.setMajorTickUnit(majorTickUnit / (iv.get(iv.size() - 1) - iv.get(0)));
          node.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double v) {
              return new DecimalFormat().format(iv.get((int)(v * (iv.size() - 1))));
            }

            @Override
            public Double fromString(String s) {
              return null;
            }
          });
        }
        default -> {}
      }
    }));

    return node;
  }

  private static ModelLinker<Slider, Double, Double> link(DoubleModel model, Slider node) {
    return ModelLinker.link(
      node,
      model,
      () -> switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
        case ContinuousView<Double> cv -> cv.get(node.getValue());
        case IndexedView<Double> iv -> iv.get((int)(Math.round((iv.size() - 1) * node.getValue())));
        default -> node.getValue();
      },
      v -> {
        if(!node.isValueChanging()) {  // Only update from master when not being dragged
          node.setValue(switch(model.getDomain().view(ContinuousView.class, IndexedView.class)) {
            case ContinuousView<Double> cv -> cv.fractionOf(model.get());
            case IndexedView<Double> iv -> (double)iv.indexOf(model.get()) / (iv.size() - 1);
            default -> model.get();
          });
        }
      },
      v -> v,
      r -> node.valueProperty().subscribe((ov, nv) -> r.run())
    );
  }
}
