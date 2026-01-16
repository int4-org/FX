package org.int4.fx.builders.control;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.ValueModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public abstract class AbstractTextFieldBuilderTest<D, M extends ValueModel<D>> extends ControlBuilderTest {
  record Values<T>(T initialValid, T valid, T invalid) {}

  protected abstract M createModel();
  protected abstract TextField createControl(M model);
  protected abstract Values<D> modelValues();
  protected abstract Values<String> controlValues();
  protected abstract Values<String> notNormalizedControlValues();

  private M model = createModel(); //DoubleModel.of(5.0, Domain.bounded(0, 10, 0.1));
  private TextField control = createControl(model);

  @Nested
  class WhenConnectedToScene {
    Scene scene = new Scene(control);

    @Test
    void controlShouldHaveExpectedInitialState() {
      assertThat(control.getText()).isEqualTo(controlValues().initialValid);
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);
    }

    @Test
    void controlShouldBindVisibleAndManagedProperties() {
      assertThat(control.managedProperty().isBound()).isTrue();
      assertThat(control.visibleProperty().isBound()).isTrue();
    }

    @Nested
    class AndModelIsSetToValidValue {
      {
        model.setValue(modelValues().valid);
      }

      @Test
      void controlShouldReceiveNewValue() {
        assertThat(control.getText()).isEqualTo(controlValues().valid);
      }

      @Test
      void controlShouldRemainUntouchedCleanAndValid() {
        assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);
      }
    }

    @Nested
    class AndModelIsSetToInvalidValue {
      {
        model.setValue(modelValues().invalid);
      }

      @Test
      void controlShouldRemainUnchanged() {
        assertThat(control.getText()).isEqualTo(controlValues().initialValid);
      }

      @Test
      void controlShouldBecomeInvalidButRemainUntouchedAndClean() {
        assertThat(control.getPseudoClassStates()).contains(INVALID).doesNotContain(TOUCHED, DIRTY);
      }
    }

    @Nested
    class AndControlIsSetToValidValue {
      {
        control.setText(notNormalizedControlValues().valid);  // copy pasted into control for example
      }

      @Test
      void modelShouldReceiveNewValue() {
        assertThat(model.getValue()).isEqualTo(modelValues().valid);
        assertThat(model.isValid()).isTrue();
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getText()).isEqualTo(notNormalizedControlValues().valid);
      }

      @Test
      void controlShouldBeDirtyTouchedAndValid() {
        assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
      }

      @Nested
      class AndControlLosesFocus {
        {
          control.fireEvent(new ModelLinker.TestFocusEvent(false));
        }

        @Test
        void modelShouldRemainUnchanged() {
          assertThat(model.getValue()).isEqualTo(modelValues().valid);
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldReceiveNormalizedValue() {
          assertThat(control.getText()).isEqualTo(controlValues().valid);  // normalized value
        }

        @Test
        void controlShouldLoseDirtyStatus() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED).doesNotContain(INVALID, DIRTY);
        }
      }

      @Nested
      class AndModelIsUpdated {
        {
          model.setValue(modelValues().valid);
        }

        @Test
        void controlShouldRemainUnchangedAsItIsDirty() {
          assertThat(control.getText()).isEqualTo(notNormalizedControlValues().valid);
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
        }

        @Nested
        class AndControlLosesFocus {
          {
            control.fireEvent(new ModelLinker.TestFocusEvent(false));
          }

          @Test
          void modelShouldBeUpdatedWithControlValueWhenBothModified() {
            assertThat(model.getValue()).isEqualTo(modelValues().valid);
          }

          @Test
          void controlShouldReceiveNormalizedValue() {
            assertThat(control.getText()).isEqualTo(controlValues().valid);  // normalized value
          }

          @Test
          void controlShouldLoseDirtyStatus() {
            assertThat(control.getPseudoClassStates()).contains(TOUCHED).doesNotContain(INVALID, DIRTY);
          }
        }
      }
    }

    @Nested
    class AndControlIsSetToInvalidValue {
      {
        control.setText(notNormalizedControlValues().invalid);
      }

      @Test
      void modelShouldBecomeInvalid() {
        assertThat(model.getValue()).isNull();
        assertThat(model.isValid()).isFalse();
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getText()).isEqualTo(notNormalizedControlValues().invalid);
      }

      @Test
      void controlShouldBeDirtyTouchedAndInvalid() {
        assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY, INVALID);
      }

      @Nested
      class AndControlLosesFocus {
        {
          control.fireEvent(new ModelLinker.TestFocusEvent(false));
        }

        @Test
        void modelShouldRemainInvalid() {
          assertThat(model.getValue()).isNull();
          assertThat(model.isValid()).isFalse();
        }

        @Test
        void controlShouldKeepExactValue() {
          assertThat(control.getText()).isEqualTo(notNormalizedControlValues().invalid);
        }

        @Test
        void controlShouldLoseDirtyStatus() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, INVALID).doesNotContain(DIRTY);
        }
      }
    }

    @Nested
    class AndControlIsSetToBlankValue {
      {
        control.setText(" ");
      }

      @Test
      void modelShouldBecomeInvalid() {
        assertThat(model.getValue()).isNull();
        assertThat(model.isValid()).isFalse();
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getText()).isEqualTo(" ");
      }

      @Test
      void controlShouldBeDirtyTouchedAndInvalid() {
        assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY, INVALID);
      }
    }

    @Nested
    class AndControlIsSetToNull {
      {
        assumeFalse(model.getDomain().allowsNull());

        control.setText(null);
      }

      @Test
      void modelShouldBecomeInvalid() {
        assertThat(model.getValue()).isNull();
        assertThat(model.isValid()).isFalse();
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getText()).isEqualTo(null);
      }

      @Test
      void controlShouldBeDirtyTouchedAndInvalid() {
        assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY, INVALID);
      }
    }

    @Nested
    class AndModelIsSetToAnEmptyDomain {
      {
        model.setDomain(Domain.empty());
      }

      @Test
      void controlShouldBecomeInvisibleAndUnmanaged() {
        assertThat(control.isManaged()).isFalse();
        assertThat(control.isVisible()).isFalse();
      }

      @Test
      void controlShouldNotReactToModelChanges() {
        model.setValue(modelValues().valid);

        assertThat(control.getText()).isEqualTo(controlValues().initialValid);
        assertThat(control.getPseudoClassStates()).doesNotContain(TOUCHED, INVALID, DIRTY);

        model.setValue(modelValues().invalid);

        assertThat(control.getText()).isEqualTo(controlValues().initialValid);
        assertThat(control.getPseudoClassStates()).doesNotContain(TOUCHED, INVALID, DIRTY);
      }

      @Test
      void modelShouldNotReactToControlChanges() {
        assertThat(model.getValue()).isNull();

        control.setText(controlValues().valid);

        assertThat(model.getValue()).isNull();
      }
    }

    @Nested
    class AndControlIsRemovedFromScene {
      {
        scene.setRoot(new Label("empty"));
      }

      @Test
      void controlShouldUnbindVisibleAndManagedProperties() {
        assertThat(control.managedProperty().isBound()).isFalse();
        assertThat(control.visibleProperty().isBound()).isFalse();
      }

      @Test
      void controlShouldNotReactToModelChanges() {
        model.setValue(modelValues().valid);

        assertThat(control.getText()).isEqualTo(controlValues().initialValid);
        assertThat(control.getPseudoClassStates()).doesNotContain(TOUCHED, INVALID, DIRTY);

        model.setValue(modelValues().invalid);

        assertThat(control.getText()).isEqualTo(controlValues().initialValid);
        assertThat(control.getPseudoClassStates()).doesNotContain(TOUCHED, INVALID, DIRTY);
      }

      @Test
      void modelShouldNotReactToControlChanges() {
        assertThat(model.getValue()).isEqualTo(modelValues().initialValid);

        control.setText(controlValues().valid);

        assertThat(model.getValue()).isEqualTo(modelValues().initialValid);
      }
    }

    @Test
    void focusGainAndLossShouldNotResultInControlGettingTouchedState() {
      control.fireEvent(new ModelLinker.TestFocusEvent(true));
      control.fireEvent(new ModelLinker.TestFocusEvent(false));

      assertThat(control.getPseudoClassStates()).doesNotContain(TOUCHED, INVALID, DIRTY);
    }
  }

  @Test
  void shouldBeUnitialized() {
    assertThat(control.getText()).isEqualTo("");
  }

  @Test
  void shouldNotRespondToModelChanges() {
    model.setValue(modelValues().valid);

    assertThat(control.getText()).isEqualTo("");
  }

  @Test
  void shouldNotRespondToControlChanges() {
    control.setText(controlValues().valid);  // hypothetical, control isn't even visible in this state

    assertThat(model.getValue()).isEqualTo(modelValues().initialValid);
    assertThat(control.getPseudoClassStates()).doesNotContain(TOUCHED, INVALID, DIRTY);
  }
}
