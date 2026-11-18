package org.int4.fx.layout.flex;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;

import org.int4.common.function.TriFunction;
import org.int4.fx.layout.internal.Layoutable;
import org.int4.fx.layout.internal.Measurable;

abstract class AbstractChildLayout extends AbstractLayout {
  private static final double BASELINE_OFFSET_SAME_AS_HEIGHT = Double.NEGATIVE_INFINITY;

  public enum SizeQuery {  // enum better?
    MIN((ctx, c, alt) -> ctx.min(c, alt)),
    PREF((ctx, c, alt) -> boundedSize(ctx.min(c, alt), ctx.pref(c, alt), ctx.max(c, alt))),
    MAX((ctx, c, alt) -> boundedSize(ctx.min(c, alt), ctx.max(c, alt), Double.MAX_VALUE));

    private final TriFunction<Context<?>, Measurable, Double, Double> query;

    SizeQuery(TriFunction<Context<?>, Measurable, Double, Double> query) {
      this.query = query;
    }

    double compute(Context<?> context, Measurable child, double alt) {
      return query.apply(context, child, alt);
    }
  }

  interface Context<C extends Context<?>> {
    double margin(Insets margin);
    double marginStart(Insets margin);
    double marginEnd(Insets margin);
    double snapSpace(double d);
    double snapSize(double d);
    double snapScale();
    double min(Measurable child, double extent);
    double pref(Measurable child, double extent);
    double max(Measurable child, double extent);
    Orientation orientation();
    C cross();

    /*
     * Every floating point operation, even on already-snapped inputs, causes
     * some deviation from the true snapped value (e.g. summing several
     * already-snapped tracks and gaps is not guaranteed to land back exactly
     * on the pixel grid). That is why inputs must be pre-snapped before any
     * calculation -- it keeps that deviation small -- and why a final result
     * combining a mix of sources (e.g. content tracks and gaps) must be
     * snapped once more, right before it leaves the system (e.g. what
     * minWidth()/prefWidth() return). Unlike snapSize, this always rounds
     * (like snapSpace): the "must not shrink content" guarantee already
     * happened at the leaf level, so only floating-point noise remains to
     * correct for here.
     */
    default double snap(double d) {
      return snapSpace(d);
    }

    default double computeSpan(SizeQuery query, Measurable child, double baselineComplement, Insets margin, double extent, boolean fillExtent) {
      boolean usesBaseline = baselineComplement != -1 && orientation() == Orientation.VERTICAL;

      if(usesBaseline) {
        double baseline = child.getBaselineOffset();

        if(baseline != BASELINE_OFFSET_SAME_AS_HEIGHT) {
          // TODO baseline and baselineComplement are unsnapped here, and the result isn't snapped either -- currently unreachable from FlexLayout (always calls with baselineComplement == -1), fix before wiring up baseline support
          return baseline + baselineComplement;
        }
      }

      double dependentExtent = -1;

      if(extent != -1 && child.getContentBias() == cross().orientation()) {  // span depends on cross span
        double areaExtent = baselineComplement != -1 && orientation() == Orientation.HORIZONTAL && child.getBaselineOffset() == BASELINE_OFFSET_SAME_AS_HEIGHT
          ? extent - baselineComplement : extent;

        dependentExtent = cross().computeDependentExtent(child, margin, areaExtent, fillExtent);
      }

      return margin(margin)
        + snapSize(query.compute(this, child, dependentExtent))
        + (usesBaseline ? baselineComplement : 0);
    }

    default double computeDependentExtent(Measurable child, Insets margin, double areaExtent, boolean fillExtent) {
      double contentExtent = areaExtent - margin(margin);

      return fillExtent
        ? snapSize(boundedSize(min(child, -1), contentExtent, max(child, -1)))
        : snapSize(boundedSize(min(child, -1), pref(child, -1), Math.min(max(child, -1), contentExtent)));
    }
  }

  static class HorizontalContext extends AbstractHorizontalContext<VerticalContext> {
    private final VerticalContext cross;

