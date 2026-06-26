package org.int4.fx.builders.control;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Region;

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

    private final Scene scene = new Scene(new Region());
    private final IntegerModel model = IntegerModel.of(5, INITIAL_DOMAIN);
    private final Spinner<Integer> control = new SpinnerBuilder.Raw().model(model).editable().build();

    private RawValue<Integer> expectedModelRawValue = RawValue.valid(5);
    private String expectedEditorControlValue = "";
    private Integer expectedControlValue = null;
    private String lastControlValue;
    private boolean connectedToScene;
    private Domain<Integer> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(RawValue<Integer> modelRawValue, Integer controlValue, String editorControlValue, String lastControlValue, boolean dirty, boolean touched, boolean connectedToScene, Domain<Integer> domain) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedModelRawValue,
        expectedControlValue,
        expectedEditorControlValue,
        lastControlValue,
        dirty,
        touched,
        connectedToScene,
        expectedDomain
      );
    }

    @Assertion
    public void assertControlState() {
      if(connectedToScene) {
        boolean modelValid = expectedDomain.equals(Domain.inapplicable()) || (expectedModelRawValue.isPresent() && expectedDomain.contains(expectedModelRawValue.orElseThrow()));
        Set<PseudoClass> expectedControlStates = new HashSet<>();

        if(dirty) {
          expectedControlStates.add(DIRTY);
        }
        if(touched) {
          expectedControlStates.add(TOUCHED);
        }

        boolean controlValid;

        if(dirty) {
          Integer parsedValue = parseInteger(expectedEditorControlValue);

          controlValid = expectedDomain.equals(Domain.inapplicable())
            ? expectedEditorControlValue == null
            : expectedDomain.contains(parsedValue);
        }
        else {
          controlValid = modelValid;
        }

        if(!controlValid) {
          expectedControlStates.add(INVALID);
        }

        assertAll(
          () -> assertThat(control.getEditor().getText()).describedAs("control.editor.text").isEqualTo(expectedEditorControlValue),
          () -> assertThat(control.getValue()).describedAs("control.value").isEqualTo(expectedControlValue),
          () -> assertThat(control.getPseudoClassStates()).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates)
        );
      }
    }

    @Assertion
    public void assertModelState() {
      boolean inapplicable = expectedDomain.equals(Domain.inapplicable());
      boolean modelValid = inapplicable || (expectedModelRawValue.isPresent() && expectedDomain.contains(expectedModelRawValue.orElseThrow()));
      Integer expectedModelValue = (modelValid && !inapplicable) ? expectedModelRawValue.orElseThrow() : null;

      assertAll(
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
      expectedEditorControlValue = null;
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
    public Runnable toEmptyDomain() {
      applyDomainChange(EMPTY_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    @ValueSource(strings = {"2", "8", "12", "100", "13"})
    public Runnable changeModel(String valueStr) {
      Integer value = Integer.valueOf(valueStr);

      applyModelChange(value);

      return () -> model.set(value);
    }

    @Action
    @ValueSource(strings = {"1", "7", "13", "abc", ""})
    public Runnable changeControl(String text) {
      applyControlChange(text);

      return () -> control.getEditor().setText(text);
    }

    @Action
    public Runnable spinUp() {
      Assumptions.assumeTrue(connectedToScene);

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
        applyModelChange(parseInteger(expectedEditorControlValue));
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

      syncControl();
    }

    private void applyControlChange(String text) {
      if(Objects.equals(text, expectedEditorControlValue)) {
        return;  // setting same value should not make control dirty or touched
      }

      Integer parsed = parseInteger(text);
      boolean parsable = parsed != null && parsed != Integer.MIN_VALUE;

      expectedEditorControlValue = text;
      expectedControlValue = parsable ? parsed : null;

      if(connectedToScene) {
        lastControlValue = text;  // Only update this when connected to model, because changes done while unconnected are to be discarded
        dirty = true;
        touched = true;

        applyModelChange(parsed);
      }
    }

    private void applyModelChange(Integer value) {
      expectedModelRawValue = value == null || !value.equals(Integer.MIN_VALUE)
        ? reevaluate(value)
        : RawValue.incompatible(ModelLinker.INCOMPATIBLE_TEMPLATE);

      syncControl();
    }

    private RawValue<Integer> reevaluate(Integer value) {
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
              case RawValue.Valid(Integer v) -> v;
              case RawValue.Invalid(Integer v, @SuppressWarnings("unused") Template reason) -> v;
              default -> null;
            };

        expectedEditorControlValue = expectedDomain.equals(Domain.inapplicable())
          ? null
          : switch(expectedModelRawValue) {
              case RawValue.Valid(Integer v) -> Integer.toString(v);
              case RawValue.Invalid(Integer v, @SuppressWarnings("unused") Template reason) -> v == null ? "" : Integer.toString(v);
              default -> lastControlValue;
            };
      }
    }

    private static Integer parseInteger(String input) {
      if(input == null || input.isBlank()) {
        return null;
      }

      try {
        return Integer.parseInt(input);
      }
      catch(NumberFormatException e) {
        return Integer.MIN_VALUE; // Sentinel for failure
      }
    }
  }
}
