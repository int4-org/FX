package org.int4.fx.values.model;

import java.util.Objects;

import org.int4.fx.values.domain.Domain;

class SimpleStringModel extends ModelBase<String> implements StringModel {
  private String value;

  SimpleStringModel(String initialValue, Domain<String> domain) {
    super(domain);

    this.value = initialValue;

    init();
  }

  @Override
  public String getRawValue() {
    return value;
  }

  @Override
  public String getValue() {
    return isValid() && isApplicable() ? value : null;
  }

  @Override
  public void setValue(String newValue) {
    set(newValue);
  }

  @Override
  public String get() {
    if(isValid()) {
      return isApplicable() ? value : null;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(String newValue) {
    String oldValue = value;

    this.value = newValue;

    updateValidity(newValue);

    if(!Objects.equals(value, oldValue) && isValid()) {
      fireValueChangedEvent();
    }
  }
}
