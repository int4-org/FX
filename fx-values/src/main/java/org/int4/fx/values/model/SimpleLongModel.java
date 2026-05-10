package org.int4.fx.values.model;

import org.int4.fx.core.util.Value;
import org.int4.fx.values.domain.Domain;

class SimpleLongModel extends ModelBase<Long> implements LongModel {

  SimpleLongModel(Long initialValue, Domain<Long> domain) {
    super(initialValue, domain);
  }

  @Override
  public boolean isNull() {
    return switch(getRawValue()) {
      case Value.Present<Long>(Long value) -> value == null;
      default -> false;
    };
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
