package org.int4.fx.values.model;

import org.int4.fx.values.domain.DomainSource;

class SimpleIntegerModel extends ModelBase<Integer> implements IntegerModel {

  SimpleIntegerModel(Integer initialValue, DomainSource<Integer> source) {
    super(initialValue, source);
  }

  @Override
  public boolean isNull() {
    return getRawValue().isNull();
  }

  @Override
  public int get() {
    if(isValid()) {
      if(isNull() || !isApplicable()) {
        throw new NullValueException(this);
      }

      return getRawValue().orElseThrow();
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(int newValue) {
    setValue(newValue);
  }
}
