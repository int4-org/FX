package org.int4.fx.values.model;

import org.int4.fx.values.domain.DomainSource;

class SimpleLongModel extends ModelBase<Long> implements LongModel {

  SimpleLongModel(Long initialValue, DomainSource<Long> source) {
    super(initialValue, source);
  }

  @Override
  public boolean isNull() {
    return getRawValue().isNull();
  }

  @Override
  public long get() {
    if(isValid()) {
      if(isNull() || !isApplicable()) {
        throw new NullValueException(this);
      }

      return getRawValue().orElseThrow();
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(long newValue) {
    setValue(newValue);
  }
}
