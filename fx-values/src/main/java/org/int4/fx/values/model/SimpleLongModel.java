package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

class SimpleLongModel extends ModelBase<Long> implements LongModel {
  private long value;
  private boolean isNull;

  SimpleLongModel(Long initialValue, Domain<Long> domain) {
    super(domain);

    this.value = initialValue == null ? 0 : initialValue;
    this.isNull = initialValue == null;

    init();
  }

  @Override
  public Long getRawValue() {
    return isNull ? null : value;
  }

  @Override
  public Long getValue() {
    return isValid() && isApplicable() && !isNull ? value : null;
  }

  @Override
  public void setValue(Long newValue) {
    set(newValue == null ? 0L : newValue, newValue == null);
  }

  @Override
  public boolean isNull() {
    return isNull;
  }

  @Override
  public long get() {
    if(isValid()) {
      if(isNull || !isApplicable()) {
        throw new NullValueException(this);
      }

      return value;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(long newValue) {
    set(newValue, false);
  }

  private void set(long newValue, boolean newIsNull) {
    long oldValue = value;
    boolean wasNull = isNull;

    this.value = newValue;
    this.isNull = newIsNull;

    updateValidity(getRawValue());

    if((value != oldValue || wasNull != isNull) && isValid()) {
      fireValueChangedEvent();
    }
  }
}
