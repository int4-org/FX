package org.int4.fx.layout.flex;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;

import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlexBuilderTest {

  @Test
  void shouldConfigureFlex() {
    Region c1 = new MockResizable(10, 10);
    Region c2 = new MockResizable(20, 20);

    Flex flex = new FlexBuilder(Orientation.VERTICAL, "form")
      .aligned()
      .fill(false)
      .spacing(8)
      .alignment(Pos.CENTER_LEFT)
      .orientation(Orientation.HORIZONTAL)
      .nodes(c1, c2)
      .build();

    assertThat(flex.getStyleClass()).contains("form");
    assertThat(flex.getOrientation()).isEqualTo(Orientation.HORIZONTAL);
    assertThat(flex.isAligned()).isTrue();
    assertThat(flex.isFill()).isFalse();
    assertThat(flex.getSpacing()).isEqualTo(8);
    assertThat(flex.getAlignment()).isEqualTo(Pos.CENTER_LEFT);
    assertThat(flex.getChildren()).containsExactly(c1, c2);
  }

  @Test
  void shouldBuildWithChildren() {
    Region c1 = new MockResizable(10, 10);

    Flex flex = Flexes.FLEXES.vflex("form").with(c1);

    assertThat(flex.getOrientation()).isEqualTo(Orientation.VERTICAL);
    assertThat(flex.getStyleClass()).contains("form");
    assertThat(flex.getChildren()).containsExactly(c1);
  }

  @Test
  void shouldCreateBuilderWithOf() {
    Region c1 = new MockResizable(10, 10);

    Flex horizontal = Flex.of(Orientation.HORIZONTAL, "form").with(c1);
    Flex vertical = Flex.of(Orientation.VERTICAL, "form").build();

    assertThat(horizontal.getOrientation()).isEqualTo(Orientation.HORIZONTAL);
    assertThat(horizontal.getStyleClass()).contains("form");
    assertThat(horizontal.getChildren()).containsExactly(c1);
    assertThat(vertical.getOrientation()).isEqualTo(Orientation.VERTICAL);
    assertThat(vertical.getStyleClass()).contains("form");
  }
}
