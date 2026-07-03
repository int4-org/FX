package org.int4.fx.builders.control;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
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
import org.int4.fx.values.domain.StepperView;
import org.int4.fx.values.model.IntegerModel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SpinnerControlExploratoryTest extends ControlBuilderTest {

  @Test
  void validateStates() {
    ExploratoryTestRunner.explore(SpinnerExplorable.class, SpinnerExplorable::new);
  }

  public static class SpinnerExplorable implements Explorable {
    private static final Domain<Integer> INITIAL_DOMAIN = Domain.bounded(0, 10);
    private static final Domain<Integer> ALTERNATE_DOMAIN = Domain.bounded(5, 15);
    private static final Domain<Integer> EMPTY_DOMAIN = Domain.of();
    private static final Domain<Integer> INAPPLICABLE_DOMAIN = Domain.inapplicable();

    private final Scene scene = new Scene(new Region());
    private final IntegerModel model = IntegerModel.of(5, INITIAL_DOMAIN);
    private final Spinner<Integer> control = new SpinnerBuilder.Raw().model(model).editable().build();
    private final NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ROOT);

    private RawValue<Integer> expectedModelRawValue = RawValue.valid(5);
    private String expectedEditorControlValue = "";
    private Integer expectedControlValue = null;
    private boolean connectedToScene;
    private Domain<Integer> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(Integer controlValue, String editorControlValue, boolean dirty, boolean touched, boolean connectedToScene, RawValue<Integer> modelRawValue, Domain<Integer> domain) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedControlValue,
        expectedEditorControlValue,
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
      Integer expectedModelValue = modelValid && !inapplicable ? expectedModelRawValue.orElseThrow() : null;

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
        boolean modelValid = expectedModelRawValue instanceof RawValue.Valid;
        boolean controlValid = expectedDomain.equals(Domain.inapplicable())
          || (dirty ? expectedDomain.contains(parseInteger(expectedEditorControlValue)) : modelValid);

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
          () -> assertThat(control.getEditor().getText()).describedAs("control.editor.text").isEqualTo(expectedEditorControlValue),
          () -> assertThat(control.getValue()).describedAs("control.value").isEqualTo(expectedControlValue),
          () -> assertThat(control.getPseudoClassStates()).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates)
        );
      }
    }

    @Action
    public Runnable connectToScene() {
      Assumptions.assumeFalse(connectedToScene);

      connectedToScene = true;
      expectedEditorControlValue = null;

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
    public Runnable toEmptyDomain() {
      applyDomainChange(EMPTY_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    public Runnable toInapplicableDomain() {
      applyDomainChange(INAPPLICABLE_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    @ValueSource(strings = {"2", "8", "12", "100", "13"})
    public Runnable changeModel(String valueStr) {
      Integer value = Integer.valueOf(valueStr);

      applyModelChange(value, null);
      syncControl();

      return () -> model.set(value);
    }

    @Action
    @ValueSource(strings = {"1", "7", "013", "abc", ""})
    public Runnable changeControl(String text) {
      applyControlChange(text);

      return () -> control.getEditor().setText(text);
    }

    @Action
    public Runnable spinUp() {
      Assumptions.assumeTrue(connectedToScene);
      Assumptions.assumeFalse(expectedDomain.equals(Domain.inapplicable()));

      Integer parsed = parseInteger(expectedEditorControlValue);

      if(parsed != null && parsed != Integer.MIN_VALUE) {
        switch(expectedDomain.view(StepperView.class)) {
          case StepperView<Integer> sv -> applyControlChange("" + sv.step(parsed, 1));
          default -> {}
        }
      }

      return () -> control.increment();
    }

    @Action
    public Runnable spinDown() {
      Assumptions.assumeTrue(connectedToScene);
      Assumptions.assumeFalse(expectedDomain.equals(Domain.inapplicable()));

      Integer parsed = parseInteger(expectedEditorControlValue);

      if(parsed != null && parsed != Integer.MIN_VALUE) {
        switch(expectedDomain.view(StepperView.class)) {
          case StepperView<Integer> sv -> applyControlChange("" + sv.step(parsed, -1));
          default -> {}
        }
      }

      return () -> control.decrement();
    }

    private void flushControlChanges() {
      if(dirty) {
        dirty = false;

        // Rare case: if both model and control changed, control wins
        applyModelChange(expectedEditorControlValue);

        if(expectedModelRawValue.isPresent()) {
          syncControl();  // reformat value
        }
      }
    }

    private void applyDomainChange(Domain<Integer> domain) {
      if(Objects.equals(domain, expectedDomain)) {
        return;
      }

      expectedDomain = domain;
      expectedModelRawValue = switch(expectedModelRawValue) {
        case RawValue.Incompatible<Integer> i -> i;
        default -> reevaluate(expectedModelRawValue.orElseThrow());
      };

      if(expectedDomain.equals(Domain.inapplicable())) {
        expectedEditorControlValue = null;
        expectedControlValue = null;
      }

      syncControl();
    }

    private void applyControlChange(String text) {
      if(Objects.equals(text, expectedEditorControlValue)) {
        return;  // setting same value should not make control dirty or touched
      }

      expectedEditorControlValue = text;

      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        Integer parsed = parseInteger(text);
        boolean parsable = parsed != null && parsed != Integer.MIN_VALUE;

        expectedControlValue = parsable ? parsed : null;
        dirty = true;
        touched = true;

        applyModelChange(text);
      }
    }

    private void applyModelChange(String raw) {
      Integer parsed = parseInteger(raw);

      applyModelChange(
        parsed,
        parsed == null || !parsed.equals(Integer.MIN_VALUE) ? null : Template.of("conversion.incompatible", Immutable.sequencedMap("value", raw))
      );
    }

    private void applyModelChange(Integer value, Template template) {
      expectedModelRawValue = template == null ? reevaluate(value) : RawValue.incompatible(template);
    }

    private RawValue<Integer> reevaluate(Integer value) {
      return switch(expectedDomain.evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }

    private void syncControl() {
      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        if(!dirty) {

          /*
           * If the model holds an incompatible value, there is no telling where it came
           * from (although usually from the control). An incompatible value should therefore
           * not be attempted to be cast, converted or otherwise used to synchronize the
           * control with; the only option is to set it to null.
           */

          expectedEditorControlValue = switch(expectedModelRawValue) {
            case RawValue.Valid(Integer v) -> Integer.toString(v);
            case RawValue.Invalid(Integer v, _) -> v == null ? "" : Integer.toString(v);
            default -> "";
          };

          Integer parsed = parseInteger(expectedEditorControlValue);
          boolean parsable = parsed != null && parsed != Integer.MIN_VALUE;

          expectedControlValue = parsable ? parsed : null;
        }
      }

      if(expectedDomain.equals(Domain.inapplicable())) {
        dirty = false;
        touched = false;
      }
    }

    private Integer parseInteger(String input) {
      if(input == null || input.isBlank()) {
        return null;
      }

      ParsePosition pp = new ParsePosition(0);
      Number n = formatter.parse(input, pp);

      if(n != null && pp.getIndex() == input.length()) {
        return n.intValue();
      }

      return Integer.MIN_VALUE; // Sentinel for failure
    }
  }
}
