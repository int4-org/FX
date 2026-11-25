package org.int4.fx.values.model;

import org.int4.fx.values.domain.DomainSource;

class SimpleDoubleModel extends ModelBase<Double> implements DoubleModel {

  SimpleDoubleModel(Double initialValue, DomainSource<Double> source) {
    super(initialValue, source);
  }

  @Override
  public boolean isNull() {
    return getRawValue().isNull();
  }

  @Override
  public double get() {
    if(isValid()) {
      if(isNull() || !isApplicable()) {
        throw new NullValueException(this);
      }

      return getRawValue().orElseThrow();
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(double newValue) {
    setValue(newValue);
  }
}
