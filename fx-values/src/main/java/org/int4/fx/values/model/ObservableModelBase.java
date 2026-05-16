package org.int4.fx.values.model;

import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

import org.int4.fx.core.util.Observe;

class ObservableModelBase<T> extends ObservableValueBase<T> implements ObservableModel<T> {
  private final ObservableValue<Boolean> applicable;
  private final ObservableValue<RawValue<T>> rawValue;

  ObservableModelBase(ObservableValue<Boolean> applicable, ObservableValue<RawValue<T>> rawValue) {
    this.applicable = applicable;
    this.rawValue = rawValue;

    Observe.values(applicable, rawValue).subscribe(this::fireValueChangedEvent);
  }

  @Override
  public ObservableValue<Boolean> applicable() {
    return applicable;
  }

  @Override
  public ObservableValue<RawValue<T>> rawValue() {
    return rawValue;
  }

  @Override
  public T getValue() {
    return switch(getRawValue()) {
      case RawValue.Valid(T value) when isApplicable() -> value;
      default -> null;
    };
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + (isApplicable() ? Objects.toString(getValue()) : "not applicable") + "]";
  }
}
