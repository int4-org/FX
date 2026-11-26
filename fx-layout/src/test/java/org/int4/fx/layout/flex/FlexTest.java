package org.int4.fx.layout.flex;

import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import org.int4.fx.layout.util.MockBiased;
import org.int4.fx.layout.util.MockResizable;
import org.int4.fx.layout.util.Rotation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.int4.fx.layout.util.LayoutAssertions.assertBounds;
import static org.int4.fx.layout.util.LayoutAssertions.assertChildBounds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FlexTest {

  @Test
  void shouldSwitchOrientationAtRuntime() {
    Flex flex = new Flex(Orientation.HORIZONTAL);
    MockResizable c1 = new MockResizable(10, 10);
    MockResizable c2 = new MockResizable(20, 20);

    flex.getChildren().addAll(c1, c2);
    flex.resize(100, 100);
    flex.layout();

    assertThat(flex.getOrientation()).isEqualTo(Orientation.HORIZONTAL);
    assertThat(c1.getLayoutX()).isZero();
    assertThat(c2.getLayoutX()).isEqualTo(10);
    assertThat(c1.getLayoutY()).isZero();
    assertThat(c2.getLayoutY()).isZero();
    assertThat(flex.getPseudoClassStates()).contains(PseudoClass.getPseudoClass("horizontal"));
    assertThat(flex.getPseudoClassStates()).doesNotContain(PseudoClass.getPseudoClass("vertical"));

    flex.setOrientation(Orientation.VERTICAL);
    flex.resize(100, 100);
    flex.layout();

    assertThat(flex.getOrientation()).isEqualTo(Orientation.VERTICAL);
    assertThat(c1.getLayoutX()).isZero();
    assertThat(c2.getLayoutX()).isZero();
    assertThat(c1.getLayoutY()).isZero();
    assertThat(c2.getLayoutY()).isEqualTo(10);
    assertThat(flex.getPseudoClassStates()).contains(PseudoClass.getPseudoClass("vertical"));
    assertThat(flex.getPseudoClassStates()).doesNotContain(PseudoClass.getPseudoClass("horizontal"));

    flex.setOrientation(null);

    assertThat(flex.getOrientation()).isEqualTo(Orientation.HORIZONTAL);
  }

  @Test
  void shouldToggleAlignedAndExposeCssProperties() {
    Flex flex = new Flex();

    assertThat(flex.getCssMetaData().stream().map(CssMetaData::getProperty).toList())
      .contains("-fx-aligned", "-fx-fill", "-fx-spacing", "-fx-alignment");

    assertThat(flex.isAligned()).isFalse();

    flex.setAligned(true);

    assertThat(flex.isAligned()).isTrue();

    flex.setAligned(false);

    assertThat(flex.isAligned()).isFalse();
  }

  @Test
  void biasTraditional() {
    final double W = 20;
    final double H = 50;

    VBox flex = new VBox();

    Region c1 = new MockBiased(Orientation.HORIZONTAL, 30.5, 20.5);
    Region c2 = new MockResizable(20, 20, 40, 40);

    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.layout();

    assertEquals(Orientation.HORIZONTAL, flex.getContentBias());
    assertEquals(20, flex.minWidth(-1));
    assertEquals(52, flex.minHeight(flex.minWidth(-1)));
    assertEquals(40, flex.prefWidth(-1));
    assertEquals(56, flex.prefHeight(flex.prefWidth(-1)));

    assertChildBounds(
      """
      +------------+
      |  1: 20x32  |
      +------------+
      |  2: 20x20  |
      +------------+
      """,
      c1, c2
    );
  }

  @Test
  void biasTraditionalFlex() {
    final double W = 20;
    final double H = 50;

    Flex flex = new Flex(Orientation.VERTICAL);

    Region c1 = new MockBiased(Orientation.HORIZONTAL, 30.5, 20.5);
    Region c2 = new MockResizable(20, 20, 40, 40);

    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.layout();

    // same scenario as biasTraditional (a real VBox), but using Flex -- confirms Flex matches VBox's own behavior

    assertEquals(Orientation.HORIZONTAL, flex.getContentBias());
    assertEquals(20, flex.minWidth(-1));
    assertEquals(52, flex.minHeight(flex.minWidth(-1)));
    assertEquals(40, flex.prefWidth(-1));
    assertEquals(56, flex.prefHeight(flex.prefWidth(-1)));

    assertChildBounds(
      """
      +------------+
      |  1: 20x32  |
      +------------+
      |  2: 20x20  |
      +------------+
      """,
      c1, c2
    );
  }


  @Test
  void oddCase2() {
    final double W = 200;
    final double H = 70;

    HBox flex = new HBox();
    VBox nested = new VBox();
    Region c1 = new MockBiased(Orientation.HORIZONTAL, 30.5, 20.5);
    Region c2 = new MockResizable(40, 40);

    nested.getChildren().addAll(c1, c2);
    flex.getChildren().add(nested);
    flex.resize(W, H);
    flex.layout();

    assertEquals(Orientation.HORIZONTAL, flex.getContentBias());
    assertEquals(10, flex.minWidth(-1));
    assertEquals(64, flex.minHeight(flex.minWidth(-1)));
    assertEquals(40, flex.prefWidth(-1));
    assertEquals(56, flex.prefHeight(flex.prefWidth(-1)));

    assertBounds(
      """
      +--------------------+---------------------------+
      |          0         |                           |
      |   +------------+   |                           |
      |   |  1: 40x16  |   |                           |
      | 0 +------------+ 0 |          160x70           |
      |   |  2: 40x40  |   |                           |
      |   +------------+   |                           |
      |         14         |                           |
      +--------------------+---------------------------+
      """,
      flex
    );

    assertChildBounds(
      """
      +------------+
      |  1: 40x16  |
      +------------+
      |  2: 40x40  |
      +------------+
      """,
      c1, c2
    );
  }

  @Test
  void nestedFlexWithContentBias() {
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex(Orientation.HORIZONTAL);
    Flex nested = new Flex(Orientation.VERTICAL);
    Region c1 = new MockBiased(Orientation.HORIZONTAL, 30.5, 20.5);
    Region c2 = new MockResizable(40, 40);

    nested.getChildren().addAll(c1, c2);
    flex.getChildren().add(nested);
    flex.resize(W, H);
    flex.layout();

    assertEquals(Orientation.HORIZONTAL, flex.getContentBias());
    assertMinSizes(flex, 10, 64);
    assertPrefSizes(flex, 40, 56);
    assertMaxSizes(flex, 626, 5021);

    assertBounds(
      """
      +--------------------+
      |          0         |
      |   +------------+   |
      |   |  1: 40x16  |   |
      | 0 +------------+ 0 | 160x70
      |   |  2: 40x40  |   |
      |   +------------+   |
      |         14         |
      +--------------------+
      """,
      flex
    );

    assertChildBounds(
      """
      +------------+
      |  1: 40x16  |
      +------------+
      |  2: 40x40  |
      +------------+
      """,
      c1, c2
    );
  }

  @Test
  void nestedFlexWithoutContentBias() {
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex(Orientation.HORIZONTAL);
    Flex nested = new Flex(Orientation.VERTICAL);
    Region c1 = new MockResizable(30.5, 20.5);
    Region c2 = new MockResizable(40, 40);

    nested.getChildren().addAll(c1, c2);
    flex.getChildren().add(nested);
    flex.resize(W, H);
    flex.layout();

    assertEquals(null, flex.getContentBias());
    assertMinSizes(flex, 1, 2);
    assertPrefSizes(flex, 40, 61);
    assertMaxSizes(flex, 5000, 10000);

    assertBounds(
      """
      +--------------------+
      |          0         |
      |   +------------+   |
      |   |  1: 40x21  |   |
      | 0 +------------+ 0 |  160x70
      |   |  2: 40x40  |   |
      |   +------------+   |
      |          9         |
      +--------------------+
      """,
      flex
    );
  }

  @Test
  void shouldHandleEmptyFlex() {
    final double SPACING = 3.5;
    final double W = 1;
    final double H = 1;

    Flex flex = new Flex();

    flex.setSpacing(SPACING);
    flex.resize(W, H);
    flex.layout();

    assertMinSizes(flex, 0, 0);  // ensure spacing is not multiplied by -1 :)
    assertPrefSizes(flex, 0, 0);
    assertMaxSizes(flex, 0, 0);
  }

  @Test
  void shouldHandleTinyFlex() {
    final double SPACING = 3.5;
    final double W = 1;  // Note, this is less than the minimum width, but should deal with it without error
    final double H = 1;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70);

    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    assertMinSizes(flex, 14, 1);
    assertPrefSizes(flex, 311, 70);
    assertMaxSizes(flex, 15008, 5000);

    assertChildBounds(
      """
      +----------------------------------------------+
      |  1: 2x1  | 4x70 |  2: 2x1  | 4x70 |  3: 2x1  |
      +----------------------------------------------+
      """,
      c1, c2, c3
    );
  }

  @Test
  void shouldIgnoreUnmanagedChildren() {
    final double SPACING = 3.5;
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70);

    c1.setManaged(false);

    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    assertMinSizes(flex, 8, 1);
    assertPrefSizes(flex, 206, 70);
    assertMaxSizes(flex, 10004, 5000);

    assertChildBounds(
      """
      +----------------------------------+
      |  1: 98x70   | 4x70 |  2: 98x70   |
      +----------------------------------+
      """,
      c2, c3
    );

    assertBounds(
      """
      +----------------------------------+
      |  1: 98x70   | 4x70 |  2: 98x70   |
      +----------------------------------+
      """,
      flex
    );

    Bounds oldBounds = c3.getBoundsInParent();

    c3.setManaged(false);

    flex.layout();

    assertBounds(
      """
      +--------------------------+
      |  1: 101x70  |    99x70   |
      +--------------------------+
      """,
      flex
    );

    // Check that c3 wasn't moved from previous location:
    assertThat(c3.getBoundsInParent()).isEqualTo(oldBounds);
  }


  @Test
  void shouldUpdateWhenSpacingChanged() {
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(20, 20);
    Region c2 = new MockResizable(20, 20);

    flex.setSpacing(40);
    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.layout();

    assertMinSizes(flex, 42, 1);
    assertPrefSizes(flex, 80, 20);
    assertMaxSizes(flex, 10040, 5000);

    assertBounds(
      """
      +----------------------------------------------------+
      |  1: 20x70   | 40x70 |  2: 20x70  |     120x70      |
      +----------------------------------------------------+
      """,
      flex
    );

    flex.setSpacing(50);
    flex.layout();

    assertBounds(
      """
      +----------------------------------------------------+
      |  1: 20x70   | 50x70 |  2: 20x70  |     110x70      |
      +----------------------------------------------------+
      """,
      flex
    );
  }

  @Test
  void shouldUpdateWhenFillAndAlignmentChanged() {
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(20, 20);
    Region c2 = new MockResizable(20, 20);

    flex.setSpacing(40);
    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.layout();

    assertMinSizes(flex, 42, 1);
    assertPrefSizes(flex, 80, 20);
    assertMaxSizes(flex, 10040, 5000);

    assertBounds(
      """
      +----------------------------------------------------+
      |             |       |            |                 |
      |  1: 20x70   | 40x70 |  2: 20x70  |     120x70      |
      |             |       |            |                 |
      +----------------------------------------------------+
      """,
      flex
    );

    flex.setFill(false);
    flex.layout();

    assertBounds(
      """
      +----------------------------------------------------+
      |  1: 20x20   | 40x20 |  2: 20x20  |     120x20      |
      +----------------------------------------------------+
      |                      200x50                        |
      +----------------------------------------------------+
      """,
      flex
    );

    flex.setAlignment(Pos.BOTTOM_RIGHT);
    flex.layout();

    assertBounds(
      """
      +----------------------------------------------------+
      |                      200x50                        |
      +----------------------------------------------------+
      |      120x20      | 1: 20x20   | 40x20 |  2: 20x20  |
      +----------------------------------------------------+
      """,
      flex
    );
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldHandleAlignment(boolean horizontal) {
    final double W = horizontal ? 200 : 70;
    final double H = horizontal ? 70 : 200;

    Flex flex = horizontal ? Flex.hflex().build() : Flex.vflex().build();

    Region c1 = horizontal ? new MockResizable(20, 10, 20, 10) : new MockResizable(10, 20, 10, 20);
    Region c2 = horizontal ? new MockResizable(20, 10, 20, 30) : new MockResizable(10, 20, 30, 20);

    flex.setSpacing(10);
    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.setFill(false);

    flex.setAlignment(Pos.TOP_LEFT);
    flex.layout();

    assertBounds(
      horizontal ? Rotation.NONE : Rotation.R90_FLIP,
      """
      +----------------------------------------------------+
      |  1: 20x10   |       |            |                 |
      +-------------+ 10x30 |  2: 20x30  |     150x30      |
      |    20x20    |       |            |                 |
      +----------------------------------------------------+
      |                      200x40                        |
      +----------------------------------------------------+
      """,
      flex
    );

    flex.setAlignment(Pos.BOTTOM_RIGHT);
    flex.layout();

    assertBounds(
      horizontal ? Rotation.NONE : Rotation.R90_FLIP,
      """
      +----------------------------------------------------+
      |                      200x40                        |
      +----------------------------------------------------+
      |                 |    20x20    |       |            |
      |     150x30      +-------------+ 10x30 |  2: 20x30  |
      |                 |  1: 20x10   |       |            |
      +----------------------------------------------------+
      """,
      flex
    );

    flex.setAlignment(Pos.CENTER);
    flex.layout();

    assertBounds(
      horizontal ? Rotation.NONE : Rotation.R90_FLIP,
      """
      +----------------------------------------------------+
      |                      200x20                        |
      +----------------------------------------------------+
      |        |    20x10    |       |            |        |
      |        +-------------+       |            |        |
      | 75x30  |  1: 20x10   | 10x30 |  2: 20x30  | 75x30  |
      |        +-------------+       |            |        |
      |        |    20x10    |       |            |        |
      +----------------------------------------------------+
      |                      200x20                        |
      +----------------------------------------------------+
      """,
      flex
    );
  }

  @Test
  void shouldHandleAlignment_Vertical() {  // TODO no longer needed, absorbed into above method
    final double W = 70;
    final double H = 200;

    Flex flex = Flex.vflex().build();

    Region c1 = new MockResizable(10, 20, 10, 20);
    Region c2 = new MockResizable(10, 20, 30, 20);

    flex.setSpacing(10);
    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.setFill(false);

    flex.setAlignment(Pos.TOP_LEFT);
    flex.layout();

    assertBounds(
      """
      +-----------------------------------+
      | 1: 10x20 |       |                |
      +----------+-------+                |
      |        0x10      |                |
      +------------------+                |
      |     2: 30x20     |      40x200    |
      +------------------+                |
      |                  |                |
      |      30x150      |                |
      |                  |                |
      +------------------+----------------+
      """,
      flex
    );

    assertBounds(
      Rotation.R90_FLIP,
      """
      +----------------------------------------------------+
      |  1: 20x10   |       |            |                 |
      +-------------+ 10x10 |  2: 20x30  |     150x30      |
      |    20x20    |       |            |                 |
      +----------------------------------------------------+
      |                      200x40                        |
      +----------------------------------------------------+
      """,
      flex
    );


    flex.setAlignment(Pos.BOTTOM_RIGHT);
    flex.layout();

    assertBounds(
      """
      +----------------+------------------+
      |                |                  |
      |                |      30x150      |
      |                |                  |
      |                +------------------+
      |     40x200     | 20x20 | 1: 10x20 |
      |                +-------+----------+
      |                |       30x10      |
      |                +------------------+
      |                |     2: 30x20     |
      +----------------+------------------+
      """,
      flex
    );

    flex.setAlignment(Pos.CENTER);
    flex.layout();

    assertBounds(
      Rotation.R90_FLIP,
      """
      +----------------------------------------------------+
      |                      200x20                        |
      +----------------------------------------------------+
      |        |    20x10    |       |            |        |
      |        +-------------+       |            |        |
      | 75x30  |  1: 20x10   | 10x30 |  2: 20x30  | 75x30  |
      |        +-------------+       |            |        |
      |        |    20x10    |       |            |        |
      +----------------------------------------------------+
      |                      200x20                        |
      +----------------------------------------------------+
      """,
      flex
    );
  }

  @Test
  void shouldApplyPadding() {
    final double SPACING = 3.5;
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70);

    double top = 5;
    double left = 2;
    double bottom = 3;
    double right = 4;
    Insets insets = new Insets(top, right, bottom, left);

    flex.setPadding(insets);
    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 3 * Math.ceil(1.5) + 2 * spacing + left + right, 1 + top + bottom);
    assertPrefSizes(flex, 3 * Math.ceil(100.5) + 2 * spacing + left + right, 70 + top + bottom);
    assertMaxSizes(flex, 15014, 5008);

    assertBounds(
      """
      +-----------------------------------------------------------------+
      |                                 5                               |
      |     +-----------------------------------------------------+     |
      |  2  |  1: 62x62   | 4x0 |  2: 62x62   | 4x0 |  3: 62x62   |  4  |
      |     +-----------------------------------------------------+     |
      |                                 3                               |
      +-----------------------------------------------------------------+
      """,
      flex
    );
  }

  @Disabled("base line support is not yet decided how to it should be solved")
  @Test
  void shouldArrangeAlongBaseline() {
    final double SPACING = 3.5;
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70).baselineOffset(10);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70).baselineOffset(20);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70).baselineOffset(Node.BASELINE_OFFSET_SAME_AS_HEIGHT);

    double top = 5;
    double left = 2;
    double bottom = 3;
    double right = 4;
    Insets insets = new Insets(top, right, bottom, left);

    flex.setPadding(insets);
    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.alignmentProperty().set(Pos.BASELINE_CENTER);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertThat(flex.getBaselineOffset()).isEqualTo(20);
    assertMinSizes(flex, 3 * Math.ceil(1.5) + 2 * spacing + left + right, 1 + top + bottom);
    assertPrefSizes(flex, 3 * Math.ceil(100.5) + 2 * spacing + left + right, 70 + top + bottom);
    assertMaxSizes(flex, 15014, 5008);

    assertBounds(
      """
      +-----------------------------------------------------------------+
      |                                 5                               |
      |     +-------------+-----+-------------+-----+-------------+     |
      |     |    0x10     |     |             |     |             |     |
      |     +-------------+     |             |     |             |     |
      |     |             |     |             |     |  3: 62x42   |     |
      |  2  |             | 4x0 |  2: 62x62   | 4x0 |             |  4  |
      |     |  1: 62x52   |     |             |     +-------------+     |
      |     |             |     |             |     |     0x20    |     |
      |     |             |     |             |     |             |     |
      |     +-------------+-----+-------------+-----+-------------+     |
      |                                 3                               |
      +-----------------------------------------------------------------+
      """,
      flex
    );
  }

  @Test
  void shouldReduceSizesWhenSpaceIsLow() {
    final double SPACING = 3.5;
    final double W = 200;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70);

    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 3 * Math.ceil(1.5) + 2 * spacing, 1);
    assertPrefSizes(flex, 3 * Math.ceil(100.5) + 2 * spacing, 70);
    assertMaxSizes(flex, 15008, 5000);

    assertChildBounds(
      """
      +-------------------------------------------------------+
      |  1: 64x70   | 4x70 |  2: 64x70   | 4x70 |  3: 64x70   |
      +-------------------------------------------------------+
      """,
      c1, c2, c3
    );
  }

  @Test
  void shouldUsePreferredSizesWhenSpaceIsPlenty() {
    final double SPACING = 3.5;
    final double W = 400;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70);

    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 3 * Math.ceil(1.5) + 2 * spacing, 1);
    assertPrefSizes(flex, 3 * Math.ceil(100.5) + 2 * spacing, 70);
    assertMaxSizes(flex, 15008, 5000);

    assertChildBounds(
      """
      +----------------------------------------------------------+
      |  1: 101x70   | 4x70 |  2: 101x70   | 4x70 |  3: 101x70   |
      +----------------------------------------------------------+
      """,
      c1, c2, c3
    );
  }

  @Test
  void shouldGrowChildrenThatAllowItWhenSpaceIsPlenty() {
    final double SPACING = 3.5;
    final double W = 400;
    final double H = 70;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1.5, 1, 100.5, 70);
    Region c2 = new MockResizable(1.5, 1, 100.5, 70);
    Region c3 = new MockResizable(1.5, 1, 100.5, 70);

    Flex.setGrow(c2, Priority.ALWAYS);

    flex.setSpacing(SPACING);
    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 3 * Math.ceil(1.5) + 2 * spacing, 1);
    assertPrefSizes(flex, 3 * Math.ceil(100.5) + 2 * spacing, 70);
    assertMaxSizes(flex, 15008, 5000);

    assertChildBounds(
      """
      +----------------------------------------------------------+
      |  1: 101x70   | 4x70 |  2: 190x70   | 4x70 |  3: 101x70   |
      +----------------------------------------------------------+
      """,
      c1, c2, c3
    );
  }

  @Test
  void shouldNotGrowBeyondMaxSize() {
    final double W = 200;
    final double H = 20;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1, 1, 50, 20, 80, 5000);  // capped at 80
    Region c2 = new MockResizable(1, 1, 50, 20);

    Flex.setGrow(c1, Priority.ALWAYS);

    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.layout();

    // c1 wants to grow to fill all available space, but must stop at its own max of 80

    assertChildBounds(
      """
      +----------------------------------------------------------------+
      |  1: 80x20  |  2: 50x20  | 70x20                                |
      +----------------------------------------------------------------+
      """,
      c1, c2
    );
  }

  @Test
  void shouldRedistributeExtraSpaceWhenGrowingChildReachesMax() {
    final double W = 200;
    final double H = 20;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1, 1, 50, 20, 80, 5000);  // capped at 80
    Region c2 = new MockResizable(1, 1, 50, 20);  // uncapped, absorbs whatever c1 could not take

    Flex.setGrow(c1, Priority.ALWAYS);
    Flex.setGrow(c2, Priority.ALWAYS);

    flex.getChildren().addAll(c1, c2);
    flex.resize(W, H);
    flex.layout();

    assertChildBounds(
      """
      +----------------------------------------------------------------+
      |  1: 80x20  |  2: 120x20                                        |
      +----------------------------------------------------------------+
      """,
      c1, c2
    );
  }

  @Test
  void shouldNotFillBeyondMaxCrossAxisSize() {
    final double W = 50;
    final double H = 80;

    Flex flex = Flex.hflex().build();

    Region c1 = new MockResizable(1, 1, 50, 20, 5000, 30);  // capped at a max height of 30

    flex.getChildren().add(c1);
    flex.resize(W, H);
    flex.layout();

    // fill defaults to true, so c1 would otherwise be stretched to fill the full 80 tall area

    assertChildBounds(
      """
      +----------+
      | 1: 50x30 |
      +----------+
      | 50x50    |
      +----------+
      """,
      c1
    );
  }

  @Test
  void shouldNotShrinkBelowMinSize() {
    final double W = 150;
    final double H = 20;

    Flex flex = new Flex();

    Region c1 = new MockResizable(1, 1, 100, 20);
    Region c2 = new MockResizable(80, 1, 100, 20);  // cannot shrink below 80
    Region c3 = new MockResizable(1, 1, 100, 20);

    flex.getChildren().addAll(c1, c2, c3);
    flex.resize(W, H);
    flex.layout();

    // total pref (300) far exceeds available space (150); c2 stops shrinking at its min of 80,
    // c1/c3 absorb the rest of the squeeze evenly

    assertChildBounds(
      """
      +--------------------------------------------------+
      |  1: 35x20  |  2: 80x20            |  3: 35x20    |
      +--------------------------------------------------+
      """,
      c1, c2, c3
    );
  }

  @Test
  void shouldDealWithSpacing() {
    final double W = 220;
    final double H = 40;

    Flex row1 = Flex.hflex().build();
    Flex row2 = Flex.hflex().build();

    Region c1 = new MockResizable(1, 1, 30, 20);
    Region c2 = new MockResizable(1, 1, 20, 20);
    Region c3 = new MockResizable(1, 1, 20, 20);
    Region c4 = new MockResizable(1, 1, 20, 20);

    row1.setSpacing(10);
    row2.setSpacing(20);

    row1.getChildren().addAll(c1, c2);
    row2.getChildren().addAll(c3, c4);

    Flex flex = Flex.vflex().with(row1, row2);

    flex.alignedProperty().set(true);
    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(20);

    assertMinSizes(flex, 2 * 1 + 1 * spacing, 2);  // based on row2
    assertPrefSizes(flex, 30 + 1 * spacing + 20, 40);
    assertMaxSizes(flex, 10020, 10000);

    assertBounds(
      """
      +----------------------------------+
      |  1: 30x20   |      |  2: 20x20   |
      +-------------| 20x0 |-------------+ 150x0
      |  3: 20x20   |      |  4: 20x20   |
      +----------------------------------+
      """,
      flex
    );

    flex.resize(70, H);
    flex.layout();

    assertBounds(
      """
      +----------------------------------+
      |  1: 30x20   |      |  2: 20x20   |
      +-------------| 20x0 |-------------+ 0x0
      |  3: 20x20   |      |  4: 20x20   |
      +----------------------------------+
      """,
      flex
    );

    flex.resize(60, H);
    flex.layout();

    assertBounds(
      """
      +----------------------------------+
      |  1: 25x20   |      |  2: 15x20   |
      +-------------| 20x0 |-------------+ 0x0
      |  3: 20x20   |      |  4: 15x20   |
      +----------------------------------+
      """,
      flex
    );

    flex.resize(40, H);
    flex.layout();

    assertBounds(
      """
      +----------------------------------+
      |  1: 15x20   |      |  2: 5x20    |
      +-------------| 20x0 |-------------+ 0x0
      |  3: 15x20   |      |  4: 5x20    |
      +----------------------------------+
      """,
      flex
    );

    flex.resize(30, H);
    flex.layout();

    assertBounds(
      """
      +----------------------------------+
      |  1: 9x20    |      |  2: 1x20    |
      +-------------| 20x0 |-------------+ 0x0
      |  3: 9x20    |      |  4: 1x20    |
      +----------------------------------+
      """,
      flex
    );
  }


  @Test
  void shouldAlignNestedFlex() {
    final double SPACING = 3.5;
    final double W = 220;
    final double H = 70;

    Flex flex = Flex.vflex().build();

    flex.alignedProperty().set(true);

    Flex row1 = Flex.hflex().build();
    Flex row2 = Flex.hflex().build();

    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);
    Region c4 = new MockResizable(1, 1, 100, 20);
    Region c5 = new MockResizable(1, 1, 50, 20);

    row1.setSpacing(SPACING / 2);  // should be ignored as row2 has higher spacing
    row2.setSpacing(SPACING);

    Flex group = Flex.hflex().with(c4, c5);

    flex.getChildren().addAll(row1, row2);
    row1.getChildren().addAll(c1, c2);
    row2.getChildren().addAll(c3, group);
    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 3 * 1 + 1 * spacing, 2);  // based on row2
    assertPrefSizes(flex, 40 + 1 * spacing + 100 + 50, 40);
    assertMaxSizes(flex, 10004, 10000);


    assertBounds(
      """
      +-----------------------------------------------+----------+--------
      |  1: 40x20   |      |  2: 100x20               |[1,2]:H50 |
      +-------------|  H4  |--------------------------+----------+  26x0
      |  3: 40x20   |      |  4: 100x20               | 5: 50x20 |
      +-----------------------------------------------+----------+--------
                                0x30
      """,
      flex
    );

    Flex.setGrow(c1, Priority.ALWAYS);

    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------+
      |  1: 66x20      |      |  2: 100x20               |[1,2]:H50
      +----------------| 4x40 |--------------------------+----------+
      |  3: 40x20   |  |      |  4: 100x20               | 5: 50x20 |
      +--------------------------------------------------+----------+
                                0x30
      """,
      flex
    );

    Flex.setGrow(c2, Priority.ALWAYS);

    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------------------------+
      |  1: 53x20      |      |  2: 163x20                                 |
      +----------------| 4x40 |--------------------------+----------+------------+
      |  3: 40x20   |  |      |  4: 100x20               | 5: 50x20 |[3,4,5]:H13 |
      +--------------------------------------------------+----------+------------+
                                0x30
      """,
      flex
    );

    Flex.setGrow(c2, Priority.NEVER);
    Flex.setGrow(c4, Priority.ALWAYS);

    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------+
      |  1: 66x20      |      |  2: 100x20               |[1,2]:H50
      +----------------| 4x40 |--------------------------+----------+
      |  3: 40x20   |  |      |  4: 100x20               | 5: 50x20 |
      +--------------------------------------------------+----------+
                                0x30
      """,
      flex
    );

    // To get c4 to grow, also need to set its group to grow within row2
    Flex.setGrow(group, Priority.ALWAYS);

    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------+
      |  1: 53x20      |      |  2: 100x20               |[1,2]:H63
      +----------------| 4x40 |--------------------------+-------------+
      |  3: 40x20   |  |      |  4: 113x20                  | 5: 50x20 |
      +----------------------------------------------------------------+
                                0x30
      """,
      flex
    );

    row1.setAlignment(Pos.CENTER_RIGHT);

    flex.layout();

    assertBounds(
      """
      +----------------------------------------------------------------+
      |  1: 53x20      |      |  63x0  |         2: 100x20             |
      +----------------| 4x40 |----------------------------------------+
      |  3: 40x20   |  |      |  4: 113x20                  | 5: 50x20 |
      +----------------------------------------------------------------+
                                0x30
      """,
      flex
    );
  }

  @Test
  void shouldAlignNestedFlexAcrossSameTypeParents() {
    final double SPACING = 3.5;
    final double W = 220;
    final double H = 70;

    // Root VFlex coordinating the master grid layout

    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);
    Region c4 = new MockResizable(1, 1, 100, 20);
    Region c5 = new MockResizable(1, 1, 50, 20);

    Flex row1 = Flex.hflex().with(c1, c2);

    // The nested HFlex representing column 1 inside row2
    Flex group = Flex.hflex().with(c4, c5);
    Flex row2 = Flex.hflex().with(c3, group);

    // Grouping container of same type (VFlex, but unaligned)
    // This is the wrapper that MUST be skipped when resolving row2's coordinator!
    Flex groupWrapper = Flex.vflex().with(row2);
    Flex flex = Flex.vflex().with(row1, groupWrapper);

    // Constructed hierarchy:
    // flex (VFlex, aligned)
    //  +-- row1 (HFlex) --> c1, c2
    //  +-- groupWrapper (VFlex, unaligned structural container)
    //        +-- row2 (HFlex) --> c3, group (HFlex)

    flex.alignedProperty().set(true);

    row1.setSpacing(SPACING / 2); // Should be overridden by the coordinator's spacing resolving to row2
    row2.setSpacing(SPACING);

    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    // Assertions mapping directly to your grid layout math
    assertMinSizes(flex, 3 * 1 + 1 * spacing, 2);
    assertPrefSizes(flex, 40 + 1 * spacing + 100 + 50, 40);
    assertMaxSizes(flex, 10004, 10000);

    // Layout check 1: Initial distribution aligning columns cleanly
    assertBounds(
      """
      +-----------------------------------------------+----------+--------
      |  1: 40x20   |      |  2: 100x20               |[1,2]:H50 |
      +-------------|  H4  |--------------------------+----------+  26x0
      |  3: 40x20   |      |  4: 100x20               | 5: 50x20 |
      +-----------------------------------------------+----------+--------
                                0x30
      """,
      flex
    );

    // Layout check 2: c1 set to Priority.ALWAYS
    Flex.setGrow(c1, Priority.ALWAYS);
    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------+
      |  1: 66x20      |      |  2: 100x20               |[1,2]:H50
      +----------------| 4x40 |--------------------------+----------+
      |  3: 40x20   |  |      |  4: 100x20               | 5: 50x20 |
      +--------------------------------------------------+----------+
                                0x30
      """,
      flex
    );

    // Layout check 3: c2 set to Priority.ALWAYS
    Flex.setGrow(c2, Priority.ALWAYS);
    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------------------------+
      |  1: 53x20      |      |  2: 163x20                                 |
      +----------------| 4x40 |--------------------------+----------+------------+
      |  3: 40x20   |  |      |  4: 100x20               | 5: 50x20 |[3,4,5]:H13 |
      +--------------------------------------------------+----------+------------+
                                0x30
      """,
      flex
    );

    // Layout check 4: c4 grow priority within its group
    Flex.setGrow(c2, Priority.NEVER);
    Flex.setGrow(c4, Priority.ALWAYS);
    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------+
      |  1: 66x20      |      |  2: 100x20               |[1,2]:H50
      +----------------| 4x40 |--------------------------+----------+
      |  3: 40x20   |  |      |  4: 100x20               | 5: 50x20 |
      +--------------------------------------------------+----------+
                                0x30
      """,
      flex
    );

    // Layout check 5: Let the nested group grow inside its row row2
    Flex.setGrow(group, Priority.ALWAYS);
    flex.layout();

    assertBounds(
      """
      +--------------------------------------------------+
      |  1: 53x20      |      |  2: 100x20               |[1,2]:H63
      +----------------| 4x40 |--------------------------+-------------+
      |  3: 40x20   |  |      |  4: 113x20                  | 5: 50x20 |
      +----------------------------------------------------------------+
                                0x30
      """,
      flex
    );

    // Layout check 6: Right alignment check
    row1.setAlignment(Pos.CENTER_RIGHT);
    flex.layout();

    assertBounds(
      """
      +----------------------------------------------------------------+
      |  1: 53x20      |      |  63x0  |         2: 100x20             |
      +----------------| 4x40 |----------------------------------------+
      |  3: 40x20   |  |      |  4: 113x20                  | 5: 50x20 |
      +----------------------------------------------------------------+
                                0x30
      """,
      flex
    );
  }

  @Test
  void shouldPadNestedHFlex() {
    final double SPACING = 3.5;
    final double W = 200;
    final double H = 70;

    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);
    Region c4 = new MockResizable(1, 1, 100, 20);
    Region c5 = new MockResizable(1, 1, 50, 20);
    Region c6 = new MockResizable(1, 1, 40, 20);
    Region c7 = new MockResizable(1, 1, 100, 20);

    Flex group = Flex.hflex().with(c4, c5);

    Flex row1 = Flex.hflex().with(c1, c2);
    Flex row2 = Flex.hflex().with(c3, group);
    Flex row3 = Flex.hflex().with(c6, c7);

    row1.setSpacing(SPACING / 2);  // should be ignored as row2 has higher spacing
    row2.setSpacing(SPACING);

    Flex flex = Flex.vflex().with(row1, row2, row3);
    flex.alignedProperty().set(true);

    double top = 5;
    double left = 2;
    double bottom = 3;
    double right = 4;

    Insets insets = new Insets(top, right, bottom, left);

    group.setPadding(insets);

    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 1 + 1 * spacing + left + 1 + 1 + right, 1 + top + 1 + bottom + 1);  // based on row2
    assertPrefSizes(flex, 40 + 1 * spacing + left + 100 + 50 + right, 20 + top + 20 + bottom + 20);
    assertMaxSizes(flex, 10004, 15008);

    assertBounds(
      """
      +-------------+-----+--------------------------+------------------+
      |  1: 40x20   |     |  2: 100x20               |[1,2]:H56         |
      +-------------+     +--------------------------+------------------+
      |             |     |                      5                      |
      |             |     |     +----------------------+----------+     |
      |  3: 40x28   | 4x0 |  2  |   4: 100x20          | 5: 50x20 |  4  |
      |             |     |     +----------------------+----------+     |
      |             |     |                      3                      |
      +-------------+     +--------------------------+------------------+
      |  6: 40x20   |     |  7: 100x20               |[6,7]:H56         |
      +-------------+-----+--------------------------+------------------+
                                   0x2
      """,
      flex
    );
  }

  @Test
  void shouldPadNestedVFlex() {
    final double SPACING = 3.5;
    final double W = 200;
    final double H = 100;

    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);
    Region c4 = new MockResizable(1, 1, 100, 20);
    Region c5 = new MockResizable(1, 1, 50, 20);
    Region c6 = new MockResizable(1, 1, 40, 20);
    Region c7 = new MockResizable(1, 1, 100, 20);

    Flex group = Flex.vflex().with(c4, c5);

    Flex row1 = Flex.hflex().with(c1, c2);
    Flex row2 = Flex.hflex().with(c3, group);
    Flex row3 = Flex.hflex().with(c6, c7);

    row1.setSpacing(SPACING / 2);  // should be ignored as row2 has higher spacing
    row2.setSpacing(SPACING);

    Flex flex = Flex.vflex().with(row1, row2, row3);

    flex.alignedProperty().set(true);

    double top = 5;
    double left = 2;
    double bottom = 3;
    double right = 4;

    Insets insets = new Insets(top, right, bottom, left);

    group.setPadding(insets);

    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 1 + 1 * spacing + left + 1 + right, 1 + top + 2 * 1 + bottom + 1);  // based on row2
    assertPrefSizes(flex, 40 + 1 * spacing + left + 100 + right, 20 + top + 20 + 20 + bottom + 20);
    assertMaxSizes(flex, 10004, 20008);

    assertBounds(
      """
      +-------------+-----+--------------------------+------------------+
      |  1: 40x20   |     |  2: 100x20               | [1,2]:H6         |
      +-------------+     +--------------------------+------------------+
      |             |     |                      5                      |
      |             |     |     +---------------------------------+     |
      |             |     |     |           4: 100x20             |     |
      |  3: 40x48   | 4x0 |  2  +---------------------------------+  4  |  50x0
      |             |     |     |           5: 100x20             |     |
      |             |     |     +---------------------------------+     |
      |             |     |                      3                      |
      +-------------+     +--------------------------+------------------+
      |  6: 40x20   |     |  7: 100x20               | [6,7]:H6         |
      +-------------+-----+--------------------------+------------------+---------
                                   0x12                                 |
      """,
      flex
    );

    flex.resize(W, 88);
    flex.layout();

    assertBounds(
      """
      +-------------+-----+--------------------------+------------------+
      |  1: 40x20   |     |  2: 100x20               | [1,2]:H6         |
      +-------------+     +--------------------------+------+-----------+
      |             |     |                      5                      |
      |             |     |     +---------------------------------+     |
      |             |     |     |           4: 100x20             |     |
      |  3: 40x48   | 4x0 |  2  +---------------------------------+  4  |  50x0
      |             |     |     |           5: 100x20             |     |
      |             |     |     +---------------------------------+     |
      |             |     |                      3                      |
      +-------------+     +--------------------------+------------------+
      |  6: 40x20   |     |  7: 100x20               | [6,7]:H6         |
      +-------------+-----+--------------------------+------------------+---------
                                   0x0                                  |
      """,
      flex
    );

    flex.resize(W, 80);
    flex.layout();

    assertBounds(
      """
      +-------------+-----+--------------------------+------------------+
      |  1: 40x17   |     |  2: 100x17               | [1,2]:H6         |
      +-------------+     +--------------------------+------------------+
      |             |     |                      5                      |
      |             |     |     +---------------------------------+     |
      |             |     |     |           4: 100x18             |     |
      |  3: 40x45   | 4x0 |  2  +---------------------------------+  4  |  50x0
      |             |     |     |           5: 100x19             |     |
      |             |     |     +---------------------------------+     |
      |             |     |                      3                      |
      +-------------+     +--------------------------+------------------+
      |  6: 40x18   |     |  7: 100x18               | [6,7]:H6         |
      +-------------+-----+--------------------------+------------------+---------
                                   0x0                                  |
      """,
      flex
    );
  }

  /*
   * Ensures a nested Flex that is unmanaged does not assume it will have a
   * correct minimum size to work with. Being unmanaged means its container won't
   * position or size it, and its size can therefore be anything.
   */
  @Test
  void shouldHandleUnmanagedNestedFlex() {
    final double SPACING = 3.666;
    final double W = 220;
    final double H = 70;

    Flex flex = Flex.vflex().build();

    flex.alignedProperty().set(true);

    Flex row1 = Flex.hflex().build();
    Flex row2 = Flex.hflex().build();

    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);
    Region c4 = new MockResizable(1, 1, 100, 20);
    Region c5 = new MockResizable(1, 1, 50, 20);

    row1.setSpacing(SPACING / 2);  // should be ignored as row2 has higher spacing
    row2.setSpacing(SPACING);

    Flex group = Flex.hflex().with(c4, c5);

    flex.getChildren().addAll(row1, row2);
    row1.getChildren().addAll(c1, c2);
    row2.getChildren().addAll(c3, group);

    row1.setManaged(false);

    flex.resize(W, H);
    flex.layout();

    double spacing = Math.round(SPACING);

    assertMinSizes(flex, 3 * 1 + 1 * spacing, 1);  // based on row2
    assertPrefSizes(flex, 40 + 1 * spacing + 100 + 50, 20);
    assertMaxSizes(flex, 15004, 5000);

    assertChildBounds(
      """
      +-----------------------------------------------+----------+
      |  1: 40x20   | 4x40 |  2: 100x20               | 3: 50x20 |
      +-----------------------------------------------+----------+
      """,
      c3, c4, c5
    );
  }

  @Test
  void shouldConfineLeafToOwnRowWhenCrossedSiblingSpansMultipleRows() {
    Region icon = new MockResizable(1, 1, 40, 60);
    Region title = new MockResizable(1, 1, 100, 20);
    Region subtitle = new MockResizable(1, 1, 100, 16);

    Flex textGroup = Flex.vflex().with(title, subtitle);
    Flex flex = Flex.hflex().with(icon, textGroup);

    flex.alignedProperty().set(true);

    flex.resize(140, 76);
    flex.layout();

    /*
     * icon is a direct leaf (a plain Region), not a Flex -- it must only
     * affect and occupy row0 (max(icon=60, title=20) = 60), not span into
     * row1 (subtitle's own 16-tall row), even though icon's own height (60)
     * covers both rows combined. This is the same "empty grid spot" case
     * StandaloneFlexLayoutTest caught for a bare FlexLayout tree, reproduced
     * here through real Flex nodes instead.
     */

    assertBounds(
      """
      +------------+--------------+
      |            | 2: 100x20    |
      | 1: 40x60   +--------------+
      |            |   100x40     |
      +------------+--------------+
      |   40x16    | 3: 100x16    |
      +------------+--------------+
      """,
      flex
    );
  }

  @Test
  void shouldSurviveChildRequestingLayoutDuringMeasurement() {

    /*
     * A control like a ComboBox updates its own display area while being
     * measured (e.g. it adds a child because its preferred width was stale),
     * and adding a child fires requestLayout() up the parent chain. That
     * requestLayout() reaches the FlexLayouts in between as invalidate(),
     * which used to clear the Tracks caches in place -- i.e. structurally
     * modifying the very HashMap whose computeIfAbsent() is still on the
     * stack. Java 19+ detects exactly that and throws a
     * ConcurrentModificationException (see HashMap.computeIfAbsent's
     * mc != modCount check). The caches must survive a requestLayout() that
     * fires mid-measurement.
     */

    Region eager = new Region() {
      boolean firstCall = true;

      @Override
      public double computeMinWidth(double height) {
        if(getParent() != null && firstCall) {
          getParent().requestLayout();

          firstCall = false;

          return 10;  // wrong answer
        }

        return 20;
      }

      @Override
      public double computePrefWidth(double height) {
        return 20;
      }

      @Override
      public double computeMaxWidth(double height) {
        return 20;
      }
    };

    Flex h1 = Flex.hflex().with(eager);
    Flex v = Flex.vflex().with(h1);
    Flex root = Flex.hflex().with(v);

    // the requestLayout() fires while the innermost computeSpans() is on the
    // stack -- must not throw ConcurrentModificationException

    assertThat(root.minWidth(-1)).isEqualTo(10);
    assertThat(root.prefWidth(-1)).isEqualTo(20);
    assertThat(root.minWidth(-1)).isEqualTo(10);
    assertThat(root.prefWidth(-1)).isEqualTo(20);
  }

  @Test
  void shouldHaveUnboundedMaxSizeByDefault() {
    Flex flex = new Flex(Orientation.HORIZONTAL);

    flex.setPadding(new Insets(1, 2, 3, 4));
    flex.setSpacing(7);
    flex.getChildren().addAll(
      new MockResizable(1, 1, 10, 20, 100, 60),
      new MockResizable(1, 1, 10, 20, 50, 80)
    );

    assertThat(flex.maxWidth(-1)).isEqualTo(Double.MAX_VALUE);
    assertThat(flex.maxHeight(-1)).isEqualTo(Double.MAX_VALUE);
  }

  @Test
  void shouldHaveUnboundedMaxSizeByDefaultForVertical() {
    Flex flex = new Flex(Orientation.VERTICAL);

    flex.setPadding(new Insets(1, 2, 3, 4));
    flex.setSpacing(7);
    flex.getChildren().addAll(
      new MockResizable(1, 1, 10, 20, 100, 60),
      new MockResizable(1, 1, 10, 20, 50, 80)
    );

    assertThat(flex.maxWidth(-1)).isEqualTo(Double.MAX_VALUE);
    assertThat(flex.maxHeight(-1)).isEqualTo(Double.MAX_VALUE);
  }

  @Test
  void shouldPreserveUnboundedMaxWhenConfigured() {
    Flex flex = new Flex(Orientation.HORIZONTAL);

    flex.getChildren().add(new MockResizable(1, 1, 10, 20, 100, 60));

    assertThat(flex.maxWidth(-1)).isEqualTo(Double.MAX_VALUE);
    assertThat(flex.maxHeight(-1)).isEqualTo(Double.MAX_VALUE);

    flex.setMaxWidth(Double.MAX_VALUE);
    flex.setMaxHeight(Double.MAX_VALUE);

    assertThat(flex.maxWidth(-1)).isEqualTo(Double.MAX_VALUE);
    assertThat(flex.maxHeight(-1)).isEqualTo(Double.MAX_VALUE);
  }

  @Test
  void shouldUsePreferredSizeWhenConfigured() {
    Flex flex = new Flex(Orientation.HORIZONTAL);

    flex.getChildren().add(new MockResizable(1, 1, 10, 20, 100, 60));
    flex.setMaxWidth(Region.USE_PREF_SIZE);
    flex.setMaxHeight(Region.USE_PREF_SIZE);

    assertThat(flex.maxWidth(-1)).isEqualTo(10);
    assertThat(flex.maxHeight(-1)).isEqualTo(20);
  }

  @Test
  void shouldPropagateUnboundedChildMaxSize() {
    Flex flex = new Flex(Orientation.HORIZONTAL);

    flex.getChildren().add(new MockResizable(1, 1, 10, 20, Double.MAX_VALUE, 60));

    assertThat(flex.maxWidth(-1)).isEqualTo(Double.MAX_VALUE);
    assertThat(flex.maxHeight(-1)).isEqualTo(Double.MAX_VALUE);
  }

  private static void assertMinSizes(Flex node, double w, double h) {
    final double mw;
    final double mh;

    if(node.getContentBias() == Orientation.HORIZONTAL) {
      mw = node.computeMinWidth(-1);
      mh = node.computeMinHeight(mw);
    }
    else if(node.getContentBias() == Orientation.VERTICAL) {
      mh = node.computeMinHeight(-1);
      mw = node.computeMinHeight(mh);
    }
    else {
      mw = node.computeMinWidth(-1);
      mh = node.computeMinHeight(-1);
    }

    if(w != mw || h != mh) {
      fail("expected minimum size (" + w + ", " + h + ") but was: (" + mw + ", " + mh + ")");
    }
  }

  private static void assertPrefSizes(Flex node, double w, double h) {
    final double mw;
    final double mh;

    if(node.getContentBias() == Orientation.HORIZONTAL) {
      mw = node.computePrefWidth(-1);
      mh = node.computePrefHeight(mw);
    }
    else if(node.getContentBias() == Orientation.VERTICAL) {
      mh = node.computePrefHeight(-1);
      mw = node.computePrefHeight(mh);
    }
    else {
      mw = node.computePrefWidth(-1);
      mh = node.computePrefHeight(-1);
    }

    if(w != mw || h != mh) {
      fail("expected preferred size (" + w + ", " + h + ") but was: (" + mw + ", " + mh + ")");
    }
  }

  private static void assertMaxSizes(Flex node, double w, double h) {
    double actualWidth = node.maxWidth(-1);
    double actualHeight = node.maxHeight(-1);

    if(node.getMaxWidth() == Region.USE_COMPUTED_SIZE && node.getMaxHeight() == Region.USE_COMPUTED_SIZE) {
      if(actualWidth != Double.MAX_VALUE || actualHeight != Double.MAX_VALUE) {
        fail("expected unbounded max size but was: (" + actualWidth + ", " + actualHeight + ")");
      }
    }
    else if(w != actualWidth || h != actualHeight) {
      fail("expected max size (" + w + ", " + h + ") but was: (" + actualWidth + ", " + actualHeight + ")");
    }
  }
}
