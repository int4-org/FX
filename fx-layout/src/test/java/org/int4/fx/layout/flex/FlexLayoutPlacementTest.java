package org.int4.fx.layout.flex;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

import static org.int4.fx.layout.util.LayoutAssertions.assertBounds;

public class FlexLayoutPlacementTest {

  @Test
  void shouldPlaceFlattenedWrapperBoxForHorizontalAlignedRoot() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 40, 20);
    Region c3 = new MockResizable(1, 1, 50, 20);
    Region c4 = new MockResizable(1, 1, 50, 20);

    FlexLayout column1 = FlexLayout.vertical(c1, c2);
    FlexLayout wrapper = FlexLayout.horizontal(column1);  // same orientation as ha -> transparent wrapper
    FlexLayout column2 = FlexLayout.vertical(c3, c4);     // direct opposite-orientation child, no wrapper

    FlexLayout ha = FlexLayout.horizontal(column2, wrapper);

    ha.aligned();
    ha.resizeRelocate(0, 0, 90, 40);

    /*
     * placeBox's HORIZONTAL branch (this.orientation == HORIZONTAL) only runs
     * when a same-orientation wrapper is flattened directly under a
     * HORIZONTAL-oriented ancestor -- every other aligned test roots the grid
     * on a vertical Flex, so this is the only tree shape that reaches it.
     * wrapper transparently wraps column1 and must still get its own box,
     * spanning exactly what column1 consumed: the second column slot (after
     * column2's 50-wide first slot), at the full row height. c1..c4 are
     * asserted individually here; assertBounds derives column1/wrapper's and
     * column2's own boxes automatically as the union of their children, and
     * verifies those too, without needing a label of their own.
     */

    assertBounds(
      """
      +--------------+------------+
      | 1: 50x20     | 3: 40x20   |
      +--------------+------------+
      | 2: 50x20     | 4: 40x20   |
      +--------------+------------+
      """,
      ha
    );
  }

  @Test
  void shouldOffsetColumnsWhenAlignedRootAlignmentIsCenter() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 40, 20);

    FlexLayout row = FlexLayout.horizontal(c1, c2);
    FlexLayout va = FlexLayout.vertical(row);

    va.aligned();
    va.setAlignment(Pos.TOP_CENTER);
    va.resizeRelocate(0, 0, 100, 20);  // content only needs 80 -> 20 spare, centered = 10 either side

    /*
     * switch(alignment.getHpos())'s CENTER arm in layoutAligned is only
     * reached when the aligned root's own alignment is explicitly set away
     * from the default LEFT -- no existing aligned test does this. c1/c2 are
     * asserted individually here; assertBounds derives row's own offset box
     * automatically as their union and verifies it too.
     */

    assertBounds(
      """
      +-------+-----------+-----------+-------+
      | 10x20 | 1: 40x20  | 2: 40x20  | 10x20 |
      +-------+-----------+-----------+-------+
      """,
      va
    );
  }

  @Test
  void shouldOffsetColumnsWhenAlignedRootAlignmentIsRight() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 40, 20);

    FlexLayout row = FlexLayout.horizontal(c1, c2);
    FlexLayout va = FlexLayout.vertical(row);

    va.aligned();
    va.setAlignment(Pos.TOP_RIGHT);
    va.resizeRelocate(0, 0, 100, 20);  // content only needs 80 -> 20 spare, all of it on the left

    // switch(alignment.getHpos())'s RIGHT arm, same reasoning as the CENTER case above.

    assertBounds(
      """
      +-------+-----------+-----------+
      | 20x20 | 1: 40x20  | 2: 40x20  |
      +-------+-----------+-----------+
      """,
      va
    );
  }

  @Test
  void shouldNotGrowAlignedColumnBeyondMaxSize() {
    Region c1 = new MockResizable(1, 1, 40, 20, 60, 5000);  // column capped at a max width of 60
    Region c2 = new MockResizable(1, 1, 40, 20);

    FlexLayout.setGrow(c1, Priority.ALWAYS);

    FlexLayout row = FlexLayout.horizontal(c1, c2);
    FlexLayout va = FlexLayout.vertical(row);

    va.aligned();
    va.resizeRelocate(0, 0, 200, 20);

    /*
     * c1's own column wants to grow to fill all available width, but must
     * stop at its max of 60 -- same rule as an ungrown leaf, just reached via
     * the aligned grid's column-grow path instead of the plain layout path.
     */

    assertBounds(
      """
      +------------+----------+
      | 1: 60x20   | 2: 40x20 |
      +------------+----------+
      """,
      row
    );
  }

  @Test
  void shouldNotGrowAlignedRowBeyondMaxSize() {
    Region c1 = new MockResizable(1, 1, 40, 20, 60, 30);  // caps the row's own height at 30

    FlexLayout row = FlexLayout.horizontal(c1);
    FlexLayout va = FlexLayout.vertical(row);

    va.aligned();
    FlexLayout.setGrow(row, Priority.ALWAYS);
    va.resizeRelocate(0, 0, 40, 100);

    // row wants to grow to fill all available height, but must stop at its own max of 30

    assertBounds(
      """
      +----------+
      | 1: 40x30 |
      +----------+
      | 40x70    |
      +----------+
      """,
      va
    );
  }

  @Test
  void shouldConfineLeafToOwnRowWhenCrossedSiblingSpansMultipleRows() {
    Region icon = new MockResizable(1, 1, 40, 60);
    Region title = new MockResizable(1, 1, 100, 20);
    Region subtitle = new MockResizable(1, 1, 100, 16);

    FlexLayout textGroup = FlexLayout.vertical(title, subtitle);
    FlexLayout root = FlexLayout.horizontal(icon, textGroup);

    root.aligned();
    root.resizeRelocate(0, 0, 140, 76);

    /*
     * icon is a direct leaf, not a FlexLayout -- computeExtents' leaf branch
     * only ever contributes a single value, which mergeParallelMax always
     * folds into track 0 specifically. So icon must only affect and occupy
     * row0 (max(icon=60, title=20) = 60), not span into row1 (subtitle's own
     * 16-tall row) even though its own height (60) covers both rows combined.
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
      root
    );
  }

  @Test
  void shouldGrowNestedFlexLayoutInPlainUnalignedLayout() {

    /*
     * Priority.ALWAYS grow can be set on a nested FlexLayout the same way as
     * on a plain leaf Node: FlexLayout.setGrow/getGrow work off Layoutable,
     * and a FlexLayout is itself a Layoutable, so it can be the growable
     * child of an ordinary (unaligned) plain layoutChildren() pass -- this
     * doesn't require aligned mode at all, unlike shouldNotGrowAlignedRow/
     * ColumnBeyondMaxSize above, which cover the aligned grid's own grow path.
     */

    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 20, 20);
    Region c3 = new MockResizable(1, 1, 30, 20);

    FlexLayout group = FlexLayout.horizontal(c2, c3);  // pref width = 20 + 30 = 50

    FlexLayout.setGrow(group, Priority.ALWAYS);

    FlexLayout row = FlexLayout.horizontal(c1, group);

    row.resizeRelocate(0, 0, 150, 20);  // pref total = 40 + 50 = 90 -> 60 extra, all absorbed by group

    /*
     * group grows from its own pref width (50) to 110 (50 + 60 spare); c2/c3
     * have no grow of their own within group, so they stay at their own
     * preferred widths and group's newly grown space ends up as unused dead
     * space after c3 -- exactly like a plain leaf would leave unused space
     * when nothing inside it is grow-flagged.
     */

    assertBounds(
      """
      +------------+------------+------------+----------------------+
      | 1: 40x20   | 2: 20x20   | 3: 30x20   |     [2,3]: 60x20     |
      +------------+------------+------------+----------------------+
      """,
      row
    );
  }

  @Test
  void shouldVerifyPaddedIntermediateContainerViaAutoDerivedMarginKey() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 40, 20);

    FlexLayout row = FlexLayout.vertical(c1, c2);

    row.setInsets(new Insets(1, 3, 4, 2));  // top, right, bottom, left

    FlexLayout va = FlexLayout.horizontal(row);

    va.resizeRelocate(0, 0, 45, 45);

    /*
     * row has its own padding, so assertBounds can no longer derive its
     * bounds as the plain union of c1/c2 -- AsciiArtParser names the margin
     * drawn around c1/c2 "M[1,2]" (derived from what's nested inside it),
     * with its four numbers read in scan order as top, left, right, bottom
     * regardless of where they're actually drawn in the frame. assertBounds
     * independently builds that same "M[1,2]" key from c1/c2's own resolved
     * keys and looks it up, using it as row's expected bounds.
     */

    assertBounds(
      """
      +--------------------------+
      |            1             |
      |   +------------------+   |
      |   |  1: 40x20        |   |
      | 2 +------------------+ 3 |
      |   |  2: 40x20        |   |
      |   +------------------+   |
      |            4             |
      +--------------------------+
      """,
      va
    );
  }
}
