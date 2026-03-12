package org.int4.fx.builders.control;

import javafx.scene.Scene;
import javafx.scene.control.ComboBox;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.ChoiceModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComboBoxControlTest extends ControlBuilderTest {
  private ChoiceModel<String> model = ChoiceModel.withInitial("C", "A", "B", "C", "D", "E");
  private ComboBox<String> control = new ComboBoxBuilder.Raw().model(model) /* editable() */ .build();

  @Nested
  class WhenConnectedToScene {
    Scene scene = new Scene(control);

    @Test
    void shouldHaveExpectedInitialState() {
      assertThat(control.getValue()).isEqualTo("C");
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);
      assertThat(control.getSelectionModel().getSelectedIndex()).isEqualTo(2);
    }

    @Test
    void shouldRespondToValidModelChanges() {
      model.set("D");

      assertThat(control.getValue()).isEqualTo("D");
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);

      model.set("E");

      assertThat(control.getValue()).isEqualTo("E");
      assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);

      model.set("Z");  // an invalid change, should not update control

      assertThat(control.getValue()).isEqualTo("E");
      assertThat(control.getPseudoClassStates()).contains(INVALID).doesNotContain(TOUCHED, DIRTY);
    }

    @Test
    void shouldUpdateModelOnUserChange() {
      control.getSelectionModel().select("B");

      assertThat(model.get()).isEqualTo("B");
      assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
    }

    @Nested
    class AndControlIsSetToAValidValue {
      {
        control.getSelectionModel().select("D");
      }

      @Test
      void modelShouldReceiveNewValue() {
        assertThat(model.get()).isEqualTo("D");
      }

      @Test
      void controlShouldBeDirtyTouchedAndValid() {
        assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
      }

      @Nested
      class AndModelDomainChanges {
        {
          model.setDomain(Domain.of("a", "b", "c", "d", "e"));
        }

        @Test
        void modelShouldBecomeInvalid() {
          assertThat(model.isValid()).isFalse();
        }

        @Test
        void controlShouldBecomeInvalidButRemainTouchedAndDirty() {
          assertThat(control.getPseudoClassStates())
              .contains(INVALID, TOUCHED, DIRTY);
        }

        @Test
        void controlChoicesShouldBeUpdated() {
          assertThat(control.getItems()).containsExactly("a", "b", "c", "d", "e");
        }

        @Test
        void selectionShouldRemainUnchanged() {
          assertThat(control.getSelectionModel().getSelectedItem()).isEqualTo("D");
        }
      }

      @Nested
      class AndModelDomainChangesButStillContainsSelectedValue {
        {
          model.setDomain(Domain.of("B", "C", "D"));
        }

        @Test
        void modelShouldRemainValid() {
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void valueShouldRemainUnchanged() {
          assertThat(control.getValue()).isEqualTo("D");
          assertThat(model.get()).isEqualTo("D");
        }

        @Test
        void controlChoicesShouldBeUpdated() {
          assertThat(control.getItems()).containsExactly("B", "C", "D");
        }

        @Test
        void selectionIndexShouldUpdate() {
          assertThat(control.getSelectionModel().getSelectedIndex()).isEqualTo(2);
        }

        @Test
        void pseudoStatesShouldRemainUnchanged() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
        }
      }

      @Nested
      class AndModelIsModifiedProgrammatically {
        {
          model.set("B");
        }

        @Test
        void modelShouldContainNewValue() {
          assertThat(model.get()).isEqualTo("B");
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldNotBeUpdatedWhileDirty() {
          assertThat(control.getValue()).isEqualTo("D");
          assertThat(control.getSelectionModel().getSelectedIndex()).isEqualTo(3);
        }

        @Test
        void controlShouldRemainDirtyAndTouched() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
        }
      }

      @Nested
      class AndUserSelectsSameValueAgain {
        {
          control.getSelectionModel().select("D");
        }

        @Test
        void modelShouldRemainUnchanged() {
          assertThat(model.get()).isEqualTo("D");
        }

        @Test
        void controlValueShouldRemainUnchanged() {
          assertThat(control.getValue()).isEqualTo("D");
        }

        @Test
        void pseudoStatesShouldRemainUnchanged() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED, DIRTY).doesNotContain(INVALID);
        }
      }

      @Nested
      class AndControlLosesFocus {
        {
          control.fireEvent(new ModelLinker.TestFocusEvent(false));
        }

        @Test
        void modelShouldRemainUnchanged() {
          assertThat(model.get()).isEqualTo("D");
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldRemainUnchanged() {
          assertThat(control.getValue()).isEqualTo("D");
        }

        @Test
        void controlShouldLoseDirtyStatus() {
          assertThat(control.getPseudoClassStates()).contains(TOUCHED).doesNotContain(INVALID, DIRTY);
        }
      }
    }

    @Nested
    class AndModelDomainChanges {
      {
        model.setDomain(Domain.of("a", "b", "c", "d", "e"));
      }

      @Test
      void modelShouldBecomeInvalid() {
        assertThat(model.isValid()).isFalse();
      }

      @Test
      void controlShouldGetInvalidState() {
        assertThat(control.getPseudoClassStates()).contains(INVALID).doesNotContain(TOUCHED, DIRTY);
      }

      @Test
      void controlChoicesShouldBeUpdated() {
        assertThat(control.getItems()).containsExactly("a", "b", "c", "d", "e");
      }

      @Test
      void selectedIndexRemainsUnchanged() {
        assertThat(control.getSelectionModel().getSelectedIndex()).isEqualTo(2);
        assertThat(control.getSelectionModel().getSelectedItem()).isEqualTo("C");
      }

      @Nested
      class AndChoiceIsSetProgrammatically {
        {
          model.set("b");
        }

        @Test
        void modelShouldBecomeValid() {
          assertThat(model.isValid()).isTrue();
        }

        @Test
        void controlShouldLoseInvalidState() {
          assertThat(control.getPseudoClassStates()).doesNotContain(INVALID, TOUCHED, DIRTY);
        }

        @Test
        void selectedIndexIsUpdated() {
          assertThat(control.getSelectionModel().getSelectedIndex()).isEqualTo(1);
          assertThat(control.getSelectionModel().getSelectedItem()).isEqualTo("b");
        }
      }
    }
  }

  @Test
  void shouldBeUnitialized() {
    assertThat(control.getValue()).isNull();
  }
}
