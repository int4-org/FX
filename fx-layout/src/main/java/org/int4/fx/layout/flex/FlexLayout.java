package org.int4.fx.layout.flex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;

import org.int4.fx.layout.internal.ConstraintKey;
import org.int4.fx.layout.internal.Layoutable;

/**
 * Lays out its children in a single row or column along its
 * {@link #getOrientation() orientation}. Unlike most layout panes, a
 * {@code FlexLayout}'s orientation is not fixed and can be changed at any
 * time, and it optionally supports coordinating its layout with nested
 * {@code FlexLayout} descendants -- see "Aligned Mode" below.
 * <p>
 * If this {@code FlexLayout} has insets set, its content is laid out within
 * them.
 * <pre>{@code
 *     FlexLayout row = FlexLayout.horizontal(new Label("Name:"), new TextField());
 *     row.setSpacing(8);
 * }</pre>
 * {@code FlexLayout} resizes children (if resizable) to their preferred size
 * along its own orientation, and uses its fill property to
 * determine whether to resize them to fill its cross axis or keep them at
 * their preferred cross-axis size ({@code fill} defaults to {@code true}).
 * The alignment of the content is controlled by the alignment property,
 * which defaults to {@code Pos.TOP_LEFT}.
 * <p>
 * If a {@code FlexLayout} is resized larger than its preferred size along its
 * orientation, by default it keeps children at their preferred size, leaving
 * the extra space unused. An application that wants one or more children to
 * be allocated that extra space may set a {@link Priority#ALWAYS grow}
 * constraint on the child -- see "Optional Layout Constraints" below.
 *
 * <h2>Resizable Range</h2>
 * <p>
 * A {@code FlexLayout}'s own resizable range is computed from its content:
 * <table border="1">
 * <caption>FlexLayout Resize Table</caption>
 * <tr><td></td><th scope="col">along orientation</th><th scope="col">across orientation</th></tr>
 * <tr><th scope="row">minimum</th>
 * <td>leading/trailing insets plus the sum of each child's min size along that axis, plus spacing between children</td>
 * <td>leading/trailing insets plus the largest of the children's min sizes across that axis</td></tr>
 * <tr><th scope="row">preferred</th>
 * <td>leading/trailing insets plus the sum of each child's pref size along that axis, plus spacing between children</td>
 * <td>leading/trailing insets plus the largest of the children's pref sizes across that axis</td></tr>
 * <tr><th scope="row">maximum</th>
 * <td>leading/trailing insets plus the sum of each child's max size along that axis, plus spacing between children</td>
 * <td>leading/trailing insets plus the largest of the children's max sizes across that axis</td></tr>
 * </table>
 * <p>
 * "Along orientation" means width when {@link Orientation#HORIZONTAL}, height
 * when {@link Orientation#VERTICAL}; "across orientation" is the other one.
 * A {@code FlexLayout}'s maximum size is computed from its content. A child
 * with an unbounded maximum size makes the corresponding result unbounded.
 *
 * <h2>Optional Layout Constraints</h2>
 * <p>
 * An application may set constraints on individual children to customize a
 * {@code FlexLayout}'s layout, using the static methods below.
 * <table border="1">
 * <caption>FlexLayout Constraint Table</caption>
 * <tr><th scope="col">Constraint</th><th scope="col">Type</th><th scope="col">Description</th></tr>
 * <tr><th scope="row">grow</th><td>{@link Priority}</td>
 * <td>Whether the child should grow along this {@code FlexLayout}'s orientation when extra space is available; only {@link Priority#ALWAYS} has any effect.</td></tr>
 * <tr><th scope="row">margin</th><td>{@link Insets}</td><td>Margin space around the outside of the child.</td></tr>
 * </table>
 *
 * <h2>Aligned Mode</h2>
 * <p>
 * Setting {@link #aligned(boolean) aligned} makes this {@code FlexLayout}
 * coordinate the layout of its nested {@code FlexLayout} descendants so their
 * cells line up across siblings: an aligned, vertical {@code FlexLayout}
 * containing horizontal {@code FlexLayout} children lines up each row's
 * columns with every other row's columns (or, mirrored, an aligned
 * horizontal {@code FlexLayout} with vertical children lines up each
 * column's rows). In the notation below, {@code v[...]} is a vertical
 * {@code FlexLayout} and {@code h[...]} a horizontal one, each containing the
 * listed children:
 * <pre>{@code
 *     v[h[A, B], h[C, D]]     // unaligned: two independent rows -- B does not line up with D
 *     av[h[A, B], h[C, D]]    // aligned: B lines up with D
 * }</pre>
 * Only the first crossing from the aligned root's own orientation into the
 * opposite orientation is treated as forming aligned rows/columns; anything
 * nested inside one of those cells -- regardless of its own orientation -- is
 * an opaque leaf and manages its own layout independently, exactly as it
 * would outside an aligned tree.
 * <p>
 * A {@code FlexLayout} nested directly under the aligned root with the SAME
 * orientation as the root is instead flattened transparently -- its children
 * are treated as if they were direct children of the root, without forming a
 * row/column of their own -- letting an application group several
 * rows/columns together (e.g. to give them a shared border, background, or
 * visibility toggle) without disturbing the alignment they contribute to:
 * <pre>{@code
 *     av[v[h[A, B], h[C, D]], h[E, F]]   // v[...] flattens: still 2 rows of 2 columns each, all aligned
 * }</pre>
 */
class FlexLayout extends AbstractChildLayout {
  private static final ConstraintKey<Priority> CHILD_GROW = ConstraintKey.of(Priority.class);
  private static final ConstraintKey<Insets> CHILD_MARGIN = ConstraintKey.of(Insets.class);

