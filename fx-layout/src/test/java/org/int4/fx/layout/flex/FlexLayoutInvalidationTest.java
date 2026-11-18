package org.int4.fx.layout.flex;

import java.util.List;

import javafx.scene.layout.Region;

import org.int4.fx.layout.internal.Layoutable;
import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Covers invalidation of cached Tracks in a standalone tree of bare
 * FlexLayout instances (no Flex/Region anywhere in between) -- see
 * StandaloneFlexLayoutTest for the general pattern. Unlike a Flex-in-Flex
 * tree, where each nested Flex is a real scene graph Node and invalidates
 * its own FlexLayout independently via its own requestLayout() override, a
 * bare FlexLayout nested inside another bare FlexLayout is not a Node and has
 * no independent invalidation path of its own -- the outer FlexLayout's own
 * invalidate() is the only thing that could ever reach it.
 */
class FlexLayoutInvalidationTest {

  @Test
  void shouldSeeNestedFlexLayoutChangesAfterInvalidate() {
    Region c1 = new MockResizable(1, 1, 10, 10);
    Region c2 = new MockResizable(1, 1, 20, 10);

    FlexLayout inner = FlexLayout.horizontal(c1, c2);  // pref width = 10 + 20 = 30
    FlexLayout outer = FlexLayout.vertical(inner);     // unaligned: inner is treated as a single leaf

    // populates outer's own Tracks cache, and -- via inner.prefWidth(-1) -- inner's own cache too
    assertThat(outer.computeExtents(-1).prefs()).containsExactly(30.0);

    // inner's content changes -- e.g. the owning Node's real scene graph child
    // fired its own requestLayout(), which is how a standalone owner learns
    // that something in the tree needs to be recomputed
    Region c3 = new MockResizable(1, 1, 100, 10);

    inner.setChildren(List.of(Layoutable.of(c3)));  // pref width now 100

    // what a standalone owner is expected to do before its next resizeRelocate()
    outer.invalidate();

    assertThat(outer.computeExtents(-1).prefs())
      .as("outer must see inner's new content after invalidate(), not inner's own stale cached Tracks")
      .containsExactly(100.0);
  }
}
