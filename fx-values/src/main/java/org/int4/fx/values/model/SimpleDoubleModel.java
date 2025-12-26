package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

class SimpleDoubleModel extends ModelBase<Double> implements DoubleModel {
  private double value;
  private boolean isNull;

  SimpleDoubleModel(Double initialValue, Domain<Double> domain) {
    super(domain);

    this.value = initialValue == null ? 0 : initialValue;
    this.isNull = initialValue == null;

    init();
  }

  @Override
  public Double getRawValue() {
    return isNull ? null : value;
  }

  @Override
  public Double getValue() {
    return isValid() && isApplicable() && !isNull ? value : null;
  }

  @Override
  public void setValue(Double newValue) {
    set(newValue == null ? 0.0 : newValue, newValue == null);
  }

  @Override
  public boolean isNull() {
    return isNull;
  }

  @Override
  public double get() {
    if(isValid()) {
      if(isNull || !isApplicable()) {
        throw new NullValueException(this);
      }

      return value;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(double newValue) {
    set(newValue, false);
  }

  private void set(double newValue, boolean newIsNull) {
    double oldValue = value;
    boolean wasNull = isNull;

    this.value = newValue;
    this.isNull = newIsNull;

    updateValidity(getRawValue());

    if((value != oldValue || wasNull != isNull) && isValid()) {
      fireValueChangedEvent();
    }
  }
}
