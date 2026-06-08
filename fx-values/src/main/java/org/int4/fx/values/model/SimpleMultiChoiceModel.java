package org.int4.fx.values.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.value.ObservableValue;

import org.int4.common.collection.Immutable;
import org.int4.fx.core.util.Template;
import org.int4.fx.core.util.UpdatableValue;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.domain.Rule;

class SimpleMultiChoiceModel<T> extends WritableModelBase<List<T>> implements MultiChoiceModel<T> {
  private final UpdatableValue<Domain<List<T>>> compositeDomain;
  private final UpdatableValue<Domain<T>> elementDomain;

  SimpleMultiChoiceModel(List<T> initialValue, Domain<List<T>> compositeDomain, Domain<T> elementDomain) {
    Objects.requireNonNull(initialValue, "initialValue");
    Objects.requireNonNull(compositeDomain, "compositeDomain");
    Objects.requireNonNull(elementDomain, "elementDomain");

    super(initialValue, createEffectiveDomain(compositeDomain, elementDomain));

    this.compositeDomain = UpdatableValue.of(compositeDomain);
    this.elementDomain = UpdatableValue.of(elementDomain);
  }

  @Override
  public ObservableValue<Domain<List<T>>> compositeDomain() {
    return compositeDomain.asObservableValue();
  }

  @Override
  public void setCompositeDomain(Domain<List<T>> domain) {
    Objects.requireNonNull(domain, "domain");

    UpdatableValue.batch(() -> {
      UpdatableValue.set(compositeDomain, domain);

      updateDomain(createEffectiveDomain(domain, elementDomain.getValue()));
    });
  }

  @Override
  public ObservableValue<Domain<T>> elementDomain() {
    return elementDomain.asObservableValue();
  }

  @Override
  public void setElementDomain(Domain<T> domain) {
    Objects.requireNonNull(domain, "domain");

    UpdatableValue.batch(() -> {
      UpdatableValue.set(elementDomain, domain);

      updateDomain(createEffectiveDomain(compositeDomain.getValue(), domain));
    });
  }

  @Override
  public void add(T item) {
    List<T> newItems = switch(getRawValue()) {
      case RawValue.Valid(List<T> v) -> v == null ? new ArrayList<>() : new ArrayList<>(v);
      case RawValue.Invalid(List<T> v, Template _) -> v == null ? new ArrayList<>() : new ArrayList<>(v);
      case RawValue.Incompatible(Template _) -> new ArrayList<>();
    };

    newItems.add(item);

    setValue(newItems);
  }

  @Override
  public void remove(T item) {
    List<T> newItems = switch(getRawValue()) {
      case RawValue.Valid(List<T> v) -> v == null ? null : new ArrayList<>(v);
      case RawValue.Invalid(List<T> v, Template _) -> v == null ? null : new ArrayList<>(v);
      case RawValue.Incompatible(Template _) -> null;
    };

    if(newItems != null) {
      newItems.remove(item);

      setValue(newItems);
    }
  }

  @Override
  protected List<T> makeImmutable(List<T> input) {
    return Immutable.of(input);
  }

  private static <T> Domain<List<T>> createEffectiveDomain(Domain<List<T>> compositeDomain, Domain<T> elementDomain) {
    if(compositeDomain.equals(Domain.inapplicable()) || elementDomain.equals(Domain.inapplicable())) {
      return Domain.inapplicable();
    }

    Domain<List<T>> domain = Domain.where(new Rule<List<T>>() {
      @Override
      public Membership evaluate(List<T> items) {
        // Check composite domain (e.g. list size)
        Membership compositeMembership = compositeDomain.evaluate(items);

        if(!compositeMembership.isMember()) {
          return compositeMembership;
        }

        // Check each element against the element domain
        for(T item : items) {
          Membership elementMembership = elementDomain.evaluate(item);

          if(!elementMembership.isMember()) {
            return elementMembership;
          }
        }

        return new Membership.Member();
      }
    });

    return compositeDomain.allowsNull() ? domain.nullable() : domain;
  }
}