  public static FlexLayout horizontal(Object... children) {
    return new FlexLayout(Orientation.HORIZONTAL, children);
  }

  public static FlexLayout vertical(Object... children) {
    return new FlexLayout(Orientation.VERTICAL, children);
  }

  public static Priority getGrow(Layoutable layoutable) {
    return layoutable.getConstraint(CHILD_GROW, Priority.NEVER);
  }

  public static void setGrow(Layoutable layoutable, Priority priority) {
    layoutable.setConstraint(CHILD_GROW, priority);
  }

  public static Priority getGrow(Node layoutable) {
    return layoutable.hasProperties() ? (Priority)layoutable.getProperties().getOrDefault(CHILD_GROW, Priority.NEVER) : Priority.NEVER;
  }

  public static void setGrow(Node layoutable, Priority priority) {
    layoutable.getProperties().put(CHILD_GROW, priority);
  }

  public static Insets getMargin(Layoutable layoutable) {
    return layoutable.getConstraint(CHILD_MARGIN, Insets.EMPTY);
  }

  private Orientation orientation;  // TODO remove
  private LayoutArea layoutArea;
  private Pos alignment = Pos.TOP_LEFT;
  private boolean aligned;
  private boolean fill = true;
  private double spacing;
  private double x, y, w, h;
  private Orientation bias;
  private boolean biasDirty = true;
  private Insets unsnappedInsets = Insets.EMPTY;
  private Insets insets = Insets.EMPTY;  // pre-snapped insets
  private List<Layoutable> children = List.of();

  public FlexLayout() {
    updateSpanInsets();  // TODO make this nicer to set layoutArea
  }

  public FlexLayout(Orientation orientation, Object... children) {
    this.orientation = orientation;
    this.children = Arrays.stream(children)
      .map(c -> switch(c) {
        case Node n -> Layoutable.of(n);
        case Layoutable l -> l;
        default -> throw new IllegalArgumentException("unsupported child type: " + c);
      })
      .toList();

    updateSpanInsets();  // TODO make this nicer to set layoutArea
  }

  /*
   * Fluent methods:
   */

  public FlexLayout aligned() {
    return aligned(true);
  }

  public FlexLayout aligned(boolean aligned) {
    this.aligned = aligned;

    return this;
  }

  public FlexLayout fill() {
    setFill(true);

    return this;
  }

  public FlexLayout spacing(double spacing) {
    setSpacing(spacing);

    return this;
  }

  /*
   * Getter/Setters:
   */

  public void setFill(boolean fill) {
    this.fill = fill;
  }

  public void setSpacing(double spacing) {
    this.spacing = spacing;
  }

  public FlexLayout setAlignment(Pos alignment) {
    this.alignment = alignment;

    return this;
  }

  @Override
  public Bounds getLayoutBounds() {
    return new BoundingBox(x, y, w, h);
  }

  @Override
  public void resizeRelocate(double x, double y, double w, double h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;

    layoutChildren();
  }

  @Override
  public void relocate(double x, double y) {
    this.x = x;
    this.y = y;

    // TODO should also layout I think, need test to confirm
  }

  @Override
  public List<Layoutable> getChildrenUnmodifiable() {
    return Collections.unmodifiableList(children);
  }

  public void setChildren(List<Layoutable> children) {
    this.children = Objects.requireNonNull(children, "children");
  }

  public void setInsets(Insets insets) {
    this.unsnappedInsets = insets == null ? Insets.EMPTY : insets;

    updateSpanInsets();
  }

  @Override
  protected void onSnapToPixelChanged() {
    updateSpanInsets();
  }

  @Override
  protected void onRenderScaleContextChanged() {
    updateSpanInsets();
  }

  private void updateSpanInsets() {
    this.insets = new Insets(
      snapSpaceY(unsnappedInsets.getTop()),
      snapSpaceX(unsnappedInsets.getRight()),
      snapSpaceY(unsnappedInsets.getBottom()),
      snapSpaceX(unsnappedInsets.getLeft())
    );

    this.layoutArea = orientation == Orientation.HORIZONTAL
      ? new HorizontalLayoutArea(getRenderScaleContext(), isSnapToPixel())
      : new VerticalLayoutArea(getRenderScaleContext(), isSnapToPixel());
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;

    updateSpanInsets();  // TODO overkill, but works to update layout area
  }

  /*
   * A bare FlexLayout nested inside this one (i.e. not backed by a real
   * Flex/Node) has no independent invalidation path of its own -- it never
   * gets its own requestLayout() call, so nothing but this cascade can ever
   * clear its cached Tracks. A Flex-backed child needs no such cascade: it
   * is a real scene graph Node and already invalidates its own FlexLayout
   * independently, via its own requestLayout() override. Since a bare
   * FlexLayout is never wrapped in a NodeLayoutable (it isn't a Node), a
   * direct instanceof check reaches exactly the children that need it,
   * without forcing a Flex-backed child through asFlexLayout()'s sync.
   */
  public void invalidate() {
    biasDirty = true;
    bias = null;
    baselineOffset = Double.NaN;
    extentsCache.clear();
    spansCache.clear();

    for(Layoutable child : children) {
      if(child instanceof FlexLayout fl) {
        fl.invalidate();
      }
    }
  }

  private double baselineOffset = Double.NaN;

  private final Map<Double, Tracks> extentsCache = new HashMap<>();
  private final Map<Double, Tracks> spansCache = new HashMap<>();

