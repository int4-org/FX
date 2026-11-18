package org.int4.fx.layout.flex;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import org.int4.fx.layout.util.LayoutAssertions;
import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

public class FlexLayoutTest {

  @Test
  void shouldLayoutSingleResizable() {
    MockResizable c1 = new MockResizable(20, 20);
    FlexLayout layout = FlexLayout.horizontal(c1);

    layout.resizeRelocate(0, 0, 20, 20);

    LayoutAssertions.assertBounds(
      """
      +-------------------+
      |         0         |
      |   +-----------+   |
      | 0 | 1: 20x20  | 0 |
      |   +-----------+   |
      |         0         |
      +-------------------+
      """,
      layout
    );

    layout.setFill(false);
    layout.setAlignment(Pos.CENTER_LEFT);
    layout.resizeRelocate(0, 0, 30, 30);

    LayoutAssertions.assertBounds(
      """
      +--------------------+
      |         5          |
      |   +-----------+    |
      | 0 | 1: 20x20  | 10 |
      |   +-----------+    |
      |         5          |
      +--------------------+
      """,
      layout
    );

    // alignment defaults to TOP_LEFT (matching HBox/VBox); verify that explicitly too.

    layout.setAlignment(Pos.TOP_LEFT);
    layout.resizeRelocate(0, 0, 30, 30);

    LayoutAssertions.assertBounds(
      """
      +-----------+--------+
      | 1: 20x20  | 10x20  |
      +-----------+--------+
      |        30x10       |
      +--------------------+
      """,
      layout
    );

    layout.setFill(true);
    layout.setAlignment(Pos.CENTER_LEFT);
    layout.setInsets(new Insets(2, 3, 4, 5));
    layout.resizeRelocate(0, 0, 20, 20);

    LayoutAssertions.assertBounds(
      """
      +-------------------+
      |         2         |
      |   +-----------+   |
      | 5 | 1: 12x14  | 3 |
      |   +-----------+   |
      |         4         |
      +-------------------+
      """,
      layout
    );
  }
}
