package org.int4.fx.values.model;

import org.int4.fx.values.domain.DomainSource;

class SimpleStringModel extends ModelBase<String> implements StringModel {

  SimpleStringModel(String initialValue, DomainSource<String> source) {
    super(initialValue, source);
  }

  @Override
  public String get() {
    if(isValid()) {
      return isApplicable() ? getRawValue().orElseThrow() : null;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(String newValue) {
    setValue(newValue);
  }
}
