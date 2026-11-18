package org.int4.fx.layout.util;

import javafx.scene.layout.Region;

public class MockResizable extends Region {
  private final double minWidth;
  private final double minHeight;
  private final double prefWidth;
  private final double prefHeight;
  private final double maxWidth;
  private final double maxHeight;

  private Double baselineOffset;

  public MockResizable(double prefWidth, double prefHeight) {
    this(1, 1, prefWidth, prefHeight);
  }

  public MockResizable(double minWidth, double minHeight, double prefWidth, double prefHeight) {
    this(minWidth, minHeight, prefWidth, prefHeight, 5000, 5000);
  }

  public MockResizable(double minWidth, double minHeight, double prefWidth, double prefHeight, double maxWidth, double maxHeight) {
    this.minWidth = minWidth;
    this.minHeight = minHeight;
    this.prefWidth = prefWidth;
    this.prefHeight = prefHeight;
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
  }

  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public double getBaselineOffset() {
    return baselineOffset == null ? Math.max(0, prefHeight - 10) : baselineOffset;
  }

  public MockResizable baselineOffset(double baselineOffset) {
    this.baselineOffset = baselineOffset;

    return this;
  }

  @Override
  public double computeMinWidth(double height) {
    return minWidth;
  }

  @Override
  public double computeMinHeight(double width) {
    return minHeight;
  }

  @Override
  public double computePrefWidth(double height) {
    return prefWidth;
  }

  @Override
  public double computePrefHeight(double width) {
    return prefHeight;
  }

  @Override
  public double computeMaxWidth(double height) {
    return maxWidth;
  }

  @Override
  public double computeMaxHeight(double width) {
    return maxHeight;
  }
}
