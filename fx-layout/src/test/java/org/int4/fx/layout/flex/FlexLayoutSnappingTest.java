package org.int4.fx.layout.flex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Orientation;
import javafx.scene.layout.Region;

import org.int4.fx.layout.internal.Layoutable;
import org.int4.fx.layout.util.MockResizable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlexLayoutSnappingTest {

  private static RenderScaleContext scaled(double scale) {
    return new RenderScaleContext(scale, scale);
  }

  private static FlexLayout horizontal(RenderScaleContext context, double spacing, Region... children) {
    FlexLayout layout = new FlexLayout();

    layout.setOrientation(Orientation.HORIZONTAL);
    layout.setSpacing(spacing);
    layout.setChildren(Arrays.stream(children).map(Layoutable::of).toList());
    layout.setRenderScaleContext(context);

    return layout;
  }

  /*
   * computeSpans() already snaps its own gap entries exactly once, via
   * snapSpace(spacing) (see FlexLayout.computeSpans). Summing tracks and
   * gaps together still deviates from the true snapped value by some tiny
   * float error, even though every summand was itself already snapped --
   * that is exactly what totalSpan()'s final snap() call exists to correct,
   * right before the value leaves the system via prefWidth()/minWidth().
   * <p>
   * The expected total below is computed independently (not by replaying
   * production's own reduction order) and then run through that same final
   * snap. A differently-grouped sum (e.g. child + child + gap instead of
   * production's (child + gap) + child) can legitimately differ by a matter
   * of femtometers before snapping -- but nowhere near a rounding boundary,
   * so the shared final snap collapses both to the identical grid point.
   * There's therefore no need to mirror production's exact operation
   * sequence here: any REAL discrepancy -- like the old computeSpans2()
   * re-snapping every entry with snapSize before summing -- shifts the
   * result by close to a whole device pixel, which survives the snap and
   * still shows up as an exact mismatch.
   */

  @Test
  void shouldReportPrefWidthConsistentWithSingleGapSnapAtFractionalScale() {
    double scale = 1.5;
    Snapper snapper = Snapper.createSnapper(scaled(scale));
    List<Double> mismatches = new ArrayList<>();

    for(int i = 0; i <= 200; i++) {
      double spacing = i * 0.1;

      Region c1 = new MockResizable(1, 1, 20, 10);
      Region c2 = new MockResizable(1, 1, 20, 10);

      FlexLayout h = horizontal(scaled(scale), spacing, c1, c2);

      double expectedGap = snapper.snapSpaceX(spacing);
      double expected = snapper.snapSpaceX(20.0 + 20.0 + expectedGap);
      double actual = h.prefWidth(-1);

      if(actual != expected) {
        mismatches.add(spacing);
      }
    }

    assertThat(mismatches).as("spacing values where prefWidth() disagreed with a single gap snap").isEmpty();
  }

  /*
   * Same shape of issue, but scanning content sizes instead of spacing: each
   * child's pref width is snapped exactly once inside computeSpans() (via
   * computeSpan()'s own snapSize call). Looking for a pref width where
   * totalSpan() disagrees with snapping the sum of the two independently
   * (and already correctly) snapped child widths.
   */

  @Test
  void shouldReportPrefWidthConsistentWithPerChildSnapAtFractionalScale() {
    double scale = 1.5;
    Snapper snapper = Snapper.createSnapper(scaled(scale));
    List<Double> mismatches = new ArrayList<>();

    for(int i = 1; i <= 300; i++) {
      double prefWidth = i * 0.1;

      Region c1 = new MockResizable(0, 1, prefWidth, 10);
      Region c2 = new MockResizable(0, 1, prefWidth, 10);

      FlexLayout h = horizontal(scaled(scale), 0, c1, c2);

      double expectedChild = snapper.snapSizeX(prefWidth);
      double expected = snapper.snapSpaceX(expectedChild + expectedChild);
      double actual = h.prefWidth(-1);

      if(actual != expected) {
        mismatches.add(prefWidth);
      }
    }

    assertThat(mismatches).as("pref widths where prefWidth() disagreed with a single per-child snap").isEmpty();
  }
}
