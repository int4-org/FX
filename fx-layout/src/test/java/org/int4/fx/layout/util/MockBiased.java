package org.int4.fx.layout.util;

import javafx.geometry.Orientation;
import javafx.scene.layout.Region;

public class MockBiased extends Region {
  private double prefWidth;
  private double prefHeight;
  private double area;
  private Orientation bias;

  public MockBiased(Orientation bias, double prefWidth, double prefHeight) {
    this.bias = bias;
    this.prefWidth = prefWidth;
    this.prefHeight = prefHeight;
    this.area = prefWidth * prefHeight;
  }

  @Override
  public Orientation getContentBias() {
    return bias;
  }

  @Override
  protected double computeMinWidth(double height) {
    return bias == Orientation.HORIZONTAL ? 10 : area / (height != -1 ? height : prefHeight(-1));
  }

  @Override
  protected double computeMinHeight(double width) {
    return bias == Orientation.VERTICAL ? 10 : area / (width != -1 ? width : prefWidth(-1));
  }

  @Override
  protected double computePrefWidth(double height) {
    return bias == Orientation.HORIZONTAL ? prefWidth : area / (height != -1 ? height : prefHeight(-1));
  }

  @Override
  protected double computePrefHeight(double width) {
    return bias == Orientation.VERTICAL ? prefHeight : area / (width != -1 ? width : prefWidth(-1));
  }

  @Override
  protected double computeMaxWidth(double height) {
    return bias == Orientation.HORIZONTAL ? area : area / (height != -1 ? height : prefHeight(-1));
  }

  @Override
  protected double computeMaxHeight(double width) {
    return bias == Orientation.VERTICAL ? area : area / (width != -1 ? width : prefWidth(-1));
  }
}