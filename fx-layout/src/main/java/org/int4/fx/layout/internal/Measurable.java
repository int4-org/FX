package org.int4.fx.layout.internal;

import javafx.geometry.Orientation;

public interface Measurable {
  double minWidth(double height);
  double prefWidth(double height);
  double maxWidth(double height);

  double minHeight(double width);
  double prefHeight(double width);
  double maxHeight(double width);

  double getBaselineOffset();

  Orientation getContentBias();
}
