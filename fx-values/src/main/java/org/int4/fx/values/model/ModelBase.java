package org.int4.fx.values.model;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

import org.int4.fx.core.util.UpdatableValue;
import org.int4.fx.core.util.Value;
import org.int4.fx.values.domain.Domain;

abstract class ModelBase<T> extends ObservableValueBase<T> implements ValueModel<T> {
  private final UpdatableValue<Domain<T>> domain = UpdatableValue.of();
  private final UpdatableValue<Boolean> applicable = UpdatableValue.of();
  private final UpdatableValue<Boolean> valid = UpdatableValue.of();
  private final UpdatableValue<Value<T>> rawValue = UpdatableValue.of();

  ModelBase(T initialValue, Domain<T> initialDomain) {
    Value<T> rv = new Value.Present<>(initialValue);

    UpdatableValue.set(
      domain, initialDomain,
      applicable, !initialDomain.equals(Domain.inapplicable()),
      valid, determineValidity(initialDomain, rv),
      rawValue, rv
    );
  }

  @Override
  public final ObservableValue<Boolean> applicable() {
    return applicable.asObservableValue();
  }

  @Override
  public final ObservableValue<Boolean> valid() {
    return valid.asObservableValue();
  }

  @Override
  public final Value<T> getRawValue() {
    return rawValue.getValue();
  }

  @Override
  public final ObservableValue<Value<T>> rawValue() {
    return rawValue.asObservableValue();
  }

  @Override
  public final T getValue() {
    return isValid() && isApplicable() ? getRawValue().orElse(null) : null;
  }

  @Override
  public final void setValue(T newValue) {
    update(getDomain(), new Value.Present<>(newValue));
  }

  @Override
  public final ObservableValue<Domain<T>> domain() {
    return domain.asObservableValue();
  }

  @Override
  public final void setDomain(Domain<T> domain) {
    Objects.requireNonNull(domain, "domain");

    update(domain, getRawValue());
  }

  @Override
  public <U> boolean trySet(U value, Function<U, T> converter) {
    Value<T> convertedValue = convert(value, converter);
    Value<T> current = getRawValue();

    if(!Objects.equals(current, convertedValue)) {
      update(getDomain(), convertedValue);

      return convertedValue.isPresent();
    }

    return false;
  }

  private <U> Value<T> convert(U value, Function<U, T> converter) {
    try {
      return Value.present(converter.apply(value));
    }
    catch(Exception e) {
      return Value.absent();
    }
  }

  private void update(Domain<T> newDomain, Value<T> newValue) {
    T oldValue = getValue();

    UpdatableValue.set(
      domain, newDomain,
      applicable, !newDomain.equals(Domain.inapplicable()),
      valid, determineValidity(newDomain, newValue),
      rawValue, newValue
    );

    if(!Objects.equals(getValue(), oldValue)) {
      fireValueChangedEvent();
    }
  }

  private boolean determineValidity(Domain<T> domain, Value<T> newValue) {
    return domain.equals(Domain.inapplicable()) || (newValue instanceof Value.Present<T>(T value) && domain.contains(value));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + (isApplicable() ? Objects.toString(getValue()) : "not applicable") + "]";
  }
}
