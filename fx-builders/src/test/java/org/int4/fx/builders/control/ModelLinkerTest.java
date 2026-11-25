package org.int4.fx.builders.control;

import javafx.scene.control.ComboBox;
import javafx.stage.Window;

import org.int4.fx.builders.common.SceneBuilder;
import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSources;
import org.int4.fx.values.model.ChoiceModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class ModelLinkerTest extends FXTest {

  @Test
  void shouldReplayModelStateAfterReconnect() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("A", "B"));
    ChoiceModel<String> model = ChoiceModel.of("A", source);
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .model(model)
      .build();

    new SceneBuilder(control).build();  // show state managing scene

    Window window = Mockito.mock(Window.class);

    Broadcasts.broadcast(control, WindowEvent.showing(window));

    assertThat(control.getItems()).containsExactly("A", "B");
    assertThat(control.getValue()).isEqualTo("A");

    Broadcasts.broadcast(control, WindowEvent.hidden(window));

    source.set(Domain.of("X", "Y"));
    model.set("Y");

    // Nothing is synchronized while hidden:
    assertThat(control.getItems()).containsExactly("A", "B");
    assertThat(control.getValue()).isEqualTo("A");

    Broadcasts.broadcast(control, WindowEvent.showing(window));

    assertThat(control.getItems()).containsExactly("X", "Y");
    assertThat(control.getValue()).isEqualTo("Y");
  }
}
