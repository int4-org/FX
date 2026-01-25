package org.int4.fx.builders.control;

import javafx.scene.Scene;
import javafx.scene.control.Slider;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.IntegerModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SliderControlTest extends ControlBuilderTest {
  private IntegerModel model = IntegerModel.of(50, Domain.bounded(0, 100));
  private Slider control = new SliderBuilder().model(model).build();

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
      control.setValue(65);

      assertThat(model.get()).isEqualTo(65);
      assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
    }

    @Nested
    class AndControlIsSetToValidValue {
      {
        control.setValueChanging(true);
        control.setValue(65.3);  // "valid" but not normalized
      }

      @Test
      void modelShouldReceiveNewValue() {
        assertThat(model.get()).isEqualTo(65);
      }

      @Test
      void controlShouldKeepExactValue() {
        assertThat(control.getValue()).isEqualTo(65.3);
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
          assertThat(model.get()).isEqualTo(65);
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldReceiveNormalizedValue() {
          assertThat(control.getValue()).isEqualTo(65);
        }

        @Test
        void controlShouldLoseDirtyStatus() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED).doesNotContain(INVALID, DIRTY);
        }
      }

      @Nested
      class AndValueStopsChanging {
        {
          control.setValueChanging(false);
        }

        @Test
        void modelShouldRemainUnchanged() {
          assertThat(model.get()).isEqualTo(65);
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldReceiveNormalizedValue() {
          assertThat(control.getValue()).isEqualTo(65);
        }

        @Test
        void controlShouldNotLoseDirtyStatus() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
        }
      }
    }
  }

  @Test
  void shouldBeUnitialized() {
    assertThat(control.getValue()).isEqualTo(0);
  }
}