  @Override
  public double getBaselineOffset() {
    if(orientation == Orientation.VERTICAL) {
      return Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
    }

    if(children.isEmpty()) {
      return Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
    }

    if(Double.isNaN(baselineOffset)) {
      VPos vpos = alignment.getVpos();

      if(vpos == VPos.BASELINE) {
        double max = 0;
        boolean sameAsHeight = false;

        for(int i = 0, sz = children.size(); i < sz; ++i) {
          Layoutable child = children.get(i);
          double offset = child.getBaselineOffset();

          if(offset == Node.BASELINE_OFFSET_SAME_AS_HEIGHT) {
            sameAsHeight = true;
            break;
          }

          /*
           * A child's baseline offset is just as untrustworthy as its
           * min/pref/max size -- nothing guarantees it arrives pre-snapped --
           * so it must be snapped before use here, exactly like a child's
           * size is snapped inside computeSpan().
           */

          offset = layoutArea.snapSpace(offset);

          Insets margin = Insets.EMPTY; // TODO support margin: getMargin(child);
          double top = margin != null ? margin.getTop() : 0;

          max = Math.max(max, top + child.getLayoutBounds().getMinY() + offset);
        }

        /*
         * max + insets.getTop() combines several already-snapped sources
         * (each child's own snapped offset, this node's own pre-snapped
         * insets), so needs exactly one final snap before this value leaves
         * the system via this public method -- unless a child reported
         * SAME_AS_HEIGHT, which must propagate untouched.
         */

        baselineOffset = sameAsHeight ? Node.BASELINE_OFFSET_SAME_AS_HEIGHT : layoutArea.snap(max + insets.getTop());
      }
      else {
        baselineOffset = Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
      }
    }

    return baselineOffset;
  }

  @Override
  public double minWidth(double dependentHeight) {
    return orientation == Orientation.HORIZONTAL
      ? totalSpan(SizeQuery.MIN, dependentHeight)
      : totalExtent(SizeQuery.MIN, dependentHeight);
  }

  @Override
  public double prefWidth(double dependentHeight) {
    return orientation == Orientation.HORIZONTAL
      ? totalSpan(SizeQuery.PREF, dependentHeight)
      : totalExtent(SizeQuery.PREF, dependentHeight);
  }

  @Override
  public double maxWidth(double dependentHeight) {
    return orientation == Orientation.HORIZONTAL
      ? totalSpan(SizeQuery.MAX, dependentHeight)
      : totalExtent(SizeQuery.MAX, dependentHeight);
  }

  @Override
  public double minHeight(double dependentWidth) {
    return orientation == Orientation.VERTICAL
      ? totalSpan(SizeQuery.MIN, dependentWidth)
      : totalExtent(SizeQuery.MIN, dependentWidth);
  }

  @Override
  public double prefHeight(double dependentWidth) {
    return orientation == Orientation.VERTICAL
      ? totalSpan(SizeQuery.PREF, dependentWidth)
      : totalExtent(SizeQuery.PREF, dependentWidth);
  }

  @Override
  public double maxHeight(double dependentWidth) {
    return orientation == Orientation.VERTICAL
      ? totalSpan(SizeQuery.MAX, dependentWidth)
      : totalExtent(SizeQuery.MAX, dependentWidth);
  }

  @Override
  public boolean isResizable() {
    return true;
  }

  /*
   * Given: an aligned VFlex it will:
   * - call computeSpans on a HFlex child
   * - call computeExtents on a VFlex child
   * - call prefWidth on anything else
   *
   * Given an unaligned VFlex it will:
   * - when computeSpans is called:
   *   - call prefHeight on each child and return those as an array
   * - when computeExtents is called:
   *   - call computeSpans on each HFlex child
   *   - call computeExtents on each VFlex child
   *   - call prefHeight on anything else
   *   -> if any of these return arrays with multiple elements,
   *      they are merged, and a simple VFlex that normally has only one
   *      extent will have multiple
   *
   */

  /**
   * Computes the cross-axis thickness of each layout track, alternating with
   * the spacing between tracks.
   * <p>
   * For a horizontal layout this returns row heights and their vertical gaps;
   * for a vertical layout this returns column widths and their horizontal gaps.
   * The returned array follows the structure:
   *
   * <pre>
   * [extent, gap, extent, gap, extent, ... , extent]
   * </pre>
   * <p>
   * When multiple children occupy the same track, the track thickness is
   * determined by taking the maximum extent value among the children in that track.
   *
   * @param availableAreaSpan the available span along the main axis, used when {@link Layoutable}s have a content bias, or {@code -1} if unconstrained
   * @return the resolved cross-axis tracks (minimum, preferred and maximum thickness) and which of them are growable, never {@code null}
   */
  Tracks computeExtents(double availableAreaSpan) {
    Tracks extents = computeExtents(availableAreaSpan, false, aligned);

    extentsCache.put(availableAreaSpan, extents);

    return extents;
  }

