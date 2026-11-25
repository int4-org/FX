package org.int4.fx.values.model;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.util.Subscription;

import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.core.util.UpdatableValue;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource;
import org.int4.fx.values.domain.DomainSource.DomainTransition;
import org.int4.fx.values.domain.Edit;
import org.int4.fx.values.domain.Membership;

abstract class WritableModelBase<T> extends ObservableModelBase<T> implements WritableModel<T> {
  private final UpdatableValue<DomainTransition<T>> transition = UpdatableValue.of();
  private final UpdatableValue<Boolean> valid = UpdatableValue.of();
  private final UpdatableValue<Boolean> applicable;
  private final UpdatableValue<RawValue<T>> rawValue;

  private DomainSource<T> source;
  private Subscription sourceSubscription = Subscription.EMPTY;

  WritableModelBase(T initialValue, DomainSource<T> source) {
    Objects.requireNonNull(source, "source");

    this.source = source;

    UpdatableValue<Boolean> applicable = UpdatableValue.of();
    UpdatableValue<RawValue<T>> rawValue = UpdatableValue.of();

    super(applicable.asObservableValue(), rawValue.asObservableValue());

    this.applicable = applicable;
    this.rawValue = rawValue;

    Domain<T> initialDomain = source.get();
    RawValue<T> newRawValue = evaluateValue(initialDomain, initialValue);

    UpdatableValue.set(
      transition, new DomainTransition<>(null, initialDomain, new Edit.Replaced<>()),
      applicable, !initialDomain.equals(Domain.inapplicable()),
      valid, determineValidity(initialDomain, newRawValue),
      rawValue, newRawValue
    );

    sourceSubscription = source.transitions(this::onTransition);
  }

  @Override
  public final ObservableValue<Boolean> valid() {
    return valid.asObservableValue();
  }

  @Override
  public final ObservableValue<DomainTransition<T>> transitions() {
    return transition.asObservableValue();
  }

  final void bindSource(DomainSource<T> source) {
    Objects.requireNonNull(source, "source");

    if(source == this.source) {
      return;
    }

    sourceSubscription.unsubscribe();

    this.source = source;
    this.sourceSubscription = source.transitions(this::onTransition);

    updateDomain(source.get());
  }

  @Override
  public final void setValue(T newValue) {
    publish(evaluateValue(getDomain(), newValue));
  }

  @Override
  public <U> boolean trySet(U value, Function<U, T> converter, Template template) {
    Objects.requireNonNull(template, "template");

    RawValue<T> convertedValue = convert(value, converter, template);
    RawValue<T> current = getRawValue();

    if(!Objects.equals(current, convertedValue)) {
      publish(convertedValue);

      return !(convertedValue instanceof RawValue.Incompatible);
    }

    return false;
  }

  final void updateDomain(Domain<T> domain) {
    Objects.requireNonNull(domain, "domain");

    Domain<T> previous = getDomain();

    if(domain == previous) {
      return;  // no change
    }

    RawValue<T> newRawValue = reevaluate(domain);

    UpdatableValue.set(
      transition, new DomainTransition<>(previous, domain, new Edit.Replaced<>()),
      applicable, !domain.equals(Domain.inapplicable()),
      valid, determineValidity(domain, newRawValue),
      rawValue, newRawValue
    );
  }

  private void onTransition(DomainTransition<T> transition) {
    Domain<T> domain = transition.successor();
    RawValue<T> newRawValue = reevaluate(domain);

    UpdatableValue.set(
      this.transition, transition,
      applicable, !domain.equals(Domain.inapplicable()),
      valid, determineValidity(domain, newRawValue),
      rawValue, newRawValue
    );
  }

  private void publish(RawValue<T> newRawValue) {
    UpdatableValue.set(
      valid, determineValidity(getDomain(), newRawValue),
      rawValue, newRawValue
    );
  }

  private RawValue<T> reevaluate(Domain<T> domain) {
    return switch(getRawValue()) {
      case RawValue.Incompatible<T> i -> i;
      case RawValue<T> rv -> evaluateValue(domain, rv.orElseThrow());
    };
  }

  private <U> RawValue<T> convert(U value, Function<U, T> converter, Template template) {
    try {
      return evaluateValue(getDomain(), converter.apply(value));
    }
    catch(Exception e) {
      return RawValue.incompatible(template);
    }
  }

  private RawValue<T> evaluateValue(Domain<T> domain, T newValue) {
    T immutableNewValue = newValue == null ? null : makeImmutable(newValue);

    return switch(domain.evaluate(immutableNewValue)) {
      case Membership.Member() -> RawValue.valid(immutableNewValue);
      case Membership.Excluded(Template reason) -> RawValue.invalid(immutableNewValue, reason);
    };
  }

  private boolean determineValidity(Domain<T> domain, RawValue<T> newRawValue) {
    return domain.equals(Domain.inapplicable()) || newRawValue instanceof RawValue.Valid;
  }

  /**
   * Override this method if a call to {@link WritableModel#setValue(Object)} requires another
   * action to make the value immutable.
   *
   * @param input the input value, not {@code null}
   * @return the adjusted value, not {@code null}
   */
  protected T makeImmutable(T input) {
    return input;
  }
}
