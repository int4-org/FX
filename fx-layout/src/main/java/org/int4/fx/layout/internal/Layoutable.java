package org.int4.fx.layout.internal;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public interface Layoutable extends Measurable {
  boolean isResizable();
  void resizeRelocate(double x, double y, double w, double h);
  void relocate(double x, double y);
  Bounds getLayoutBounds();

  <T> T getConstraint(ConstraintKey<T> key, T defaultValue);
  <T> void setConstraint(ConstraintKey<T> key, T value);

  /*
   * Below stuff is not needed if Node were to implement Layoutable:
   */

  @SuppressWarnings("unchecked")
  default <T> T unwrap(Class<T> cls) {
    if(cls.isInstance(this)) {
      return (T)this;
    }

    if(this instanceof NodeLayoutable nl && cls.isInstance(nl.n)) {
      return (T)nl.n;
    }

    return null;
  }

  static Layoutable of(Node n) {
    return new NodeLayoutable(n);
  }
}