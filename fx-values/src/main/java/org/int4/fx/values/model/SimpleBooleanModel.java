package org.int4.fx.values.model;

import org.int4.fx.values.domain.DomainSource;

class SimpleBooleanModel extends ModelBase<Boolean> implements BooleanModel {

  SimpleBooleanModel(Boolean initialValue, DomainSource<Boolean> source) {
    super(initialValue, source);
  }

  @Override
  public boolean isNull() {
    return getRawValue().isNull();
  }

  @Override
  public boolean get() {
    if(isValid()) {
      if(isNull() || !isApplicable()) {
        throw new NullValueException(this);
      }

      return getRawValue().orElseThrow();
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(boolean newValue) {
    setValue(newValue);
  }
}