  private Tracks computeExtents(double availableAreaSpan, boolean crossedAxis, boolean rootAligned) {
    List<double[]> minArrays = new ArrayList<>();
    List<double[]> prefArrays = new ArrayList<>();
    List<double[]> maxArrays = new ArrayList<>();
    List<double[]> growArrays = new ArrayList<>();

    for(Layoutable child : children) {

      /*
       * Same-orientation wrappers flatten transparently, and a single
       * opposite-orientation crossing turns their children into grid rows/
       * columns -- but only until the FIRST crossing. Once crossedAxis is
       * true we are already inside a row's cells, and everything there is an
       * opaque leaf, regardless of its own orientation: a nested Flex of
       * either orientation is not traversed any further.
       */

      if(!crossedAxis && rootAligned && extractLayout(child) instanceof FlexLayout fl) {
        if(fl.getOrientation() == orientation) {
          Tracks tracks = fl.computeExtents(availableAreaSpan, false, rootAligned);

          minArrays.add(tracks.mins());
          prefArrays.add(tracks.prefs());
          maxArrays.add(tracks.maxs());
          growArrays.add(tracks.grow());
          continue;
        }

        /*
         * Crossing here means: fl's own main axis becomes tracks in OUR
         * cross axis. fl's grow flag (if any) belongs to whichever
         * container fl is a *direct* child of on the main axis it grows
         * along -- that is resolved in computeSpans' own cross-branch
         * below, never here.
         */

        Tracks tracks = fl.computeSpans(availableAreaSpan, true, rootAligned);

        minArrays.add(tracks.mins());
        prefArrays.add(tracks.prefs());
        maxArrays.add(tracks.maxs());
        growArrays.add(tracks.grow());
        continue;
      }

      /*
       * Leaf node or isolation boundary: treat as a single extent. The
       * provided span is the max span available, and fill determines if the
       * child should prefer using the available size if possible, or use
       * its preferred span.
       */

      Insets margin = getMargin(child);

      minArrays.add(new double[] {layoutArea.cross().computeSpan(SizeQuery.MIN, child, -1, margin, availableAreaSpan, fill)});
      prefArrays.add(new double[] {layoutArea.cross().computeSpan(SizeQuery.PREF, child, -1, margin, availableAreaSpan, fill)});
      maxArrays.add(new double[] {layoutArea.cross().computeSpan(SizeQuery.MAX, child, -1, margin, availableAreaSpan, fill)});
      growArrays.add(new double[] {0.0});  // cross-axis tracks are never growable; only alignment/fill apply there
    }

    double[] mins = mergeParallelMax(minArrays);
    double[] prefs = mergeParallelMax(prefArrays);
    double[] maxs = mergeParallelMax(maxArrays);
    double[] grow = mergeParallelMax(growArrays);
    double padding = layoutArea.cross().margin(insets);

    for(int i = 0; i < mins.length; i++) {
      mins[i] += padding;  // TODO adding snapped and unsnapped values together here
      prefs[i] += padding;
      maxs[i] += padding;
    }

    return new Tracks(mins, prefs, maxs, grow);
  }

  /**
   * Computes the main-axis thickness of each layout track, alternating with the
   * spacing between tracks.
   * <p>
   * For a horizontal layout this returns column widths and their horizontal gaps;
   * for a vertical layout this returns row heights and their vertical gaps.
   * The returned array follows the structure:
   *
   * <pre>
   * [span, gap, span, gap, span, ... , span]
   * </pre>
   * <p>
   * When multiple children occupy the same track, the track thickness is
   * determined by taking the maximum span value among the children in that track.
   *
   * @param availableAreaExtent the available extent along the cross axis, used when {@link Layoutable}s have a content bias,
   *   or {@code -1} if unconstrained
   * @return the resolved main-axis tracks (minimum, preferred and maximum length) and which of them are growable, never {@code null}
   */
  Tracks computeSpans(double availableAreaExtent) {
//    System.out.println("--start computeSpans(" + availableAreaExtent + ") called on: " + this);
    Tracks v = computeSpans(availableAreaExtent, false, aligned);

    spansCache.put(availableAreaExtent, v);
    //System.out.println("--end computeSpans(" + availableAreaExtent + ") called on: " + this);
    return v;
  }

  private Tracks computeSpans(double availableAreaExtent, boolean crossedAxis, boolean rootAligned) {
    List<double[]> minArrays = new ArrayList<>();
    List<double[]> prefArrays = new ArrayList<>();
    List<double[]> maxArrays = new ArrayList<>();
    List<double[]> growArrays = new ArrayList<>();

    for(Layoutable child : children) {

      /*
       * See computeExtents for why both branches below are gated by
       * !crossedAxis: only the first orientation crossing turns children
       * into grid tracks. Beyond that, a nested Flex of either orientation
       * is an opaque leaf, never traversed further.
       */

      if(!crossedAxis && rootAligned && extractLayout(child) instanceof FlexLayout fl) {
        if(fl.getOrientation() == orientation) {
          Tracks tracks = fl.computeSpans(availableAreaExtent, false, rootAligned);  // same orientation, so gather the same axis

          minArrays.add(tracks.mins());
          prefArrays.add(tracks.prefs());
          maxArrays.add(tracks.maxs());
          growArrays.add(tracks.grow());
          continue;
        }

        /*
         * Crossing here means: fl's own cross axis becomes a track on OUR
         * main axis, and fl is a *direct* child of ours on that axis, so
         * fl's own grow flag (Priority.ALWAYS growing it along our main
         * axis, like hgrow/vgrow on a nested HBox/VBox) is folded in here,
         * in addition to whatever fl's own subtree already reported
         * growable.
         */

        Tracks tracks = fl.computeExtents(availableAreaExtent, true, rootAligned);  // mismatched orientations means their cross-axis aligns with our main axis
        boolean grows = getGrow(child) == Priority.ALWAYS;

        minArrays.add(tracks.mins());
        prefArrays.add(tracks.prefs());
        maxArrays.add(tracks.maxs());
        growArrays.add(orEach(tracks.grow(), grows));
        continue;
      }

      Insets margin = getMargin(child);

      minArrays.add(new double[] {layoutArea.computeSpan(SizeQuery.MIN, child, -1, margin, availableAreaExtent, fill)});
      prefArrays.add(new double[] {layoutArea.computeSpan(SizeQuery.PREF, child, -1, margin, availableAreaExtent, fill)});
      maxArrays.add(new double[] {layoutArea.computeSpan(SizeQuery.MAX, child, -1, margin, availableAreaExtent, fill)});
      growArrays.add(new double[] {getGrow(child) == Priority.ALWAYS ? 1.0 : 0.0});  // main-axis tracks grow like Priority.ALWAYS on HBox/VBox
    }

    if(minArrays.isEmpty()) {
      return new Tracks(new double[0], new double[0], new double[0], new double[0]);
    }

    double snappedSpacing = layoutArea.snapSpace(spacing);
    double[] mins = mergeSequentialConcat(minArrays, snappedSpacing);
    double[] prefs = mergeSequentialConcat(prefArrays, snappedSpacing);
    double[] maxs = mergeSequentialConcat(maxArrays, snappedSpacing);
    double[] grow = mergeSequentialConcat(growArrays, 0.0);  // gaps between children are never growable

    if(mins.length > 0) {  // TODO adding snapped and unsnapped values together here
      double marginStart = layoutArea.marginStart(insets);
      double marginEnd = layoutArea.marginEnd(insets);

      mins[0] += marginStart;
      mins[mins.length - 1] += marginEnd;
      prefs[0] += marginStart;
      prefs[prefs.length - 1] += marginEnd;
      maxs[0] += marginStart;
      maxs[maxs.length - 1] += marginEnd;
    }

    return new Tracks(mins, prefs, maxs, grow);
  }

