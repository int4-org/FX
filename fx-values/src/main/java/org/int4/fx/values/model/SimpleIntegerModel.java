package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

class SimpleIntegerModel extends ModelBase<Integer> implements IntegerModel {
  private int value;
  private boolean isNull;

  SimpleIntegerModel(Integer initialValue, Domain<Integer> domain) {
    super(domain);

    this.value = initialValue == null ? 0 : initialValue;
    this.isNull = initialValue == null;

    init();
  }

  @Override
  public Integer getRawValue() {
    return isNull ? null : value;
  }

  @Override
  public Integer getValue() {
    return isValid() && isApplicable() && !isNull ? value : null;
  }

  @Override
  public void setValue(Integer newValue) {
    set(newValue == null ? 0 : newValue, newValue == null);
  }

  @Override
  public boolean isNull() {
    return isNull;
  }

  @Override
  public int get() {
    if(isValid()) {
      if(isNull || !isApplicable()) {
        throw new NullValueException(this);
      }

      return value;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(int newValue) {
    set(newValue, false);
  }

  private void set(int newValue, boolean newIsNull) {
    int oldValue = value;
    boolean wasNull = isNull;

    this.value = newValue;
    this.isNull = newIsNull;

    updateValidity(getRawValue());

    if((value != oldValue || wasNull != isNull) && isValid()) {
      fireValueChangedEvent();
    }
  }
}
