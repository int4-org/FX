package org.int4.fx.builders.control;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;

import org.int4.common.test.explorer.Action;
import org.int4.common.test.explorer.Assertion;
import org.int4.common.test.explorer.Explorable;
import org.int4.common.test.explorer.ExploratoryTestRunner;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.model.BooleanModel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CheckBoxControlExploratoryTest extends ControlBuilderTest {

  @Test
  void validateStates() {
    ExploratoryTestRunner.explore(CheckBoxExplorable.class, CheckBoxExplorable::new);
  }

  public static class CheckBoxExplorable implements Explorable {
    private static final Domain<Boolean> INITIAL_DOMAIN = Domain.of(true, false);
    private static final Domain<Boolean> ALTERNATE_DOMAIN = Domain.of(true);
    private static final Domain<Boolean> INAPPLICABLE_DOMAIN = Domain.inapplicable();

    private final Scene scene = new Scene(new Region());
    private final BooleanModel model = BooleanModel.of(false, INITIAL_DOMAIN);
    private final CheckBox control = new CheckBoxBuilder().model(model).build();

    private RawValue<Boolean> expectedModelRawValue = RawValue.valid(false);
    private boolean expectedControlValue;
    private boolean connectedToScene;
    private Domain<Boolean> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(boolean selected, boolean dirty, boolean touched, boolean connectedToScene, RawValue<Boolean> modelRawValue, Domain<Boolean> domain) {}

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
      Boolean expectedModelValue = modelValid && !inapplicable ? expectedModelRawValue.orElseThrow() : null;

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
          || (dirty ? expectedDomain.contains(expectedControlValue) : expectedModelRawValue instanceof RawValue.Valid);

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
          () -> assertThat(control.isSelected()).describedAs("control.selected").isEqualTo(expectedControlValue),
          () -> assertThat(control.getPseudoClassStates().stream().filter(ALL::contains)).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates)
        );
      }
    }

    @Action
    public Runnable connectToScene() {
      Assumptions.assumeFalse(connectedToScene);

      connectedToScene = true;

      if(expectedDomain.equals(Domain.inapplicable())) {
        expectedControlValue = false;
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
    public Runnable toInitialDomain() {
      applyDomainChange(INITIAL_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    public Runnable toAlternateDomain() {
      applyDomainChange(ALTERNATE_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }


    @Action
    public Runnable toInapplicableDomain() {
      applyDomainChange(INAPPLICABLE_DOMAIN);

      return () -> model.setDomain(expectedDomain);
    }

    @Action
    @ValueSource(booleans = {true, false})
    public Runnable changeModel(boolean value) {
      applyModelChange(value);
      syncControl();

      return () -> model.set(value);
    }

    @Action
    @ValueSource(booleans = {true, false})
    public Runnable changeControl(boolean selected) {
      applyControlChange(selected);

      return () -> control.setSelected(selected);
    }

    private void flushControlChanges() {
      if(dirty) {
        dirty = false;

        applyModelChange(expectedControlValue);

        if(expectedModelRawValue.isPresent()) {
          syncControl();
        }
      }
    }

    private void applyDomainChange(Domain<Boolean> domain) {
      if(Objects.equals(domain, expectedDomain)) {
        return;
      }

      expectedDomain = domain;
      expectedModelRawValue = switch(expectedModelRawValue) {
        case RawValue.Incompatible<Boolean> i -> i;
        default -> reevaluate(expectedModelRawValue.orElseThrow());
      };

      if(expectedDomain.equals(Domain.inapplicable())) {
        expectedControlValue = false;
      }

      syncControl();
    }

    private void applyControlChange(boolean selected) {
      if(selected == expectedControlValue) {
        return;
      }

      expectedControlValue = selected;

      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        dirty = true;
        touched = true;

        applyModelChange(selected);
      }
    }

    private void applyModelChange(Boolean value) {
      expectedModelRawValue = reevaluate(value);
    }

    private RawValue<Boolean> reevaluate(Boolean value) {
      return switch(expectedDomain.evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }

    private void syncControl() {
      if(connectedToScene && !expectedDomain.equals(Domain.inapplicable())) {
        if(!dirty) {
          expectedControlValue = switch(expectedModelRawValue) {
            case RawValue.Converted<Boolean> c -> c.value();
            default -> false;
          };
        }
      }
      else {
        dirty = false;
        touched = false;
      }
    }
  }
}
