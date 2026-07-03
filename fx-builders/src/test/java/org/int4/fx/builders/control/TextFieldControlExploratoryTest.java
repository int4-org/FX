package org.int4.fx.builders.control;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import org.int4.common.collection.Immutable;
import org.int4.common.test.explorer.Action;
import org.int4.common.test.explorer.Assertion;
import org.int4.common.test.explorer.Explorable;
import org.int4.common.test.explorer.ExploratoryTestRunner;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.model.DoubleModel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TextFieldControlExploratoryTest extends ControlBuilderTest {

  @Test
  void validateStates() {
    ExploratoryTestRunner.explore(TextFieldExplorable.class, TextFieldExplorable::new);
  }

  public static class TextFieldExplorable implements Explorable {
    private static final Domain<Double> INITIAL_DOMAIN = Domain.bounded(0.0, 10.0, 0.1);
    private static final Domain<Double> ALTERNATE_DOMAIN = Domain.bounded(5.0, 15.0, 0.2);
    private static final Domain<Double> INAPPLICABLE_DOMAIN = Domain.inapplicable();

    private final Scene scene = new Scene(new Region());
    private final DoubleModel model = DoubleModel.of(5.0, INITIAL_DOMAIN);
    private final TextField control = new TextFieldBuilder().model(model).build();
    private final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.ROOT);

    private RawValue<Double> expectedModelRawValue = RawValue.valid(5.0);
    private String expectedControlValue = "";  // only relevant for applicable domains
    private String lastControlValue;
    private boolean connectedToScene;
    private Domain<Double> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(String controlValue, String lastControlValue, boolean dirty, boolean touched, boolean connectedToScene, RawValue<Double> modelRawValue, Domain<Double> domain) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedControlValue,
        lastControlValue,
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
      Double expectedModelValue = modelValid && !inapplicable ? expectedModelRawValue.orElseThrow() : null;

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
        boolean controlValid = expectedDomain.equals(Domain.inapplicable())
          || (dirty ? expectedDomain.contains(parseDouble(expectedControlValue)) : expectedModelRawValue instanceof RawValue.Valid);

        if(!controlValid) {
          expectedControlStates.add(INVALID);
        }

        if(dirty) {
          expectedControlStates.add(DIRTY);
        }

        if(touched) {
          expectedControlStates.add(TOUCHED);
        }

        assertAll(
          () -> assertThat(control.getText()).describedAs("control.text").isEqualTo(expectedControlValue),
          () -> assertThat(control.getPseudoClassStates()).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates)
        );
      }
    }

    @Action
    public Runnable connectToScene() {
      Assumptions.assumeFalse(connectedToScene);

      connectedToScene = true;

      if(expectedDomain.equals(Domain.inapplicable())) {
        expectedControlValue = null;
      }

      syncControl();

      return () -> scene.setRoot(control);
    }

    @Action
    public Runnable disconnectFromScene() {
      Assumptions.assumeTrue(connectedToScene);

      flushControlChanges();

      touched = false;
      connectedToScene = false;

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
    @ValueSource(doubles = {2.0, 8.0, 12.0, 25.51, 13.0})
    public Runnable changeModel(double value) {
      applyModelChange(value, null);
      syncControl();

      return () -> model.set(value);
    }

    @Action
    @ValueSource(strings = {"1", "7.5", "013.0", "abc", ""})
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
        applyModelChange(expectedControlValue);

        if(expectedModelRawValue.isPresent()) {
          syncControl();  // reformat value
        }
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
        expectedControlValue = null;
      }

      syncControl();
    }

    private void applyControlChange(String text) {
      if(Objects.equals(text, expectedControlValue)) {
        return;
      }

      expectedControlValue = text;

      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        lastControlValue = text;  // Only update this when connected to model, because changes done while unconnected are to be discarded
        dirty = true;
        touched = true;

        applyModelChange(text);
      }
    }

    private void applyModelChange(String raw) {
      Double parsed = parseDouble(raw);

      applyModelChange(
        parsed,
        parsed == null || !Double.isNaN(parsed) ? null : Template.of("conversion.incompatible", Immutable.sequencedMap("value", raw))
      );
    }

    private void applyModelChange(Double value, Template template) {
      expectedModelRawValue = template == null ? reevaluate(value) : RawValue.incompatible(template);
    }

    private RawValue<Double> reevaluate(Double value) {
      return switch(expectedDomain.evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }

    private void syncControl() {
      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        if(!dirty) {
          expectedControlValue = switch(expectedModelRawValue) {
            case RawValue.Converted<Double> c -> c.value() == null ? "" : formatter.format(c.value());
            default -> "";
          };
        }
      }
      else {
        dirty = false;
        touched = false;
      }
    }

    private Double parseDouble(String input) {
      try {
        return Parsers.parseDouble(formatter, input);
      }
      catch(NumberFormatException e) {
        return Double.NaN; // Signals failure in this context
      }
    }
  }
}
