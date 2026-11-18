package org.int4.fx.controls;

import javafx.scene.control.Label;

import org.int4.fx.builders.custom.CustomNodeBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResizablePaneDecoratorTest extends FXTest {

  @Test
  void shouldWrapContentInResizablePaneAndTrackVisibility() {
    Label label = new Label("Hello");

    ResizablePane pane = new CustomNodeBuilder<>(() -> label)
      .decorate(ResizablePaneDecorator.INSTANCE)
      .build();

    assertThat(label.getParent()).isSameAs(pane);
    assertThat(pane.isVisible()).isTrue();
    assertThat(pane.isManaged()).isTrue();

    label.setVisible(false);

    assertThat(pane.isVisible()).isFalse();

    label.setManaged(false);

    assertThat(pane.isManaged()).isFalse();
  }
}