  /**
   * Returns a copy of {@code values} with {@code extra} OR-ed into every entry
   * (encoded as {@code 1.0}/{@code 0.0}), used to fold a crossed child's own
   * grow constraint into whatever its subtree already reported growable.
   */
  private static double[] orEach(double[] values, boolean extra) {
    if(!extra) {
      return values;
    }

    double[] result = new double[values.length];

    for(int i = 0; i < values.length; i++) {
      result[i] = Math.max(values[i], 1.0);
    }

    return result;
  }

  private static FlexLayout extractLayout(Layoutable layoutable) {
    return layoutable instanceof FlexLayout fl
      ? fl
      : layoutable.unwrap(Flex.class) instanceof Flex f
        ? f.asFlexLayout()
        : null;
//    Flexible f = layoutable.unwrap(Flexible.class);
//
//    return f == null ? null : f.asFlexLayout();
  }

  /**
   * Zips the child column arrays together, taking the maximum value
   * at each index since the children are stacked vertically.
   */
  private static double[] mergeParallelMax(List<double[]> arrays) {
    int maxLength = arrays.stream().mapToInt(a -> a.length).max().orElse(0);
    double[] merged = new double[maxLength];

    for(double[] array : arrays) {
      for(int i = 0; i < array.length; i++) {
        merged[i] = Math.max(merged[i], array[i]);
      }
    }

    return merged;
  }

  private static double[] mergeSequentialConcat(List<double[]> arrays, double spacing) {
    int totalLength = arrays.stream().mapToInt(a -> a.length).sum() + arrays.size() - 1;
    double[] concatenated = new double[totalLength];
    int destPos = 0;

    for(int i = 0; i < arrays.size(); i++) {
      double[] array = arrays.get(i);

      System.arraycopy(array, 0, concatenated, destPos, array.length);

      destPos += array.length;

      if(i < arrays.size() - 1) {
        concatenated[destPos++] = spacing;
      }
    }

    return concatenated;
  }

  /*
   * computeSpans() already snaps every entry of its returned array exactly
   * once (each track via computeSpan()'s own snapSize call, each gap via
   * snapSpace(spacing)) -- summing tracks and gaps together is exactly the
   * "combining a mix of sources" case snap() exists for: even though every
   * summand was already snapped, the summation itself still introduces its
   * own small deviation from the true snapped value, so this total -- about
   * to leave the system via minWidth()/prefWidth() -- gets exactly one final
   * snap() to correct for that. Using snapSize here instead (as the old
   * computeSpans2() did) would double-apply a ceiling meant to run exactly
   * once per root value, which can inflate the result by a whole pixel at
   * fractional render scales (see FlexLayoutSnappingTest).
   */
  private double totalSpan(SizeQuery sizeQuery, double availableAreaExtent) {
    return layoutArea.snap(sum(select(sizeQuery, computeSpans(availableAreaExtent))));
  }

  /* As totalSpan, but for this node's cross axis. */
  private double totalExtent(SizeQuery sizeQuery, double availableAreaSpan) {
    return layoutArea.cross().snap(sum(select(sizeQuery, computeExtents(availableAreaSpan))));
  }

  private static double[] select(SizeQuery sizeQuery, Tracks tracks) {
    return switch(sizeQuery) {
      case MIN -> tracks.mins();
      case PREF -> tracks.prefs();
      case MAX -> tracks.maxs();
    };
  }

  @Override
  public Orientation getContentBias() {
    if(biasDirty) {
      bias = null;

      for(Layoutable child : children) {
        Orientation contentBias = child.getContentBias();

        if(contentBias != null) {
          bias = contentBias;

          if(contentBias == orientation) {
            break;
          }
        }
      }

      biasDirty = false;
    }

    return bias;
  }

  private void layoutChildren() {
    if(children.isEmpty()) {
      return;
    }

    if(aligned) {
      layoutAligned();

      return;
    }

    double areaSpan = layoutArea.span();
    double areaExtent = layoutArea.extent();

    Tracks tracks = computeSpans(areaExtent);
    double[] prefs = excludingOwnSpanInsets(trackValues(tracks.prefs()));
    boolean[] grow = trackGrow(tracks.grow());
    double[] mins = excludingOwnSpanInsets(trackValues(tracks.mins()));
    double[] maxs = excludingOwnSpanInsets(trackValues(tracks.maxs()));

    double snappedSpacing = layoutArea.snapSpace(spacing);
    double availableSpan = Math.max(0, areaSpan - snappedSpacing * (children.size() - 1));
    double[] finalSpanSizes = resolveTrackSizes(prefs, mins, maxs, grow, availableSpan);

    double contentSpan = sum(finalSpanSizes) + snappedSpacing * (children.size() - 1);
    double spanOffset = layoutArea.spanStart(contentSpan);
    double extentOffset = layoutArea.extentStart();

    for(int i = 0, max = children.size(); i < max; i++) {
      Layoutable child = children.get(i);
      double spanSize = finalSpanSizes[i];

      layoutArea.layout(child, spanOffset, extentOffset, spanSize, areaExtent, 0);

      spanOffset += spanSize + snappedSpacing;
    }
  }

