package org.int4.fx.builders.control;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;

import org.int4.common.test.explorer.Action;
import org.int4.common.test.explorer.Assertion;
import org.int4.common.test.explorer.Explorable;
import org.int4.common.test.explorer.ExploratoryTestRunner;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.domain.NormalizedView;
import org.int4.fx.values.model.DoubleModel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SliderControlExploratoryTest extends ControlBuilderTest {

  @Test
  void validateStates() {
    ExploratoryTestRunner.explore(SliderExplorable.class, SliderExplorable::new);
  }

  public static class SliderExplorable implements Explorable {
    private static final Domain<Double> INITIAL_DOMAIN = Domain.continuous(0.0, 100.0);
    private static final Domain<Double> ALTERNATE_DOMAIN = Domain.continuous(-50.0, 50.0);
    private static final Domain<Double> INAPPLICABLE_DOMAIN = Domain.inapplicable();

    private final Scene scene = new Scene(new Region());
    private final DoubleModel model = DoubleModel.of(50.0, INITIAL_DOMAIN);
    private final Slider control = new SliderBuilder().model(model).build();

    private RawValue<Double> expectedModelRawValue = RawValue.valid(50.0);
    private Double expectedControlValue = 0.0;  // only relevant when connected to scene
    private boolean connectedToScene;
    private Domain<Double> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(Double controlValue, boolean dirty, boolean touched, boolean connectedToScene, RawValue<Double> modelRawValue, Domain<Double> domain) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedControlValue,
        dirty,
        touched,
        connectedToScene,
        expectedModelRawValue,
        expectedDomain
      );
    }

    @Assertion
    public void assertModelState() {
      boolean inapplicable = expectedDomain.equals(Domain.inapplicable());
      boolean modelValid = inapplicable || expectedModelRawValue instanceof RawValue.Valid;
      Double expectedModelValue = (modelValid && !inapplicable) ? expectedModelRawValue.orElseThrow() : null;

      assertAll(
        () -> assertThat(model.getValue()).describedAs("model.value").isEqualTo(expectedModelValue),
        () -> assertThat(model.getRawValue()).describedAs("model.rawValue").isEqualTo(expectedModelRawValue),
        () -> assertThat(model.getDomain()).describedAs("model.domain").isEqualTo(expectedDomain),
        () -> assertThat(model.isValid()).describedAs("model.valid").isEqualTo(modelValid)
      );
    }

    @Assertion
    public void assertControlState() {
      if(connectedToScene) {
        Set<PseudoClass> expectedControlStates = new HashSet<>();
        boolean inapplicable = expectedDomain.equals(Domain.inapplicable());
        boolean isDomainMember = dirty
          ? expectedDomain.contains(expectedControlValue)
          : expectedModelRawValue instanceof RawValue.Valid;

        if(!isDomainMember && !inapplicable) {
          expectedControlStates.add(INVALID);
        }

        if(dirty) {
          expectedControlStates.add(DIRTY);
        }

        if(touched) {
          expectedControlStates.add(TOUCHED);
        }

        assertAll(
          () -> assertThat(control.getValue()).describedAs("control.value").isEqualTo(expectedControlValue),
          () -> assertThat(control.getPseudoClassStates().stream().filter(ALL::contains)).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates)
        );
      }
    }

    @Action
    public Runnable connectToScene() {
      Assumptions.assumeFalse(connectedToScene);

      connectedToScene = true;

      syncControl();

      return () -> scene.setRoot(control);
    }

    @Action
    public Runnable disconnectFromScene() {
      Assumptions.assumeTrue(connectedToScene);

      flushControlChanges();

      touched = false;
      connectedToScene = false;

      syncControl();

      return () -> scene.setRoot(new Region());
    }

    @Action
    public Runnable loseFocus() {
      flushControlChanges();

      return () -> control.fireEvent(new ModelLinker.TestFocusEvent(false));
    }

    @Action
    public Runnable gainFocus() {
      return () -> control.fireEvent(new ModelLinker.TestFocusEvent(true));
    }

    @Action
    public Runnable toAlternateDomain() {
      applyDomainChange(ALTERNATE_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    public Runnable toInitialDomain() {
      applyDomainChange(INITIAL_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    public Runnable toInapplicableDomain() {
      applyDomainChange(INAPPLICABLE_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    @ValueSource(doubles = {10.0, 75.0, 0.0})
    public Runnable changeModel(double value) {
      applyModelChange(Double.valueOf(value));

      return () -> model.set(value);
    }

    @Action
    @ValueSource(doubles = {10.5, 80.0, -5.3, 99.0})
    public Runnable changeControl(double value) {
      applyControlChange(value);

      return () -> control.setValue(value);
    }

    @Action
    @ValueSource(booleans = {true, false})
    public Runnable changeValueChanging(boolean valueChanging) {
      return () -> control.setValueChanging(valueChanging);
    }

    private void flushControlChanges() {
      if(dirty) {
        dirty = false;

        applyModelChange(expectedControlValue);
      }
    }

    private void applyDomainChange(Domain<Double> domain) {
      if(Objects.equals(domain, expectedDomain)) {
        return;
      }

      expectedDomain = domain;
      expectedModelRawValue = switch(expectedModelRawValue) {
        case RawValue.Incompatible<Double> i -> i;
        default -> reevaluate(expectedModelRawValue.orElseThrow());
      };

      if(expectedDomain.equals(Domain.inapplicable())) {
        expectedControlValue = 0.0;
      }

      syncControl();
    }

    private void applyControlChange(double value) {
      double normalizedValue = switch(expectedDomain.view(NormalizedView.class)) {
        case NormalizedView<Double> nv -> nv.snap(value);  // sliders will aggresively clamp value to min/max
        default -> expectedControlValue;  // inapplicable domain has no normalized view
      };

      if(Objects.equals(normalizedValue, expectedControlValue)) {
        return;
      }

      if(connectedToScene) {
        expectedControlValue = normalizedValue;
        dirty = true;
        touched = true;

        applyModelChange(expectedControlValue);
      }
    }

    private void applyModelChange(Double value) {
      expectedModelRawValue = reevaluate(value);

      syncControl();
    }

    private RawValue<Double> reevaluate(Double value) {
      if(value == null) {
        return RawValue.valid(null);
      }

      return switch(expectedDomain.evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }

    private void syncControl() {
      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        if(dirty) {

          /*
           * If a domain change occured, the current value of the dirty control may be outside its updated range.
           * As sliders aggressively clamp their value to min/max, a model initiated change (changing of domains)
           * can in this affect a dirty control (normally that's forbidden). As this is expected to a rare edge
           * case, this is allowed, and the control's value gets clamped to the range.
           *
           * As the control is dirty, and its value is still nicely within range after clamping, the control is
           * also considered valid.
           */

          expectedControlValue = normalizeValue(expectedControlValue);
        }
        else {

          /*
           * Note: sliders aggressively clamp their value to min/max. This means an odd situation can occur:
           *
           * - The model holds an invalid value (outside of domain range)
           * - The control shows a valid value (model value clamped to domain range)
           *
           * The control is in this case shown in invalid state (as it is not dirty, this tracks the model state).
           * The user can fix the invalid state by moving the slider, even though it may physically be positioned on
           * a valid value already. This is a bit undesirable (sliders just can't show out of range values). It is
           * unlikely to occur in practice, nevertheless the test must deal with it. Updating the model value for
           * a non-dirty control (because the control can't display it) is not an option!
           *
           * This is why the value we get from the model in the invalid case is normalized.
           */

          expectedControlValue = switch(expectedModelRawValue) {
            case RawValue.Valid(Double v) -> v;
            case RawValue.Invalid(Double v, _) -> normalizeValue(v);
            default -> null;
          };
        }
      }
      else {
        dirty = false;
        touched = false;
      }
    }

    private double normalizeValue(double value) {
      return switch(expectedDomain.view(NormalizedView.class)) {
        case NormalizedView<Double> nv -> nv.snap(value);  // sliders will aggressively clamp value to min/max
        default -> value;  // inapplicable domain has no normalized view
      };
    }
  }
}
