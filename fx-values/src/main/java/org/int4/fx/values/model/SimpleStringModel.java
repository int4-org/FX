package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

class SimpleStringModel extends ModelBase<String> implements StringModel {

  SimpleStringModel(String initialValue, Domain<String> domain) {
    super(initialValue, domain);
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