  private void layoutAligned() {
    double areaExtent = layoutArea.extent();
    double areaSpan = layoutArea.span();

    Tracks extentTracks = computeExtents(-1);
    double[] colPrefs = excludingOwnExtentInsets(trackValues(extentTracks.prefs()));
    double[] colGaps = trackGaps(extentTracks.prefs());
    boolean[] colGrow = trackGrow(extentTracks.grow());
    double[] colMins = excludingOwnExtentInsets(trackValues(extentTracks.mins()));
    double[] colMaxs = excludingOwnExtentInsets(trackValues(extentTracks.maxs()));

    Tracks spanTracks = computeSpans(-1);
    double[] rowPrefs = excludingOwnSpanInsets(trackValues(spanTracks.prefs()));
    double[] rowGaps = trackGaps(spanTracks.prefs());
    boolean[] rowGrow = trackGrow(spanTracks.grow());
    double[] rowMins = excludingOwnSpanInsets(trackValues(spanTracks.mins()));
    double[] rowMaxs = excludingOwnSpanInsets(trackValues(spanTracks.maxs()));

    double totalColGap = sum(colGaps);
    double availableColSpace = Math.max(0, areaExtent - totalColGap);
    double[] colWidths = resolveTrackSizes(colPrefs, colMins, colMaxs, colGrow, availableColSpace);

    double totalRowGap = sum(rowGaps);
    double availableRowSpace = Math.max(0, areaSpan - totalRowGap);
    double[] rowHeights = resolveTrackSizes(rowPrefs, rowMins, rowMaxs, rowGrow, availableRowSpace);

    double totalColWidth = sum(colWidths);
    double totalRowHeight = sum(rowHeights);

    double contentSpan = totalRowHeight + totalRowGap;
    double spanOffset = layoutArea.spanStart(contentSpan);
    double extentOffset = layoutArea.extentStart();

    double contentExtent = totalColWidth + totalColGap;
    double colOffset = switch(alignment.getHpos()) {
      case CENTER -> (areaExtent - contentExtent) / 2;
      case RIGHT -> areaExtent - contentExtent;
      default -> 0;
    };

    double[] colOffsets = trackOffsets(colWidths, colGaps, extentOffset + colOffset);
    double[] rowOffsets = trackOffsets(rowHeights, rowGaps, spanOffset);

    placeCoordinated(
      new Coordination(false, rowHeights, rowOffsets, new int[] {0}, colWidths, colOffsets),
      extentOffset + colOffset, totalColWidth + totalColGap
    );
  }

  private static double sum(double[] values) {
    double s = 0;

    for(double v : values) {
      s += v;
    }

    return s;
  }

  private static double[] targetsWithGrow(double[] prefs, double[] maxs, boolean[] grow) {
    double[] targets = new double[prefs.length];

    for(int i = 0; i < prefs.length; i++) {
      targets[i] = grow[i] ? maxs[i] : prefs[i];
    }

    return targets;
  }

  /**
   * Resolves this node's final track sizes from their preferred/minimum/
   * maximum sizes and grow flags: grows toward {@code maxs} where there is
   * spare space and a track wants it, otherwise shrinks toward {@code mins}.
   */
  private double[] resolveTrackSizes(double[] prefs, double[] mins, double[] maxs, boolean[] grow, double availableSpace) {
    double[] targets = sum(prefs) < availableSpace ? targetsWithGrow(prefs, maxs, grow) : mins.clone();

    return SpaceDistributor.distribute(availableSpace, isSnapToPixel() ? layoutArea.snapScale() : 0, prefs, targets);
  }

  private static double[] trackOffsets(double[] sizes, double[] gaps, double start) {
    double[] offsets = new double[sizes.length];
    double pos = start;

    for(int i = 0; i < sizes.length; i++) {
      offsets[i] = pos;
      pos += sizes[i];

      if(i < gaps.length) {
        pos += gaps[i];
      }
    }

    return offsets;
  }

  /**
   * Extracts the track values from an interleaved {@code [value, gap, value, ...]}
   * array, as returned by {@link Tracks#sizes()} or {@link Tracks#grow()}.
   */
  private static double[] trackValues(double[] interleaved) {
    double[] values = new double[(interleaved.length + 1) / 2];

    for(int i = 0; i < values.length; i++) {
      values[i] = interleaved[i * 2];
    }

    return values;
  }

  /**
   * Extracts the gaps from an interleaved {@code [value, gap, value, ...]}
   * array, as returned by {@link Tracks#sizes()}.
   */
  private static double[] trackGaps(double[] interleaved) {
    double[] gaps = new double[interleaved.length / 2];

    for(int i = 0; i < gaps.length; i++) {
      gaps[i] = interleaved[i * 2 + 1];
    }

    return gaps;
  }

  private static boolean[] trackGrow(double[] interleavedGrow) {
    double[] values = trackValues(interleavedGrow);
    boolean[] grow = new boolean[values.length];

    for(int i = 0; i < values.length; i++) {
      grow[i] = values[i] != 0.0;
    }

    return grow;
  }

