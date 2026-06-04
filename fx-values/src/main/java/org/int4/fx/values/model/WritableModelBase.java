package org.int4.fx.values.model;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;

import org.int4.fx.core.util.Template;
import org.int4.fx.core.util.UpdatableValue;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;

abstract class WritableModelBase<T> extends ObservableModelBase<T> implements WritableModel<T> {
  private final UpdatableValue<Domain<T>> domain = UpdatableValue.of();
  private final UpdatableValue<Boolean> valid = UpdatableValue.of();
  private final UpdatableValue<Boolean> applicable;
  private final UpdatableValue<RawValue<T>> rawValue;

  WritableModelBase(T initialValue, Domain<T> initialDomain) {
    UpdatableValue<Boolean> applicable = UpdatableValue.of();
    UpdatableValue<RawValue<T>> rawValue = UpdatableValue.of();

    super(applicable.asObservableValue(), rawValue.asObservableValue());

    this.applicable = applicable;
    this.rawValue = rawValue;

    RawValue<T> newRawValue = evaluateValue(initialDomain, initialValue);

    UpdatableValue.set(
      domain, initialDomain,
      applicable, !initialDomain.equals(Domain.inapplicable()),
      valid, determineValidity(initialDomain, newRawValue),
      rawValue, newRawValue
    );
  }

  @Override
  public final ObservableValue<Boolean> valid() {
    return valid.asObservableValue();
  }

  @Override
  public final void setValue(T newValue) {
    update(getDomain(), newValue);
  }

  @Override
  public final ObservableValue<Domain<T>> domain() {
    return domain.asObservableValue();
  }

  final void updateDomain(Domain<T> domain) {
    Objects.requireNonNull(domain, "domain");

    update(
      domain,
      switch(getRawValue()) {
        case RawValue.Incompatible<T> i -> i;
        case RawValue<T> rv -> evaluateValue(domain, rv.orElseThrow());
      }
    );
  }

  @Override
  public <U> boolean trySet(U value, Function<U, T> converter, Template template) {
    Objects.requireNonNull(template, "template");

    RawValue<T> convertedValue = convert(value, converter, template);
    RawValue<T> current = getRawValue();

    if(!Objects.equals(current, convertedValue)) {
      update(getDomain(), convertedValue);

      return !(convertedValue instanceof RawValue.Incompatible);
    }

    return false;
  }

  private <U> RawValue<T> convert(U value, Function<U, T> converter, Template template) {
    try {
      return evaluateValue(getDomain(), converter.apply(value));
    }
    catch(Exception e) {
      return RawValue.incompatible(template);
    }
  }

  private void update(Domain<T> newDomain, T newValue) {
    update(newDomain, evaluateValue(newDomain, newValue));
  }

  private void update(Domain<T> newDomain, RawValue<T> newRawValue) {
    UpdatableValue.set(
      domain, newDomain,
      applicable, !newDomain.equals(Domain.inapplicable()),
      valid, determineValidity(newDomain, newRawValue),
      rawValue, newRawValue
    );
  }

  private RawValue<T> evaluateValue(Domain<T> domain, T newValue) {
    if(domain.equals(Domain.inapplicable())) {
      return RawValue.valid(newValue);
    }

    return switch(domain.evaluate(newValue)) {
      case Membership.Member() -> RawValue.valid(newValue);
      case Membership.Excluded(Template reason) -> RawValue.invalid(newValue, reason);
    };
  }

  private boolean determineValidity(Domain<T> domain, RawValue<T> newRawValue) {
    return domain.equals(Domain.inapplicable()) || newRawValue instanceof RawValue.Valid;
  }
}
