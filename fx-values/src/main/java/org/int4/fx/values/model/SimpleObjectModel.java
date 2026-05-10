package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

class SimpleObjectModel<T> extends ModelBase<T> implements ObjectModel<T> {

  SimpleObjectModel(T initialValue, Domain<T> domain) {
    super(initialValue, domain);
  }

  @Override
  public T get() {
    if(isValid()) {
      return isApplicable() ? getRawValue().orElseThrow() : null;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(T newValue) {
    setValue(newValue);
  }
}