    public HorizontalContext(RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = new VerticalContext(this, renderScaleContext, snapToPixel);
    }

    public HorizontalContext(VerticalContext cross, RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = cross;
    }

    @Override
    public VerticalContext cross() {
      return cross;
    }
  }

  static abstract class AbstractHorizontalContext<C extends Context<?>> implements Context<C> {
    private final RenderScaleContext renderScaleContext;
    private final Snapper snapper;

    protected AbstractHorizontalContext(RenderScaleContext renderScaleContext, boolean snapToPixel) {
      this.renderScaleContext = renderScaleContext;
      this.snapper = snapToPixel ? Snapper.createSnapper(renderScaleContext) : Snapper.NO_SNAPPING;
    }

    @Override
    public double margin(Insets margin) {
      return margin == null ? 0 : snapSpace(margin.getLeft()) + snapSpace(margin.getRight());
    }

    @Override
    public double marginStart(Insets margin) {
      return margin == null ? 0 : snapSpace(margin.getLeft());
    }

    @Override
    public double marginEnd(Insets margin) {
      return margin == null ? 0 : snapSpace(margin.getRight());
    }

    @Override
    public double snapScale() {
      return renderScaleContext.snapScaleX();
    }

    @Override
    public double snapSpace(double d) {
      return snapper.snapSpaceX(d);
    }

    @Override
    public double snapSize(double d) {
      return snapper.snapSizeX(d);
    }

    @Override
    public double min(Measurable child, double extent) {
      return child.minWidth(extent);
    }

    @Override
    public double pref(Measurable child, double extent) {
      return child.prefWidth(extent);
    }

    @Override
    public double max(Measurable child, double extent) {
      return child.maxWidth(extent);
    }

    @Override
    public Orientation orientation() {
      return Orientation.HORIZONTAL;
    }
  }

  static class VerticalContext extends AbstractVerticalContext<HorizontalContext> {
    private final HorizontalContext cross;

    public VerticalContext(HorizontalContext cross, RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = cross;
    }

    public VerticalContext(RenderScaleContext renderScaleContext, boolean snapToPixel) {
      super(renderScaleContext, snapToPixel);

      this.cross = new HorizontalContext(this, renderScaleContext, snapToPixel);
    }

    @Override
    public HorizontalContext cross() {
      return cross;
    }
  }

  static abstract class AbstractVerticalContext<C extends Context<?>> implements Context<C> {
    private final RenderScaleContext renderScaleContext;
    private final Snapper snapper;

    protected AbstractVerticalContext(RenderScaleContext renderScaleContext, boolean snapToPixel) {
      this.renderScaleContext = renderScaleContext;
      this.snapper = snapToPixel ? Snapper.createSnapper(renderScaleContext) : Snapper.NO_SNAPPING;
    }

    @Override
    public double margin(Insets margin) {
      return margin == null ? 0 : snapSpace(margin.getTop()) + snapSpace(margin.getBottom());
    }

    @Override
    public double marginStart(Insets margin) {
      return margin == null ? 0 : snapSpace(margin.getTop());
    }

    @Override
    public double marginEnd(Insets margin) {
      return margin == null ? 0 : snapSpace(margin.getBottom());
    }

    @Override
    public double snapScale() {
      return renderScaleContext.snapScaleY();
    }

    @Override
    public double snapSpace(double d) {
      return snapper.snapSpaceY(d);
    }

    @Override
    public double snapSize(double d) {
      return snapper.snapSizeY(d);
    }

    @Override
    public double min(Measurable child, double extent) {
      return child.minHeight(extent);
    }

    @Override
    public double pref(Measurable child, double extent) {
      return child.prefHeight(extent);
    }

    @Override
    public double max(Measurable child, double extent) {
      return child.maxHeight(extent);
    }

    @Override
    public Orientation orientation() {
      return Orientation.VERTICAL;
    }
  }

