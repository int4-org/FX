package org.int4.fx.builders.control;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import org.int4.fx.builders.custom.CustomNodeBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScrollPaneDecoratorTest extends FXTest {

  @Test
  void shouldWrapContentInScrollPaneAndTrackVisibility() {
    Label label = new Label("Hello");

    ScrollPane scrollPane = new CustomNodeBuilder<>(() -> label)
      .decorate(ScrollPaneDecorator.INSTANCE)
      .fitToWidth()
      .build();

    assertThat(scrollPane.isFitToWidth()).isTrue();
    assertThat(scrollPane.getContent()).isSameAs(label);
    assertThat(scrollPane.isVisible()).isTrue();
    assertThat(scrollPane.isManaged()).isTrue();

    label.setVisible(false);

    assertThat(scrollPane.isVisible()).isFalse();

    label.setManaged(false);

    assertThat(scrollPane.isManaged()).isFalse();
  }
}
