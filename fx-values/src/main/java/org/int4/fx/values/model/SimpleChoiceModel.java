package org.int4.fx.values.model;

import java.util.Objects;

import org.int4.fx.values.domain.Domain;

class SimpleChoiceModel<T> extends ModelBase<T> implements ChoiceModel<T> {
  private T value;

  SimpleChoiceModel(T initialValue, Domain<T> domain) {
    super(domain);

    this.value = initialValue;

    init();
  }

  @Override
  public T getRawValue() {
    return value;
  }

  @Override
  public T getValue() {
    return isValid() && isApplicable() ? value : null;
  }

  @Override
  public void setValue(T newValue) {
    set(newValue);
  }

  @Override
  public T get() {
    if(isValid()) {
      return isApplicable() ? value : null;
    }

    throw new InvalidValueException(this);
  }

  @Override
  public void set(T newValue) {
    T oldValue = value;

    this.value = newValue;

    updateValidity(newValue);

    if(!Objects.equals(value, oldValue) && isValid()) {
      fireValueChangedEvent();
    }
  }
}
