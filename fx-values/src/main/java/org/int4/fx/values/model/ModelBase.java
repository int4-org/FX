package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

abstract class ModelBase<T> extends WritableModelBase<T> implements Model<T> {

  ModelBase(T initialValue, Domain<T> initialDomain) {
    super(initialValue, initialDomain);
  }

  @Override
  public final void setDomain(Domain<T> domain) {
    super.updateDomain(domain);
  }
}
