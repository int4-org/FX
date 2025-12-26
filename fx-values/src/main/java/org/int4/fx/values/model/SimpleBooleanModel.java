package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

class SimpleBooleanModel extends ModelBase<Boolean> implements BooleanModel {
  private boolean value;
  private boolean isNull;

  SimpleBooleanModel(Boolean initialValue, Domain<Boolean> domain) {
    super(domain);

    this.value = initialValue == null ? false : initialValue;
    this.isNull = initialValue == null;

    init();
  }

  @Override
  public Boolean getRawValue() {
    return isNull ? null : value;
  }

  @Override
  public Boolean getValue() {
    return isValid() && isApplicable() && !isNull ? value : null;
  }

  @Override
  public void setValue(Boolean newValue) {
    set(newValue == null ? false : newValue, newValue == null);
  }

  @Override
  public boolean isNull() {
    return isNull;
  }

  @Override
  public boolean get() {
    if(isValid()) {
      if(isNull || !isApplicable()) {
        throw new NullValueException(this);
      }

      return value;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(boolean newValue) {
    set(newValue, false);
  }

  private void set(boolean newValue, boolean newIsNull) {
    boolean oldValue = value;
    boolean wasNull = isNull;

    this.value = newValue;
    this.isNull = newIsNull;

    updateValidity(getRawValue());

    if((newValue != oldValue || wasNull != isNull) && isValid()) {
      fireValueChangedEvent();
    }
  }
}
