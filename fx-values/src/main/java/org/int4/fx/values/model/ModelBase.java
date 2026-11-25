package org.int4.fx.values.model;

import java.util.Objects;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource;
import org.int4.fx.values.domain.DomainSources;

abstract class ModelBase<T> extends WritableModelBase<T> implements Model<T> {

  ModelBase(T initialValue, DomainSource<T> source) {
    super(initialValue, source);
  }

  @Override
  public final void setSource(DomainSource<T> source) {
    bindSource(source);
  }

  @Override
  public final void setDomain(Domain<T> domain) {
    Objects.requireNonNull(domain, "domain");

    bindSource(DomainSources.of(domain));
  }
}
