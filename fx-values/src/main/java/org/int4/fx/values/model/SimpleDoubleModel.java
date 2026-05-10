package org.int4.fx.values.model;

import org.int4.fx.core.util.Value;
import org.int4.fx.values.domain.Domain;

class SimpleDoubleModel extends ModelBase<Double> implements DoubleModel {

  SimpleDoubleModel(Double initialValue, Domain<Double> domain) {
    super(initialValue, domain);
  }

  @Override
  public boolean isNull() {
    return switch(getRawValue()) {
      case Value.Present<Double>(Double value) -> value == null;
      default -> false;
    };
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
