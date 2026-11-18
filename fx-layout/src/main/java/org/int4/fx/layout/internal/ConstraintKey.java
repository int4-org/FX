package org.int4.fx.layout.internal;

public class ConstraintKey<T> {
  private final Class<T> cls;

  public static <T> ConstraintKey<T> of(Class<T> cls) {
    return new ConstraintKey<>(cls);
  }

  private ConstraintKey(Class<T> cls) {
    this.cls = cls;
  }

  @Override
  public String toString() {
    return "ConstraintKey[" + cls + "]";
  }
}
