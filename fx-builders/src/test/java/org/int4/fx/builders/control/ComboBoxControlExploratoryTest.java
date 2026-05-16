package org.int4.fx.builders.control;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;

import org.int4.fx.builders.explorer.Action;
import org.int4.fx.builders.explorer.Assertion;
import org.int4.fx.builders.explorer.Explorable;
import org.int4.fx.builders.explorer.ExploratoryTestRunner;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.model.ChoiceModel;
import org.int4.fx.values.model.RawValue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ComboBoxControlExploratoryTest extends ControlBuilderTest {

  @Test
  void validateStates() {
    ExploratoryTestRunner.explore(ComboBoxExplorable.class);
  }

  public static class ComboBoxExplorable implements Explorable {
    private static final Domain<String> INITIAL_DOMAIN = Domain.of("A", "B", "C", "D", "E");
    private static final Domain<String> ALTERNATE_DOMAIN = Domain.of("B", "C", "Z");
    private static final Domain<String> EMPTY_DOMAIN = Domain.of();
    private static final Domain<String> INAPPLICABLE_DOMAIN = Domain.inapplicable();

    private final Scene scene = new Scene(new Region());
    private final ChoiceModel<String> model = ChoiceModel.of("C", INITIAL_DOMAIN);
    private final ComboBox<String> control = new ComboBoxBuilder.Raw().model(model).build();

    private RawValue<String> expectedModelRawValue = RawValue.valid("C");
    private String expectedControlValue;
    private boolean connectedToScene;
    private Domain<String> expectedDomain = INITIAL_DOMAIN;
    private boolean dirty;
    private boolean touched;

    public record State(RawValue<String> modelRawValue, String controlValue, boolean dirty, boolean touched, boolean connectedToScene, Domain<String> domain) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedModelRawValue,
        expectedControlValue,
        dirty,
        touched,
        connectedToScene,
        expectedDomain
      );
    }

    @Assertion
    public void assertState() {
      boolean inapplicable = expectedDomain.equals(Domain.inapplicable());
      boolean domainContainsRawValue = expectedModelRawValue.isPresent() ? expectedDomain.contains(expectedModelRawValue.orElseThrow()) : false;
      boolean modelValid = inapplicable || domainContainsRawValue;
      String expectedModelValue = (domainContainsRawValue && !inapplicable) ? expectedModelRawValue.orElseThrow() : null;
      Set<PseudoClass> expectedControlStates = new HashSet<>();

      boolean controlValid = dirty
        ? (inapplicable ? expectedControlValue == null : expectedDomain.contains(expectedControlValue))
        : modelValid;

      if(connectedToScene && !controlValid) {
        expectedControlStates.add(INVALID);
      }

      if(dirty) {
        expectedControlStates.add(DIRTY);
      }

      if(touched) {
        expectedControlStates.add(TOUCHED);
      }

      List<String> expectedControlItems = (connectedToScene && !inapplicable) ? expectedDomain.<IndexedView<String>>requireView(IndexedView.class).asList() : Collections.emptyList();
      int expectedControlIndex = expectedControlItems.indexOf(expectedControlValue);

      assertAll(
        () -> assertThat(control.getValue()).describedAs("control.value").isEqualTo(expectedControlValue),
        () -> assertThat(control.getPseudoClassStates()).describedAs("control.pseudoClassStates").containsExactlyInAnyOrderElementsOf(expectedControlStates),
        () -> assertThat(control.getSelectionModel().getSelectedIndex()).describedAs("control.selectionModel.selectedIndex").isEqualTo(expectedControlIndex),
        () -> assertThat(control.getItems()).describedAs("control.items").containsExactlyElementsOf(expectedControlItems),
        () -> assertThat(model.getValue()).describedAs("model.value").isEqualTo(expectedModelValue),
        () -> assertThat(model.getRawValue()).describedAs("model.rawValue").isEqualTo(expectedModelRawValue),
        () -> assertThat(model.getDomain()).describedAs("model.domain").isEqualTo(expectedDomain),
        () -> assertThat(model.isValid()).describedAs("model.valid").isEqualTo(modelValid)
      );
    }

    @Action
    public Runnable connectToScene() {
      Assumptions.assumeFalse(connectedToScene);

      /*
       * At this point, the control should never be dirty (it was just connected),
       * so make it reflect the model value:
       */

      connectedToScene = true;

      syncControl();

      return () -> scene.setRoot(control);
    }

    @Action
    public Runnable disconnectFromScene() {
      touched = false;

      flushControlChanges();

      if(connectedToScene) {
        expectedControlValue = null;
      }

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
    @ValueSource(strings = {"A", "B", "Y", "Z"})
    public Runnable changeModel(String value) {
      applyModelChange(value);

      return () -> model.set(value);
    }

    @Action
    public Runnable clearModel() {
      applyModelChange(null);

      return () -> model.set(null);
    }

    @Action
    @ValueSource(strings = {"A", "B", "Y", "Z"})
    public Runnable changeControl(String value) {
      applyControlChange(value);

      return () -> control.getSelectionModel().select(value);
    }

    private void flushControlChanges() {
      if(dirty) {
        dirty = false;

        applyModelChange(expectedControlValue);
      }
    }

    private void applyDomainChange(Domain<String> domain) {
      if(Objects.equals(domain, expectedDomain)) {
        return;
      }

      expectedDomain = domain;
      expectedModelRawValue = switch(expectedModelRawValue) {
        case RawValue.Incompatible<String> rv -> rv;
        default -> reevaluate(expectedModelRawValue.orElseThrow());
      };

      syncControl();
    }

    private void applyModelChange(String value) {
      expectedModelRawValue = reevaluate(value);

      // Control only follows model if it's connected and not being edited by the user
      syncControl();
    }

    private void syncControl() {
      if(connectedToScene && !dirty) {
        expectedControlValue = expectedDomain.equals(Domain.inapplicable()) ? null : expectedModelRawValue.orElse(null);
      }
    }

    private void applyControlChange(String value) {
      if(Objects.equals(value, expectedControlValue)) {
        return;
      }

      expectedControlValue = value;

      if(connectedToScene) {
        dirty = true;
        touched = true;
        expectedModelRawValue = reevaluate(value);
      }
    }

    private RawValue<String> reevaluate(String value) {
      if(expectedDomain.equals(Domain.inapplicable())) {
        return RawValue.valid(value);
      }

      return switch(expectedDomain.evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }
  }
}
