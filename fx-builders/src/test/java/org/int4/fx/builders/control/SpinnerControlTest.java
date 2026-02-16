package org.int4.fx.builders.control;

import javafx.scene.Scene;
import javafx.scene.control.Spinner;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.IntegerModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpinnerControlTest extends ControlBuilderTest {
  private IntegerModel model = IntegerModel.of(50, Domain.bounded(0, 100));
  private Spinner<Integer> control = new SpinnerBuilder.Raw().model(model).editable().build();

  @Nested
  class WhenConnectedToScene {
    Scene scene = new Scene(control);

    @Test
    void shouldHaveExpectedInitialState() {
      assertThat(control.getValue()).isEqualTo(50);
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);
    }

    @Test
    void shouldRespondToValidModelChanges() {
      model.set(75);

      assertThat(control.getValue()).isEqualTo(75);
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);

      model.set(100);

      assertThat(control.getValue()).isEqualTo(100);
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);

      model.set(-10);  // an invalid change, should not update control

      assertThat(control.getValue()).isEqualTo(100);
      assertThat(control.getPseudoClassStates()).contains(INVALID).doesNotContain(TOUCHED, DIRTY);
    }

    @Test
    void shouldUpdateModelOnUserChange() {
      control.getValueFactory().setValue(65);

      assertThat(model.get()).isEqualTo(65);
      assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
    }

    @Nested
    class AndControlIsSetToAnInvalidValue {
      {
        control.getEditor().setText("illegal");
      }

      @Test
      void modelShouldBecomeInvalid() {
        assertThat(model.isValid()).isFalse();
        assertThat(model.getRawValue()).isEqualTo(50);  // last accepted value
        assertThat(model.conversionFailed()).isTrue();  // but it should be ignored
        assertThat(model.getValue()).isNull();
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getEditor().getText()).isEqualTo("illegal");
        assertThat(control.getValue()).isEqualTo(50);
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
        void modelShouldRemainUnchanged() {
          assertThat(model.isValid()).isFalse();
          assertThat(model.getRawValue()).isEqualTo(50);  // last accepted value
          assertThat(model.conversionFailed()).isTrue();  // but it should be ignored
          assertThat(model.getValue()).isNull();
        }

        @Test
        void controlShouldKeepExactValue() {
          assertThat(control.getEditor().getText()).isEqualTo("illegal");
          assertThat(control.getValue()).isEqualTo(50);
        }

        @Test
        void controlShouldLoseDirtyState() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, INVALID).doesNotContain(DIRTY);
        }
      }

      @Nested
      class AndControlIsSetToAValidValue {
        {
          control.getEditor().setText("99");
        }

        @Test
        void modelShouldBeUpdatedAndBecomeValid() {
          assertThat(model.isValid()).isTrue();
          assertThat(model.getRawValue()).isEqualTo(99);
          assertThat(model.conversionFailed()).isFalse();
          assertThat(model.getValue()).isEqualTo(99);
        }

        @Test
        void controlShouldKeepExactValueUntilFocusLoss() {
          assertThat(control.getEditor().getText()).isEqualTo("99");
          assertThat(control.getValue()).isEqualTo(50);
        }

        @Test
        void controlShouldBecomeValid() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
        }

        @Nested
        class AndControlLosesFocus {
          {
            control.fireEvent(new ModelLinker.TestFocusEvent(false));
          }

          @Test
          void modelShouldRemainUnchanged() {
            assertThat(model.isValid()).isTrue();
            assertThat(model.getRawValue()).isEqualTo(99);
            assertThat(model.conversionFailed()).isFalse();
            assertThat(model.getValue()).isEqualTo(99);
          }

          @Test
          void controlShouldCommitValue() {
            assertThat(control.getEditor().getText()).isEqualTo("99");
            assertThat(control.getValue()).isEqualTo(99);
          }

          @Test
          void controlShouldBecomeClean() {
            assertThat(control.getPseudoClassStates()).contains(TOUCHED).doesNotContain(DIRTY, INVALID);
          }
        }
      }
    }

    @Nested
    class AndControlIsSetToAnInvalidButConvertibleValue {
      {
        control.getEditor().setText("150");
      }

      @Test
      void modelShouldBecomeInvalid() {
        assertThat(model.isValid()).isFalse();
        assertThat(model.getRawValue()).isEqualTo(150);
        assertThat(model.conversionFailed()).isFalse();
        assertThat(model.getValue()).isNull();
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getEditor().getText()).isEqualTo("150");
        assertThat(control.getValue()).isEqualTo(50);
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
        void modelShouldRemainUnchanged() {
          assertThat(model.isValid()).isFalse();
          assertThat(model.getRawValue()).isEqualTo(150);
          assertThat(model.conversionFailed()).isFalse();
          assertThat(model.getValue()).isNull();
        }

        @Test
        void controlShouldKeepExactValue() {
          assertThat(control.getEditor().getText()).isEqualTo("150");
          assertThat(control.getValue()).isEqualTo(50);
        }

        @Test
        void controlShouldLoseDirtyState() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, INVALID).doesNotContain(DIRTY);
        }
      }
    }

    @Nested
    class AndControlIsSetToAValidValue {
      {
        control.getValueFactory().setValue(66);
      }

      @Test
      void modelShouldReceiveNewValue() {
        assertThat(model.get()).isEqualTo(66);
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
          assertThat(model.get()).isEqualTo(66);
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldRemainUnchanged() {
          assertThat(control.getValue()).isEqualTo(66);
        }

        @Test
        void controlShouldLoseDirtyStatus() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED).doesNotContain(INVALID, DIRTY);
        }
      }
    }
  }

  @Test
  void shouldBeUnitialized() {
    assertThat(control.getValue()).isNull();
  }
}
