package org.int4.fx.values.model;

import org.int4.fx.values.domain.DomainSource;

class SimpleChoiceModel<T> extends ModelBase<T> implements ChoiceModel<T> {

  SimpleChoiceModel(T initialValue, DomainSource<T> source) {
    super(initialValue, source);
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
