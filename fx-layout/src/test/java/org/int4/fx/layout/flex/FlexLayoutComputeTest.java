package org.int4.fx.layout.flex;

import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlexLayoutComputeTest {

  @Test
  void shouldComputeExtentsWithFlatteningBoundary() {
    Region c1 = new MockResizable(1, 1, 40, 20, 200, 100);
    Region c2 = new MockResizable(1, 1, 100, 20, 300, 100);
    Region c3 = new MockResizable(1, 1, 40, 20, 200, 100);
    Region c4 = new MockResizable(1, 1, 100, 20, 300, 100);
    Region c5 = new MockResizable(1, 1, 50, 20, 150, 100);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout h3 = FlexLayout.horizontal(c4, c5);
    FlexLayout h2 = FlexLayout.horizontal(c3, h3);
    FlexLayout v1 = FlexLayout.vertical(h2);
    FlexLayout va = FlexLayout.vertical(h1, v1);

    // +-va--------------------+-----------------------+  spans (vertical sizes):
    // |                       |                       |
    // |          c1           |          c2           | 1/20/100
    // |                       |                       |
    // +-v1--------------------+-----------+-----------+
    // |                       |           |           |
    // |          c3           |   h3:c4   |   h3:c5   | 1/20/MAX
    // |                       |           |           |
    // +-----------------------+-----------+-----------+
    // extents: 1/40/200                2/150/MAX

    va.aligned();

    Tracks extents = va.computeExtents(-1);
    Tracks spans = va.computeSpans(-1);

    /*
     * va sees:
     *  h1 (opposite) -> computeSpans -> [40, 0, 100]
     *  v1 (same) -> recurse -> h2 (opposite) -> computeSpans -> [40, 0, h3_w=150]
     * merged = [max(40,40), max(0,0), max(100,150)] = [40, 0, 150]
     */

    assertThat(extents.prefs()).as("extents").containsExactly(40.0, 0.0, 150.0);

    /*
     * h1 -> computeExtents -> [20], v1 -> computeSpans -> [20]
     * concatenated = [20, 0, 20]
     */

    assertThat(spans.prefs()).as("spans").containsExactly(20.0, 0.0, 20.0);

    /*
     * min: c1=1, c2=1, c3=1, c4=1, c5=1; h3 as leaf minWidth=1+0+1=2
     * column 0: max(c1.min=1, c3.min=1)=1
     * column 1: max(c2.min=1, h3.min=2)=2; gap=max(0,0)=0
     */

    assertThat(extents.mins()).containsExactly(1.0, 0.0, 2.0);
    assertThat(spans.mins()).containsExactly(1.0, 0.0, 1.0);

    // max: h3 as leaf uses maxWidth -> 300 + 150 = 450; col 0=max(200,200)=200, col 1=max(300,450)=450
    assertThat(extents.maxs()).containsExactly(200.0, 0.0, 450.0);
    assertThat(spans.maxs()).containsExactly(100.0, 0.0, 100.0);
  }

  @Test
  void shouldNotFlattenWhenUnaligned() {
    Region c1 = new MockResizable(1, 2, 40, 20, 500, 600);
    Region c2 = new MockResizable(3, 5, 100, 15, 550, 650);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout v1 = FlexLayout.vertical(h1);

    Tracks extents = v1.computeExtents(-1);
    Tracks spans = v1.computeSpans(-1);

    assertThat(extents.prefs()).containsExactly(140.0);  // sum(40, 100)
    assertThat(spans.prefs()).containsExactly(20.0);  // max(15, 20)

    assertThat(extents.mins()).containsExactly(4.0);  // sum(1, 3)
    assertThat(spans.mins()).containsExactly(5.0);    // max(2, 5)

    assertThat(extents.maxs()).containsExactly(1050.0);
    assertThat(spans.maxs()).containsExactly(650.0);
  }

  @Test
  void shouldComputeExtentsWithSpacing() {
    Region c1 = new MockResizable(1, 1, 30, 20);
    Region c2 = new MockResizable(1, 1, 20, 20);
    Region c3 = new MockResizable(1, 1, 20, 20);
    Region c4 = new MockResizable(1, 1, 20, 20);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2).spacing(10);
    FlexLayout h2 = FlexLayout.horizontal(c3, c4).spacing(20);
    FlexLayout va = FlexLayout.vertical(h1, h2);

    va.aligned();

    // columns: mergeParallelMax([30,10,20], [20,20,20]) = [30, max(10,20)=20, 20]
    assertThat(va.computeExtents(-1).prefs())
      .containsExactly(30.0, 20.0, 20.0);

    // rows: h1->[20], h2->[20]; concatenated = [20, 0(spacing), 20]
    assertThat(va.computeSpans(-1).prefs())
      .containsExactly(20.0, 0.0, 20.0);
  }

  @Test
  void shouldComputeExtentsWithHorizontalTopLevel() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 60, 25);
    Region c3 = new MockResizable(1, 1, 80, 15);

    FlexLayout v1 = FlexLayout.vertical(c1, c2);
    FlexLayout ha = FlexLayout.horizontal(v1, c3);

    ha.aligned();

    /*
     * extents (row heights): v1 -> computeSpans -> [20, 0(spacing), 25]; c3 -> [15]
     * mergeParallelMax: [max(20,15)=20, max(0)=0, max(25)=25]
     */

    assertThat(ha.computeExtents(-1).prefs())
      .containsExactly(20.0, 0.0, 25.0);

    /*
     * spans (column widths): v1 -> computeExtents -> [max(40,60)=60]; c3 -> [80]
     * concatenated: [60, 0(spacing), 80]
     */

    assertThat(ha.computeSpans(-1).prefs())
      .containsExactly(60.0, 0.0, 80.0);
  }

  @Test
  void shouldComputeExtentsWithDeepSameOrientationWrappers() {
    Region c1 = new MockResizable(1, 1, 30, 10);
    Region c2 = new MockResizable(1, 1, 50, 10);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout v2 = FlexLayout.vertical(h1);
    FlexLayout v1 = FlexLayout.vertical(v2);
    FlexLayout va = FlexLayout.vertical(v1);

    va.aligned();

    // all VFlex chain is transparent; h1(opposite) provides columns: [30, 0, 50]
    assertThat(va.computeExtents(-1).prefs())
      .containsExactly(30.0, 0.0, 50.0);

    // h1->computeExtents->[10]; concatenated through the VFlex chain -> [10]
    assertThat(va.computeSpans(-1).prefs())
      .containsExactly(10.0);
  }

  @Test
  void shouldComputeExtentsWithOppositeChildInsideUnalignedWrapper() {
    Region c1 = new MockResizable(1, 1, 30, 10);
    Region c2 = new MockResizable(1, 1, 50, 10);
    Region c3 = new MockResizable(1, 1, 70, 10);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout vw = FlexLayout.vertical(h1);       // unaligned wrapper
    FlexLayout h2 = FlexLayout.horizontal(c3);
    FlexLayout va = FlexLayout.vertical(vw, h2);   // aligned root

    va.aligned();

    /*
     * va sees:
     *  vw (same) -> recurse -> h1 (opposite) -> computeSpans -> [30, 0, 50]
     *  h2 (opposite) -> computeSpans -> [70]
     * mergeParallelMax: [max(30,70)=70, max(0)=0, max(50)=50]
     */

    assertThat(va.computeExtents(-1).prefs())
      .containsExactly(70.0, 0.0, 50.0);

    // rows: vw->computeSpans->[10], h2->computeExtents->[10]; concatenated=[10, 0, 10]
    assertThat(va.computeSpans(-1).prefs())
      .containsExactly(10.0, 0.0, 10.0);
  }

  @Test
  void shouldTreatFirstOppositeChildAsLeafForExtents() {
    Region c1 = new MockResizable(1, 1, 30, 10);
    Region c2 = new MockResizable(1, 1, 50, 10);
    Region c3 = new MockResizable(1, 1, 70, 10);

    FlexLayout h3 = FlexLayout.horizontal(c3);
    FlexLayout h2 = FlexLayout.horizontal(h3, c2);
    FlexLayout h1 = FlexLayout.horizontal(c1, h2);
    FlexLayout va = FlexLayout.vertical(h1);

    va.aligned();

    /*
     * va is VFlex, h1 is first opposite child (crossedAxis=true for computeSpans)
     * h1.computeSpans(true):
     *   c1 -> [30]
     *   h2 (same orientation, crossedAxis=true -> falls through -> leaf)
     *     h2 as leaf: prefWidth = 70 + 0 + 50 = 120 -> [120]
     *   mergeSequentialConcat: [30, 0, 120]
     * merged extents: [30, 0, 120]
     */

    assertThat(va.computeExtents(-1).prefs())
      .containsExactly(30, 0, 120);

    // h1->computeExtents->[10]
    assertThat(va.computeSpans(-1).prefs())
      .containsExactly(10.0);
  }

  @Test
  void shouldOnlyCrossOrientationOnce() {

    /*
     * va (V, aligned) -> row (H, the ONE allowed crossing) -> group (V,
     * opposite of row) -> rowInner (H, opposite of group) -> c6, c7
     *                                                     -> c5
     *
     * Every one of group's and rowInner's own children are past the first
     * crossing (row's), so both group and rowInner must be treated as opaque
     * leaves, regardless of their own orientation matching or mismatching
     * whatever encloses them. Under the old (buggy) rule, an opposite-
     * orientation child crossed unconditionally no matter how many times a
     * crossing had already happened, so group and rowInner would each have
     * been decomposed into separate grid tracks too.
     */

    Region c1 = new MockResizable(1, 1, 30, 12);
    Region c6 = new MockResizable(1, 1, 20, 4);
    Region c7 = new MockResizable(1, 1, 25, 6);
    Region c5 = new MockResizable(1, 1, 10, 3);

    FlexLayout rowInner = FlexLayout.horizontal(c6, c7);
    FlexLayout group = FlexLayout.vertical(rowInner, c5);
    FlexLayout row = FlexLayout.horizontal(c1, group);
    FlexLayout va = FlexLayout.vertical(row);

    va.aligned();

    /*
     * columns (va.computeExtents, crossing row once): group is a leaf, so its
     * own prefWidth (rowInner treated as ONE leaf value itself, 20+0+25=45,
     * max'd against c5's 10) is a single column -- rowInner's c6/c7 are never
     * split into separate columns.
     * Under the old rule this would instead have been [30, 0, 20, 0, 25],
     * because group and then rowInner would each keep crossing.
     */

    assertThat(va.computeExtents(-1).prefs())
      .containsExactly(30.0, 0.0, 45.0);

    /*
     * rows (va.computeSpans, crossing row once): group is a leaf here too, so
     * its own prefHeight (rowInner's height as ONE leaf value, max(4,6)=6,
     * plus c5's 3) is a single value merged against c1's 12.
     * Under the old rule this would instead have been [12, 0, 3], because
     * group would keep crossing into rowInner and c5 separately.
     */

    assertThat(va.computeSpans(-1).prefs())
      .containsExactly(12.0);
  }

  @Test
  void unalignedRootShouldTreatOnlyChildAsLeaf() {
    Region c1 = new MockResizable(1, 1, 30, 10);
    Region c2 = new MockResizable(1, 1, 50, 10);
    Region c3 = new MockResizable(1, 1, 70, 10);

    FlexLayout h3 = FlexLayout.horizontal(c3);
    FlexLayout h2 = FlexLayout.horizontal(h3, c2);
    FlexLayout h1 = FlexLayout.horizontal(c1, h2);
    FlexLayout root = FlexLayout.vertical(h1);

    assertThat(root.computeExtents(-1).prefs())
      .containsExactly(30 + 50 + 70);

    assertThat(root.computeSpans(-1).prefs())
      .containsExactly(10.0);
  }

  /*
   * Priority.ALWAYS grows a child along the main axis of its immediate
   * container, exactly like Priority.ALWAYS on an HBox/VBox child (never
   * the cross axis -- there, only alignment and fill apply). The tests
   * below lock in that a child's own grow flag surfaces at the track
   * position its *direct* parent occupies in the coordinated grid, not at
   * the position of whichever grid axis its parent happens to expose it as.
   */

  @Test
  void shouldMarkColumnGrowableFromDirectRowLeafOnly() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);

    FlexLayout.setGrow(c1, Priority.ALWAYS);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout h2 = FlexLayout.horizontal(c3);
    FlexLayout va = FlexLayout.vertical(h1, h2);

    va.aligned();

    // c1 grows along h1's own main axis (horizontal) -> column 0 growable
    assertThat(va.computeExtents(-1).grow())
      .containsExactly(1.0, 0.0, 0.0);

    // va's own main axis (rows) is unaffected: nothing directly under va is grow-flagged
    assertThat(va.computeSpans(-1).grow())
      .containsExactly(0.0, 0.0, 0.0);
  }

  @Test
  void shouldMarkRowGrowableFromDirectRowOnly() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout h2 = FlexLayout.horizontal(c3);
    FlexLayout va = FlexLayout.vertical(h1, h2);

    va.aligned();

    FlexLayout.setGrow(h1, Priority.ALWAYS);

    // h1 grows along va's own main axis (vertical) -> row 0 growable
    assertThat(va.computeSpans(-1).grow())
      .containsExactly(1.0, 0.0, 0.0);

    // columns are unaffected by a row's own grow flag
    assertThat(va.computeExtents(-1).grow())
      .containsExactly(0.0, 0.0, 0.0);
  }

  @Test
  void shouldMarkColumnGrowableFromNestedGroupWithinRow() {
    Region c1 = new MockResizable(1, 1, 40, 20);
    Region c2 = new MockResizable(1, 1, 100, 20);
    Region c3 = new MockResizable(1, 1, 40, 20);

    FlexLayout group = FlexLayout.vertical(c2, c3);  // orientation opposite of row, same as va
    FlexLayout row = FlexLayout.horizontal(c1, group);
    FlexLayout va = FlexLayout.vertical(row);

    va.aligned();

    FlexLayout.setGrow(group, Priority.ALWAYS);

    /*
     * group grows along row's own main axis (horizontal) -> its column slot
     * is growable, regardless of anything happening inside group itself
     */

    assertThat(va.computeExtents(-1).grow())
      .containsExactly(0.0, 0.0, 1.0);
  }

  @Test
  void shouldPropagateColumnGrowThroughTransparentWrappers() {
    Region c1 = new MockResizable(1, 1, 30, 10);
    Region c2 = new MockResizable(1, 1, 50, 10);

    FlexLayout.setGrow(c2, Priority.ALWAYS);

    FlexLayout h1 = FlexLayout.horizontal(c1, c2);
    FlexLayout v2 = FlexLayout.vertical(h1);
    FlexLayout v1 = FlexLayout.vertical(v2);
    FlexLayout va = FlexLayout.vertical(v1);

    va.aligned();

    /*
     * c2 grows along h1's own main axis; propagated transparently through
     * the v1/v2 same-orientation wrapper chain up to va's columns
     */

    assertThat(va.computeExtents(-1).grow())
      .containsExactly(0.0, 0.0, 1.0);
  }
}