  /**
   * Utility method which lays out the child within an area of this
   * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
   * with a baseline offset relative to that area.
   * <p>
   * If the child is resizable, this method will resize it to fill the specified
   * area unless the node's maximum size prevents it.  If the node's maximum
   * size preference is less than the area size, the maximum size will be used.
   * If node's maximum is greater than the area size, then the node will be
   * resized to fit within the area, unless its minimum size prevents it.
   * <p>
   * If the child has a non-null contentBias, then this method will use it when
   * resizing the child.  If the contentBias is horizontal, it will set its width
   * first to the area's width (up to the child's max width limit) and then pass
   * that value to compute the child's height.  If child's contentBias is vertical,
   * then it will set its height to the area height (up to child's max height limit)
   * and pass that height to compute the child's width.  If the child's contentBias
   * is null, then it's width and height have no dependencies on each other.
   * <p>
   * If the child is not resizable (Shape, Group, etc) then it will only be
   * positioned and not resized.
   * <p>
   * If the child's resulting size differs from the area's size (either
   * because it was not resizable or it's sizing preferences prevented it), then
   * this function will align the node relative to the area using horizontal and
   * vertical alignment values.
   * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
   * with the area baseline offset parameter, otherwise the baseline parameter
   * is ignored.
   * <p>
   * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
   * values will be rounded to their nearest pixel boundaries and the
   * width/height values will be ceiled to the next pixel boundary.
   *
   * @param child the child being positioned within this region
   * @param areaX the horizontal offset of the layout area relative to this region
   * @param areaY the vertical offset of the layout area relative to this region
   * @param areaWidth  the width of the layout area
   * @param areaHeight the height of the layout area
   * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
   * @param halignment the horizontal alignment for the child within the area
   * @param valignment the vertical alignment for the child within the area
   *
   */
  protected final void layoutInArea(Layoutable child, double areaX, double areaY,
    double areaWidth, double areaHeight,
    double areaBaselineOffset,
    HPos halignment, VPos valignment
  ) {
    layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, Insets.EMPTY, halignment, valignment);
  }

  /**
   * Utility method which lays out the child within an area of this
   * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
   * with a baseline offset relative to that area.
   * <p>
   * If the child is resizable, this method will resize it to fill the specified
   * area unless the node's maximum size prevents it.  If the node's maximum
   * size preference is less than the area size, the maximum size will be used.
   * If node's maximum is greater than the area size, then the node will be
   * resized to fit within the area, unless its minimum size prevents it.
   * <p>
   * If the child has a non-null contentBias, then this method will use it when
   * resizing the child.  If the contentBias is horizontal, it will set its width
   * first to the area's width (up to the child's max width limit) and then pass
   * that value to compute the child's height.  If child's contentBias is vertical,
   * then it will set its height to the area height (up to child's max height limit)
   * and pass that height to compute the child's width.  If the child's contentBias
   * is null, then it's width and height have no dependencies on each other.
   * <p>
   * If the child is not resizable (Shape, Group, etc) then it will only be
   * positioned and not resized.
   * <p>
   * If the child's resulting size differs from the area's size (either
   * because it was not resizable or it's sizing preferences prevented it), then
   * this function will align the node relative to the area using horizontal and
   * vertical alignment values.
   * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
   * with the area baseline offset parameter, otherwise the baseline parameter
   * is ignored.
   * <p>
   * If {@code margin} is non-null, then that space will be allocated around the
   * child within the layout area.  margin may be null.
   * <p>
   * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
   * values will be rounded to their nearest pixel boundaries and the
   * width/height values will be ceiled to the next pixel boundary.
   *
   * @param child the child being positioned within this region
   * @param areaX the horizontal offset of the layout area relative to this region
   * @param areaY the vertical offset of the layout area relative to this region
   * @param areaWidth  the width of the layout area
   * @param areaHeight the height of the layout area
   * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
   * @param margin the margin of space to be allocated around the child
   * @param halignment the horizontal alignment for the child within the area
   * @param valignment the vertical alignment for the child within the area
   */
  protected final void layoutInArea(Layoutable child, double areaX, double areaY,
    double areaWidth, double areaHeight,
    double areaBaselineOffset,
    Insets margin,
    HPos halignment, VPos valignment
  ) {
    layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin, true, true, halignment, valignment);
  }

  /**
   * Utility method which lays out the child within an area of this
   * region defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
   * with a baseline offset relative to that area.
   * <p>
   * If the child is resizable, this method will use {@code fillWidth} and {@code fillHeight}
   * to determine whether to resize it to fill the area or keep the child at its
   * preferred dimension.  If fillWidth/fillHeight are true, then this method
   * will only resize the child up to its max size limits.  If the node's maximum
   * size preference is less than the area size, the maximum size will be used.
   * If node's maximum is greater than the area size, then the node will be
   * resized to fit within the area, unless its minimum size prevents it.
   * <p>
   * If the child has a non-null contentBias, then this method will use it when
   * resizing the child.  If the contentBias is horizontal, it will set its width
   * first and then pass that value to compute the child's height.  If child's
   * contentBias is vertical, then it will set its height first
   * and pass that value to compute the child's width.  If the child's contentBias
   * is null, then it's width and height have no dependencies on each other.
   * <p>
   * If the child is not resizable (Shape, Group, etc) then it will only be
   * positioned and not resized.
   * <p>
   * If the child's resulting size differs from the area's size (either
   * because it was not resizable or it's sizing preferences prevented it), then
   * this function will align the node relative to the area using horizontal and
   * vertical alignment values.
   * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
   * with the area baseline offset parameter, otherwise the baseline parameter
   * is ignored.
   * <p>
   * If {@code margin} is non-null, then that space will be allocated around the
   * child within the layout area.  margin may be null.
   * <p>
   * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
   * values will be rounded to their nearest pixel boundaries and the
   * width/height values will be ceiled to the next pixel boundary.
   *
   * @param child the child being positioned within this region
   * @param areaX the horizontal offset of the layout area relative to this region
   * @param areaY the vertical offset of the layout area relative to this region
   * @param areaWidth  the width of the layout area
   * @param areaHeight the height of the layout area
   * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
   * @param margin the margin of space to be allocated around the child
   * @param fillWidth whether or not the child should be resized to fill the area width or kept to its preferred width
   * @param fillHeight whether or not the child should e resized to fill the area height or kept to its preferred height
   * @param halignment the horizontal alignment for the child within the area
   * @param valignment the vertical alignment for the child within the area
   */
  protected final void layoutInArea(Layoutable child, double areaX, double areaY,
    double areaWidth, double areaHeight,
    double areaBaselineOffset,
    Insets margin, boolean fillWidth, boolean fillHeight,
    HPos halignment, VPos valignment
  ) {
    layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin, fillWidth, fillHeight, halignment, valignment, isSnapToPixel());
  }

  /**
   * Utility method which lays out the child within an area of it's
   * parent defined by {@code areaX}, {@code areaY}, {@code areaWidth} x {@code areaHeight},
   * with a baseline offset relative to that area.
   * <p>
   * If the child is resizable, this method will use {@code fillWidth} and {@code fillHeight}
   * to determine whether to resize it to fill the area or keep the child at its
   * preferred dimension.  If fillWidth/fillHeight are true, then this method
   * will only resize the child up to its max size limits.  If the node's maximum
   * size preference is less than the area size, the maximum size will be used.
   * If node's maximum is greater than the area size, then the node will be
   * resized to fit within the area, unless its minimum size prevents it.
   * <p>
   * If the child has a non-null contentBias, then this method will use it when
   * resizing the child.  If the contentBias is horizontal, it will set its width
   * first and then pass that value to compute the child's height.  If child's
   * contentBias is vertical, then it will set its height first
   * and pass that value to compute the child's width.  If the child's contentBias
   * is null, then it's width and height have no dependencies on each other.
   * <p>
   * If the child is not resizable (Shape, Group, etc) then it will only be
   * positioned and not resized.
   * <p>
   * If the child's resulting size differs from the area's size (either
   * because it was not resizable or it's sizing preferences prevented it), then
   * this function will align the node relative to the area using horizontal and
   * vertical alignment values.
   * If valignment is {@code VPos.BASELINE} then the node's baseline will be aligned
   * with the area baseline offset parameter, otherwise the baseline parameter
   * is ignored.
   * <p>
   * If {@code margin} is non-null, then that space will be allocated around the
   * child within the layout area.  margin may be null.
   * <p>
   * If {@code snapToPixel} is {@code true} for this region, then the resulting x,y
   * values will be rounded to their nearest pixel boundaries and the
   * width/height values will be ceiled to the next pixel boundary.
   *
   * @param child the child being positioned within this region
   * @param areaX the horizontal offset of the layout area relative to this region
   * @param areaY the vertical offset of the layout area relative to this region
   * @param areaWidth  the width of the layout area
   * @param areaHeight the height of the layout area
   * @param areaBaselineOffset the baseline offset to be used if VPos is BASELINE
   * @param margin the margin of space to be allocated around the child
   * @param fillWidth whether or not the child should be resized to fill the area width or kept to its preferred width
   * @param fillHeight whether or not the child should e resized to fill the area height or kept to its preferred height
   * @param halignment the horizontal alignment for the child within the area
   * @param valignment the vertical alignment for the child within the area
   * @param isSnapToPixel whether to snap size and position to pixels
   */
  protected final void layoutInArea(Layoutable child, double areaX, double areaY,
    double areaWidth, double areaHeight,
    double areaBaselineOffset,
    Insets margin, boolean fillWidth, boolean fillHeight,
    HPos halignment, VPos valignment, boolean isSnapToPixel
  ) {
    Insets childMargin = margin == null ? Insets.EMPTY : margin;
    double top = snapSpaceY(childMargin.getTop());
    double bottom = snapSpaceY(childMargin.getBottom());
    double left = snapSpaceX(childMargin.getLeft());
    double right = snapSpaceX(childMargin.getRight());

    if(valignment == VPos.BASELINE) {
      // In BASELINE mode, 'top' represents the offset needed to align the child's baseline
      // TODO baselineOffset (child.getBaselineOffset()) is used raw/unsnapped below -- currently unreachable from FlexLayout (areaBaselineOffset is always passed as 0), fix before wiring up baseline support
      double baselineOffset = child.getBaselineOffset();

      if(baselineOffset == BASELINE_OFFSET_SAME_AS_HEIGHT) {
        if(child.isResizable()) {

          /*
           * For children that are placed exactly on top of the baseline, simply
           * adjust the bottom inset to exclude this area for resizing.
           */

          bottom += snapSpaceY(areaHeight - areaBaselineOffset);
        }
        else {
          top = snapSpaceY(areaBaselineOffset - child.getLayoutBounds().getHeight());
        }
      }
      else {
        top = snapSpaceY(areaBaselineOffset - baselineOffset);
      }
    }

    double usableWidth = areaWidth - left - right;
    double usableHeight = areaHeight - top - bottom;
    Size size = child.isResizable()
      ? biasedSize(child, usableWidth, usableHeight, fillWidth, fillHeight)
      : new Size(child.getLayoutBounds().getWidth(), child.getLayoutBounds().getHeight());

    /*
     * Relocation must happen after resize as baseline calculations may
     * query the size.
     */

    double x = areaX + left + computeXPos(size.w, usableWidth, halignment);
    double y = areaY + switch(valignment) {
      case BASELINE -> computeBaselineYPos(child, size.h, areaBaselineOffset);
      default -> top + computeYPos(size.h, usableHeight, valignment);
    };

    if(child.isResizable()) {
      child.resizeRelocate(snapPositionX(x), snapPositionY(y), size.w, size.h);
    }
    else {
      child.relocate(snapPositionX(x), snapPositionY(y));
    }
  }

  private static double computeXPos(double childWidth, double usableWidth, HPos hpos) {
    double unusedWidth = usableWidth - childWidth;

    return unusedWidth * hposFactor(hpos);
  }

  private static double computeYPos(double childHeight, double usableHeight, VPos vpos) {
    double unusedHeight = usableHeight - childHeight;

    return unusedHeight * vposFactor(vpos);
  }

  private static double computeBaselineYPos(Layoutable child, double childHeight, double areaBaselineOffset) {
    double offset = child.getBaselineOffset();

    return areaBaselineOffset - (offset == BASELINE_OFFSET_SAME_AS_HEIGHT ? childHeight : offset);
  }

  private static double hposFactor(HPos hpos) {
    return switch(hpos) {
      case LEFT -> 0.0;
      case CENTER -> 0.5;
      case RIGHT -> 1.0;
    };
  }

  private static double vposFactor(VPos vpos) {
    return switch(vpos) {
      case TOP, BASELINE -> 0.0;
      case CENTER -> 0.5;
      case BOTTOM -> 1.0;
    };
  }

  private record Size(double w, double h) {}

  /**
   * Returns the size of a Node that should be placed in an area of the specified size,
   * bounded in it's min/max size, respecting bias.
   *
   * @param n the node
   * @param areaWidth the width of the bounding area where the node is going to be placed
   * @param areaHeight the height of the bounding area where the node is going to be placed
   * @param fillWidth if Node should try to fill the area width
   * @param fillHeight if Node should try to fill the area height
   */
  private Size biasedSize(Layoutable n, double areaWidth, double areaHeight, boolean fillWidth, boolean fillHeight) {
    Orientation bias = n.getContentBias();
    double w = 0;
    double h = 0;

    if(bias == Orientation.VERTICAL) {
      h = boundedSize(n.minHeight(-1), fillHeight ? areaHeight : Math.min(areaHeight, n.prefHeight(-1)), n.maxHeight(-1));
      w = boundedSize(n.minWidth(h), fillWidth ? areaWidth : Math.min(areaWidth, n.prefWidth(h)), n.maxWidth(h));
    }
    else {
      w = boundedSize(n.minWidth(-1), fillWidth ? areaWidth : Math.min(areaWidth, n.prefWidth(-1)), n.maxWidth(-1));

      double d = bias == Orientation.HORIZONTAL ? w : -1;

      h = boundedSize(n.minHeight(d), fillHeight ? areaHeight : Math.min(areaHeight, n.prefHeight(d)), n.maxHeight(d));
    }

    return new Size(snapSizeX(w), snapSizeY(h));
  }

  /**
   * Computes the value based on the given min and max values. We encode in this method the logic surrounding various
   * edge cases, such as when the min is specified as greater than the max, or the max less than the min, or a pref
   * value that exceeds either the max or min in their extremes.
   * <p>
   * If the min is greater than the max, then we want to make sure the returned value is the min. In other words, in
   * such a case, the min becomes the only acceptable return value.
   * <p>
   * If the min and max values are well ordered, and the pref is less than the min then the min is returned. Likewise,
   * if the values are well ordered and the pref is greater than the max, then the max is returned. If the pref lies
   * between the min and the max, then the pref is returned.
   *
   * @param min The minimum bound
   * @param pref The value to be clamped between the min and max
   * @param max the maximum bound
   * @return the size bounded by min, pref, and max.
   */
  private static double boundedSize(double min, double pref, double max) {
    double a = pref >= min ? pref : min;
    double b = min >= max ? min : max;

    return a <= b ? a : b;
  }

  static double computeXOffset(double width, double contentWidth, HPos hpos) {
    switch(hpos) {
      case LEFT:
        return 0;
      case CENTER:
        return (width - contentWidth) / 2;
      case RIGHT:
        return width - contentWidth;
      default:
        throw new AssertionError("Unhandled hPos");
    }
  }

  static double computeYOffset(double height, double contentHeight, VPos vpos) {
    switch(vpos) {
      case BASELINE:
      case TOP:
        return 0;
      case CENTER:
        return (height - contentHeight) / 2;
      case BOTTOM:
        return height - contentHeight;
      default:
        throw new AssertionError("Unhandled vPos");
    }
  }
}
