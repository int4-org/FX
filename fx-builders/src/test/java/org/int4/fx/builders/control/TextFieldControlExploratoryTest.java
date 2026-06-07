package org.int4.fx.builders.control;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import org.int4.fx.builders.explorer.Action;
import org.int4.fx.builders.explorer.Assertion;
import org.int4.fx.builders.explorer.Explorable;
import org.int4.fx.builders.explorer.ExploratoryTestRunner;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.RawValue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TextFieldControlExploratoryTest extends ControlBuilderTest {

  @Test
  void validateStates() {
    ExploratoryTestRunner.explore(TextFieldExplorable.class);
  }

  public static class TextFieldExplorable implements Explorable {
    private static final Domain<Double> INITIAL_DOMAIN = Domain.bounded(0.0, 10.0, 0.1);
    private static final Domain<Double> ALTERNATE_DOMAIN = Domain.bounded(5.0, 15.0, 0.2);
    private static final Domain<Double> INAPPLICABLE_DOMAIN = Domain.inapplicable();

    private final Scene scene = new Scene(new Region());
    private final DoubleModel model = DoubleModel.of(5.0, INITIAL_DOMAIN);
    private final TextField control = new TextFieldBuilder().model(model).build();
    private final DecimalFormat formatter = new DecimalFormat();

    private RawValue<Double> expectedModelRawValue = RawValue.valid(5.0);
    private String expectedControlValue = "";
    private String lastControlValue;
    private boolean connectedToScene;
    private Domain<Double> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(RawValue<Double> modelRawValue, String controlValue, String lastControlValue, boolean dirty, boolean touched, boolean connectedToScene, Domain<Double> domain) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedModelRawValue,
        expectedControlValue,
        lastControlValue,
        dirty,
        touched,
        connectedToScene,
        expectedDomain
      );
    }

    @Assertion
    public void assertState() {
      boolean inapplicable = expectedDomain.equals(Domain.inapplicable());
      boolean modelValid = inapplicable || (expectedModelRawValue.isPresent() && expectedDomain.contains(expectedModelRawValue.orElseThrow()));
      Double expectedModelValue = (modelValid && !inapplicable) ? expectedModelRawValue.orElseThrow() : null;
      Set<PseudoClass> expectedControlStates = new HashSet<>();

      if(connectedToScene) {
        if(dirty) {
          expectedControlStates.add(DIRTY);
        }
        if(touched) {
          expectedControlStates.add(TOUCHED);
        }

        boolean controlValid;
        if(dirty) {
          Double parsedValue = parseDouble(expectedControlValue);

          controlValid = inapplicable
            ? expectedControlValue == null
            : expectedDomain.contains(parsedValue);
        }
        else {
          controlValid = modelValid;
        }

        if(!controlValid) {
          expectedControlStates.add(INVALID);
        }
      }

      assertAll(
        () -> assertThat(control.getText()).describedAs("control.text").isEqualTo(expectedControlValue),
        () -> assertThat(control.getPseudoClassStates()).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates),
        () -> assertThat(model.getValue()).describedAs("model.value").isEqualTo(expectedModelValue),
        () -> assertThat(model.getRawValue()).describedAs("model.rawValue").isEqualTo(expectedModelRawValue),
        () -> assertThat(model.getDomain()).describedAs("model.domain").isEqualTo(expectedDomain),
        () -> assertThat(model.isValid()).describedAs("model.valid").isEqualTo(modelValid)
      );
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
      expectedControlValue = null;
      lastControlValue = "";

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
    @ValueSource(strings = {"2.0", "8.0", "12.0", "25.51", "13"})
    public Runnable changeModel(String valueStr) {
      Double value = Double.valueOf(valueStr);

      applyModelChange(value);

      return () -> model.set(value);
    }

    @Action
    @ValueSource(strings = {"1", "7", "13", "abc", ""})
    public Runnable changeControl(String text) {
      applyControlChange(text);

      return () -> control.setText(text);
    }

    @Action
    public Runnable clearControl() {
      applyControlChange(null);

      return () -> control.setText(null);
    }

    private void flushControlChanges() {
      if(dirty) {
        dirty = false;

        // Rare case: if both model and control changed, control wins
        applyModelChange(parseDouble(expectedControlValue));
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

      syncControl();
    }

    private void applyControlChange(String text) {
      if(Objects.equals(text, expectedControlValue)) {
        return;
      }

      expectedControlValue = text;

      if(connectedToScene) {
        lastControlValue = text;  // Only update this when connected to model, because changes done while unconnected are to be discarded
        dirty = true;
        touched = true;

        applyModelChange(parseDouble(text));
      }
    }

    private void applyModelChange(Double value) {
      expectedModelRawValue = value == null || !Double.isNaN(value)
        ? reevaluate(value)
        : RawValue.incompatible(ModelLinker.INCOMPATIBLE_TEMPLATE);

      syncControl();
    }

    private RawValue<Double> reevaluate(Double value) {
      return switch(expectedDomain.evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }

    private void syncControl() {
      if(connectedToScene && !dirty) {
        expectedControlValue = expectedDomain.equals(Domain.inapplicable())
          ? null
          : switch(expectedModelRawValue) {
              case RawValue.Valid(Double v) -> v == null ? "" : formatter.format(v);
              case RawValue.Invalid(Double v, @SuppressWarnings("unused") Template reason) -> v == null ? "" : formatter.format(v);
              default -> lastControlValue;
            };
      }
    }

    private Double parseDouble(String input) {
      if(input == null || input.isBlank()) {
        return null;
      }

      ParsePosition pp = new ParsePosition(0);
      Number n = formatter.parse(input, pp);

      if(n != null && pp.getIndex() == input.length()) {
        return n.doubleValue();
      }

      return Double.NaN; // Signals failure in this context
    }
  }
}
