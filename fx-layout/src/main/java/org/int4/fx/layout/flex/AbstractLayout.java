package org.int4.fx.layout.flex;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.int4.fx.layout.internal.ConstraintKey;
import org.int4.fx.layout.internal.Layout;

abstract class AbstractLayout implements Layout {
  private RenderScaleContext renderScaleContext = RenderScaleContext.DEFAULT;
  private Map<Object, Object> properties;
  private boolean snapToPixel = true;
  private Snapper snapper = Snapper.DEFAULT;

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getConstraint(ConstraintKey<T> key, T defaultValue) {
    return properties == null ? defaultValue : (T)properties.getOrDefault(key, defaultValue);
  }

  @Override
  public <T> void setConstraint(ConstraintKey<T> key, T value) {
    if(properties == null) {
      this.properties = new HashMap<>();
    }

    this.properties.put(key, value);
  }

  public final boolean isSnapToPixel() {
    return snapToPixel;
  }

  public final void setSnapToPixel(boolean snapToPixel) {
    if(this.snapToPixel == snapToPixel) {
      return;
    }

    this.snapToPixel = snapToPixel;
    this.snapper = snapToPixel ? Snapper.createSnapper(renderScaleContext) : Snapper.NO_SNAPPING;

    onSnapToPixelChanged();
  }

  public final RenderScaleContext getRenderScaleContext() {
    return renderScaleContext;
  }

  public final void setRenderScaleContext(RenderScaleContext renderScaleContext) {
    if(this.renderScaleContext.equals(Objects.requireNonNull(renderScaleContext, "renderScaleContext"))) {
      return;
    }

    this.renderScaleContext = renderScaleContext;
    this.snapper = snapToPixel ? Snapper.createSnapper(renderScaleContext) : Snapper.NO_SNAPPING;

    onRenderScaleContextChanged();
  }

  protected void onSnapToPixelChanged() {}
  protected void onRenderScaleContextChanged() {}

  protected final double snapSpaceX(double v) {
    return snapper.snapSpaceX(v);
  }

  protected final double snapSpaceY(double v) {
    return snapper.snapSpaceY(v);
  }

  protected final double snapPositionX(double v) {
    return snapper.snapSpaceX(v);
  }

  protected final double snapPositionY(double v) {
    return snapper.snapSpaceY(v);
  }

  protected final double snapSizeX(double v) {
    return snapper.snapSizeX(v);
  }

  protected final double snapSizeY(double v) {
    return snapper.snapSizeY(v);
  }
}