  /*
   * computeSpans() bakes this node's own marginStart/marginEnd into the
   * first/last entry of the array it returns, so its own contribution sizes
   * correctly once merged into an ancestor's tracks, or summed into this
   * node's own reported preferred size by totalSpan()/prefWidth(). When
   * resolving how much space THIS node's own direct children get, that same
   * padding is already accounted for separately via spanStart()/span(), so it
   * must be excluded here to avoid counting it twice.
   */
  private double[] excludingOwnSpanInsets(double[] values) {
    if(values.length == 0) {
      return values;
    }

    double[] result = values.clone();

    result[0] -= layoutArea.marginStart(insets);
    result[result.length - 1] -= layoutArea.marginEnd(insets);

    return result;
  }

  /*
   * As excludingOwnSpanInsets, but for computeExtents(), which adds its own
   * margin uniformly to every entry rather than only to the first/last.
   */
  private double[] excludingOwnExtentInsets(double[] values) {
    double padding = layoutArea.cross().margin(insets);
    double[] result = new double[values.length];

    for(int i = 0; i < values.length; i++) {
      result[i] = values[i] - padding;
    }

    return result;
  }

  /**
   * Places this node at the given box, then places its own direct children
   * using {@code coordination}'s already-resolved tracks instead of
   * computing its own -- called by an aligned ancestor (possibly several
   * levels up, via intervening same-orientation wrappers) in place of a
   * normal resize.
   */
  void layoutCoordinated(Coordination coordination, double x, double y, double w, double h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;

    placeCoordinated(coordination, layoutArea.extentStart(), layoutArea.extent());
  }

  /**
   * Positions this node's direct children along its own main axis, mirroring
   * {@link #computeSpans}'s tree shape exactly, using a shared, mutable cursor
   * into the coordinated grid's already-resolved tracks -- so bookkeeping can
   * never drift out of sync with the sizes {@code computeSpans}/
   * {@code computeExtents} originally resolved.
   * <p>
   * This only walks as far down the tree as a coordinated descendant would
   * be found: through same-orientation wrappers (transparently, mirroring
   * the "flatten" branch), and exactly one level of opposite-orientation
   * crossing per aligned ancestor (mirroring the "cross" branch, but --
   * unlike the size computation -- not recursing any further once already
   * past the first crossing). Anything beyond that gets a single resolved
   * box and is left to lay out its own children independently, exactly as
   * it would outside an aligned tree.
   * <p>
   * {@code mainOffsets}/{@code otherOffsets} are absolute, never rebased: the
   * same shared arrays are read by every node in the coordinated subtree via
   * the advancing {@code mainCursor}. A coordinated child's own box (and thus
   * its own {@code x}/{@code y}/{@code w}/{@code h}) is computed the exact
   * same way a plain leaf's box would be, via {@link LayoutArea#layout}; from
   * there, {@link #layoutCoordinated} derives that child's own cross-axis
   * position/thickness for placing anything nested further, exactly like the
   * non-coordinated layout path already does via {@code extentStart()}/
   * {@code extent()} -- so no rebasing is needed there either, regardless of
   * whether that child turns out to be backed by a real {@code Flex} (whose
   * own {@code x}/{@code y} JavaFX will separately compose) or a bare
   * {@code FlexLayout} (whose {@code x}/{@code y} are used directly).
   *
   * @param coordination the resolved tracks to place this node's own direct children with, cannot be {@code null}
   * @param crossOffset this node's own single, already-resolved cross-axis start
   * @param crossSize this node's own single, already-resolved cross-axis thickness
   */
  private void placeCoordinated(Coordination coordination, double crossOffset, double crossSize) {
    boolean crossedAxis = coordination.crossedAxis();
    double[] mainSizes = coordination.mainSizes();
    double[] mainOffsets = coordination.mainOffsets();
    int[] mainCursor = coordination.mainCursor();
    double[] otherSizes = coordination.otherSizes();
    double[] otherOffsets = coordination.otherOffsets();

    for(Layoutable child : children) {
      FlexLayout fl = extractLayout(child);

      if(fl != null && fl.getOrientation() == orientation && !crossedAxis) {

        /*
         * Transparent same-orientation wrapper: keep consuming the SAME
         * shared main-axis tracks/cursor, handing it a fresh cursor of its
         * own (starting where it left off) for anything nested further
         * inside it.
         */

        int count = trackValueCount(fl.computeSpans(-1, false, aligned).prefs());
        int startIdx = mainCursor[0];
        double wrapperStart = mainOffsets[startIdx];

        mainCursor[0] += count;

        int endIdx = mainCursor[0];
        double wrapperMainSize = mainOffsets[endIdx - 1] + mainSizes[endIdx - 1] - wrapperStart;
        Coordination childCoordination = new Coordination(false, mainSizes, mainOffsets, new int[] {startIdx}, otherSizes, otherOffsets);

        layoutCoordinatedChild(child, childCoordination, wrapperStart, wrapperMainSize, crossOffset, crossSize);
        continue;
      }

      if(fl != null && fl.getOrientation() != orientation) {

        /*
         * Crossing into fl: it may have needed several of OUR tracks to
         * describe its own cross-axis thickness (e.g. a nested Flex with
         * several stacked children) -- replay the same query that originally
         * determined that, so the shared cursor consumes exactly as many
         * value-slots as computeExtents/computeSpans attributed to it. Its
         * interleaved [value, gap, value, ...] length maps to that many
         * de-interleaved value-slots in mainSizes/mainOffsets.
         */

        int count = trackValueCount(fl.computeExtents(-1, true, true).prefs());
        int startIdx = mainCursor[0];
        double start = mainOffsets[startIdx];

        mainCursor[0] += count;

        int endIdx = mainCursor[0];
        double size = mainOffsets[endIdx - 1] + mainSizes[endIdx - 1] - start;

        if(!crossedAxis) {

          /*
           * fl is coordinated: hand it our OTHER axis as its own main axis,
           * with a fresh cursor of its own.
           */

          Coordination childCoordination = new Coordination(true, otherSizes, otherOffsets, new int[] {0}, mainSizes, mainOffsets);

          layoutCoordinatedChild(child, childCoordination, start, size, crossOffset, crossSize);
        }
        else {

          /*
           * fl is not coordinated (already past the first crossing): give it
           * its box and let its own, independently-triggered layoutChildren()
           * lay out its children on its own.
           */

          layoutArea.layout(child, start, crossOffset, size, crossSize, 0);
        }

        continue;
      }

      /*
       * Leaf: a plain node, or a same-orientation Flex beyond the first
       * crossing (not coordinated either, so it manages its own children).
       * <p>
       * A leaf only ever contributes a single value to computeSpans'/
       * computeExtents' own cross-axis merge (mergeParallelMax always folds
       * it into track 0 specifically, never any other track) -- so before
       * the first crossing, its own placement must be narrowed to track 0
       * of the other axis too, not given the full combined cross-extent
       * every track shares in aggregate. Once already past the first
       * crossing, the aggregate crossOffset/crossSize IS already just a
       * single track's own slot, so no narrowing is needed there.
       */

      double leafStart = mainOffsets[mainCursor[0]];
      double leafSize = mainSizes[mainCursor[0]];

      mainCursor[0]++;

      double leafCrossOffset = crossedAxis ? crossOffset : otherOffsets[0];
      double leafCrossSize = crossedAxis ? crossSize : otherSizes[0];

      layoutArea.layout(child, leafStart, leafCrossOffset, leafSize, leafCrossSize, 0);
    }
  }

