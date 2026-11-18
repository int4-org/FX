package org.int4.fx.layout.flex;

import javafx.scene.Node;
import javafx.scene.layout.Region;

/*
 * Minimal harness for the "standalone FlexLayout" usage pattern: a real Node
 * that owns a tree of bare FlexLayout instances directly (no Flex/Region
 * wrapper anywhere in between) purely as a layout-computation engine for its
 * own real scene graph children. The real children must still be added to
 * this Region's own getChildren() by the caller -- the FlexLayout tree only
 * computes sizes and positions for them, it has no scene graph presence of
 * its own.
 */
class StandaloneFlexNode extends Region {
  private final FlexLayout root;

  StandaloneFlexNode(FlexLayout root, Node... realChildren) {
    this.root = root;

    getChildren().addAll(realChildren);
  }

  @Override
  protected double computeMinWidth(double height) {
    return root.minWidth(height);
  }

  @Override
  protected double computePrefWidth(double height) {
    return root.prefWidth(height);
  }

  @Override
  protected double computeMaxWidth(double height) {
    return root.maxWidth(height);
  }

  @Override
  protected double computeMinHeight(double width) {
    return root.minHeight(width);
  }

  @Override
  protected double computePrefHeight(double width) {
    return root.prefHeight(width);
  }

  @Override
  protected double computeMaxHeight(double width) {
    return root.maxHeight(width);
  }

  @Override
  public void requestLayout() {
    root.invalidate();

    super.requestLayout();
  }

  @Override
  protected void layoutChildren() {
    root.resizeRelocate(0, 0, getWidth(), getHeight());
  }
}
