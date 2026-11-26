package org.int4.fx.layout.flex;

import javafx.scene.layout.Region;

import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.int4.fx.layout.util.LayoutAssertions.assertBounds;

/*
 * Covers the "standalone FlexLayout" usage pattern described for task #16: a
 * real Node lays out its own real scene graph children (added directly to its
 * own getChildren()) using a tree of bare FlexLayout instances -- no Flex or
 * other Region wrapper anywhere in the tree except the one real owning Node
 * at the very top. This is exactly the "icon + stacked title/subtitle"
 * scenario: icon on the left, title/subtitle stacked to its right, aligned so
 * the icon spans the combined height of the two stacked labels.
 */
public class StandaloneFlexLayoutTest {

  @Test
  void shouldLayoutRealNodeUsingAlignedStandaloneFlexLayoutTree() {
    Region icon = new MockResizable(1, 1, 40, 60);
    Region title = new MockResizable(1, 1, 100, 20);
    Region subtitle = new MockResizable(1, 1, 100, 16);

    FlexLayout textGroup = FlexLayout.vertical(title, subtitle);
    FlexLayout root = FlexLayout.horizontal(icon, textGroup);

    root.aligned();

    StandaloneFlexNode node = new StandaloneFlexNode(root, icon, title, subtitle);

    // measurement first, exactly as a real parent would query this node, before any resizeRelocate/layoutChildren call

    node.resize(node.prefWidth(-1), node.prefHeight(-1));
    node.layout();

    /*
     * icon is a direct leaf, not a FlexLayout -- computeExtents' leaf branch
     * only ever contributes a single value, which mergeParallelMax always
     * folds into track 0 specifically, never any other track. So icon only
     * ever affects row0 (max(icon=60, title=20) = 60), and is placed within
     * row0 only, NOT spanning row1 (subtitle's own 16-tall row) -- leaving
     * column0 unused for that row, exactly like any other track a leaf
     * doesn't contribute to. (Making a leaf deliberately span every row --
     * a rowspan-like feature -- might be worth supporting explicitly one
     * day, but isn't what happens by default.)
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
      node
    );
  }

  @Test
  void shouldAlignParallelTracksAcrossTwoStandaloneColumns() {

    /*
     * The previous test only ever has a single crossed VFlex column (plus a
     * bare leaf spanning its full height) -- it never actually exercises rows
     * aligning ACROSS more than one column, which is the entire point of
     * aligned mode. This one has two independent VFlex "columns", each with
     * its own two rows, so their row heights genuinely have to be merged and
     * shared: row0 = max(title1=20, title2=30) = 30, row1 = max(subtitle1=16,
     * subtitle2=10) = 16.
     */

    Region title1 = new MockResizable(1, 1, 80, 20);
    Region subtitle1 = new MockResizable(1, 1, 80, 16);
    Region title2 = new MockResizable(1, 1, 60, 30);
    Region subtitle2 = new MockResizable(1, 1, 60, 10);

    FlexLayout col1 = FlexLayout.vertical(title1, subtitle1);
    FlexLayout col2 = FlexLayout.vertical(title2, subtitle2);
    FlexLayout root = FlexLayout.horizontal(col1, col2);

    root.aligned();

    StandaloneFlexNode node = new StandaloneFlexNode(root, title1, subtitle1, title2, subtitle2);

    double prefWidth = node.prefWidth(-1);
    double prefHeight = node.prefHeight(-1);

    assertThat(prefWidth).isEqualTo(140);
    assertThat(prefHeight).isEqualTo(46);

    node.resize(prefWidth, prefHeight);
    node.layout();

    // neither title fills its row (no grow set): title1 leaves 10 unused below it in row0, subtitle2 leaves 6 unused below it in row1

    assertBounds(
      """
      +------------+----------+
      | 1: 80x20   |          |
      +------------+ 3: 60x30 |
      |  80x10     |          |
      +------------+----------+
      | 2: 80x16   | 4: 60x10 |
      |            +----------+
      |            |  60x6    |
      +------------+----------+
      """,
      node
    );
  }

  @Test
  void shouldLayoutRealNodeUsingStandaloneFlexLayoutTree() {
    Region icon = new MockResizable(1, 1, 40, 60);
    Region title = new MockResizable(1, 1, 100, 20);
    Region subtitle = new MockResizable(1, 1, 100, 16);

    FlexLayout textGroup = FlexLayout.vertical(title, subtitle);
    FlexLayout root = FlexLayout.horizontal(icon, textGroup);

    StandaloneFlexNode node = new StandaloneFlexNode(root, icon, title, subtitle);

    // measurement first, exactly as a real parent would query this node, before any resizeRelocate/layoutChildren call

    node.resize(node.prefWidth(-1), node.prefHeight(-1));
    node.layout();

    assertBounds(
      """
      +------------+--------------+
      |            | 2: 100x20    |
      |            +--------------+
      | 1: 40x60   | 3: 100x16    |
      |            +--------------+
      |            |              |
      +------------+--------------+
      """,
      node
    );
  }

  @Test
  void shouldAlignFlexAndFlexLayoutAsOppositeOrientationSiblings() {

    /*
     * The Flexible bridge must let a real Flex be treated exactly like a
     * bare FlexLayout when a top-level aligned FlexLayout resolves its own
     * grid: one column here is a real Flex (a Node managing its own real
     * scene-graph children), the other a bare FlexLayout -- both cross the
     * aligned root's orientation the same way, so their rows must still
     * line up with each other, and the real Flex must still get its own
     * layoutChildren() invoked (positioning title1/subtitle1) once the
     * coordinated resize reaches it.
     *
     * Same sizes as shouldAlignParallelTracksAcrossTwoStandaloneColumns --
     * only difference is that column 1 is now a real Flex instead of a bare
     * FlexLayout -- so the resolved geometry should be identical.
     */

    Region title1 = new MockResizable(1, 1, 80, 20);
    Region subtitle1 = new MockResizable(1, 1, 80, 16);
    Region title2 = new MockResizable(1, 1, 60, 30);
    Region subtitle2 = new MockResizable(1, 1, 60, 10);

    Flex flexCol = Flex.vflex().with(title1, subtitle1);              // Node-based column
    FlexLayout flCol = FlexLayout.vertical(title2, subtitle2); // bare column
    FlexLayout root = FlexLayout.horizontal(flexCol, flCol);

    root.aligned(true);

    StandaloneFlexNode node = new StandaloneFlexNode(root, flexCol, title2, subtitle2);

    double prefWidth = node.prefWidth(-1);
    double prefHeight = node.prefHeight(-1);

    assertThat(prefWidth).isEqualTo(140);
    assertThat(prefHeight).isEqualTo(46);

    node.resize(prefWidth, prefHeight);
    node.layout();

    // row0 = max(title1=20, title2=30) = 30, row1 = max(subtitle1=16, subtitle2=10) = 16

    assertBounds(
      """
      +------------+----------+
      | 1: 80x20   |          |
      +------------+ 3: 60x30 |
      |  80x10     |          |
      +------------+----------+
      | 2: 80x16   | 4: 60x10 |
      |            +----------+
      |            |  60x6    |
      +------------+----------+
      """,
      node
    );
  }
}