  /* Converts main/cross terms to x/y/w/h and hands the child its coordination via the shared Flexible interface. */
  private void layoutCoordinatedChild(Layoutable layoutable, Coordination coordination, double mainStart, double mainSize, double crossOffset, double crossSize) {
    double x = orientation == Orientation.HORIZONTAL ? mainStart : crossOffset;
    double y = orientation == Orientation.HORIZONTAL ? crossOffset : mainStart;
    double w = orientation == Orientation.HORIZONTAL ? mainSize : crossSize;
    double h = orientation == Orientation.HORIZONTAL ? crossSize : mainSize;

    if(layoutable instanceof FlexLayout fl) {
      fl.layoutCoordinated(coordination, x, y, w, h);
    }
    else if(layoutable.unwrap(Flex.class) instanceof Flex f) {
      f.layoutCoordinated(coordination, x, y, w, h);
    }
  }

  /* Number of de-interleaved value-slots in an interleaved [value, gap, value, ...] array. */
  private static int trackValueCount(double[] interleaved) {
    return (interleaved.length + 1) / 2;
  }

  interface LayoutArea extends Context<LayoutArea> {
    abstract double span();
    abstract double extent();
    abstract double spanStart(double contentSpan);
    abstract double extentStart();
    abstract void layout(Layoutable child, double spanOffset, double extentOffset, double span, double extent, double baselineOffset);
  }

  final class HorizontalLayoutArea extends AbstractHorizontalContext<LayoutArea> implements LayoutArea {
    private final VerticalLayoutArea cross;

    HorizontalLayoutArea(RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = new VerticalLayoutArea(this, renderScaleContext, snapToPixel);
    }

    HorizontalLayoutArea(VerticalLayoutArea cross, RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = cross;
    }


    @Override
    public VerticalLayoutArea cross() {
      return cross;
    }

    @Override
    public double span() {
      return w - insets.getLeft() - insets.getRight();
    }

    @Override
    public double extent() {
      return h - insets.getTop() - insets.getBottom();
    }

    @Override
    public double spanStart(double contentSpan) {
      return x + insets.getLeft() + computeXOffset(span(), contentSpan, alignment.getHpos());
    }

    @Override
    public double extentStart() {
      return y + insets.getTop();
    }

    @Override
    public void layout(Layoutable child, double spanOffset, double extentOffset, double span, double extent, double baselineOffset) {
      boolean fillSpan = getGrow(child) == Priority.ALWAYS;
      Insets margin = getMargin(child);

      layoutInArea(child, spanOffset, extentOffset, span, extent, baselineOffset, margin, fillSpan, fill || aligned, alignment.getHpos(), alignment.getVpos());
    }
  }

  final class VerticalLayoutArea extends AbstractVerticalContext<LayoutArea> implements LayoutArea {
    private final HorizontalLayoutArea cross;

    VerticalLayoutArea(RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = new HorizontalLayoutArea(this, renderScaleContext, snapToPixel);
    }

    VerticalLayoutArea(HorizontalLayoutArea cross, RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = cross;
    }

    @Override
    public HorizontalLayoutArea cross() {
      return cross;
    }

    @Override
    public double span() {
        return h - insets.getTop() - insets.getBottom();
    }

    @Override
    public double extent() {
        return w - insets.getLeft() - insets.getRight();
    }

    @Override
    public double spanStart(double contentSpan) {
        return y + insets.getTop() + computeYOffset(span(), contentSpan, alignment.getVpos());
    }

    @Override
    public double extentStart() {
        return x + insets.getLeft();
    }

    @Override
    public void layout(Layoutable child, double spanOffset, double extentOffset, double span, double extent, double baselineOffset) {
      boolean fillSpan = getGrow(child) == Priority.ALWAYS;
      Insets margin = getMargin(child);

      layoutInArea(child, extentOffset, spanOffset, extent, span, baselineOffset, margin, fill || aligned, fillSpan, alignment.getHpos(), alignment.getVpos());
    }
  }

  @Override
  public String toString() {
    return "FlexLayout[" + orientation + " " + children.size() + " children " + System.identityHashCode(this) + "]";
  }
}
