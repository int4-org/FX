package org.int4.fx.layout.internal;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;

final class NodeLayoutable implements Layoutable {
  final Node n;

  NodeLayoutable(Node n) {
    this.n = n;
  }

  @Override
  public double minWidth(double height) {
    return n.minWidth(height);
  }

  @Override
  public double prefWidth(double height) {
    return n.prefWidth(height);
  }

  @Override
  public double maxWidth(double height) {
    return n.maxWidth(height);
  }

  @Override
  public double minHeight(double width) {
    return n.minHeight(width);
  }

  @Override
  public double prefHeight(double width) {
    return n.prefHeight(width);
  }

  @Override
  public double maxHeight(double width) {
    return n.maxHeight(width);
  }

  @Override
  public boolean isResizable() {
    return n.isResizable();
  }

  @Override
  public double getBaselineOffset() {
    return n.getBaselineOffset();
  }

  @Override
  public Orientation getContentBias() {
    return n.getContentBias();
  }

  @Override
  public void resizeRelocate(double x, double y, double w, double h) {
    n.resizeRelocate(x, y, w, h);
  }

  @Override
  public void relocate(double x, double y) {
    n.relocate(x, y);
  }

  @Override
  public Bounds getLayoutBounds() {
    return n.getLayoutBounds();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getConstraint(ConstraintKey<T> key, T defaultValue) {
    if(n.hasProperties()) {
      return (T)n.getProperties().getOrDefault(key, defaultValue);
    }

    return defaultValue;
  }

  @Override
  public <T> void setConstraint(ConstraintKey<T> key, T value) {
    n.getProperties().put(key, value);
  }

  @Override
  public String toString() {
    return "Layoutable[" + n + "]";
  }
}