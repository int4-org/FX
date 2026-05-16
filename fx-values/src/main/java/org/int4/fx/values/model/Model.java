package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;

/**
 * A writable model whose domain can be modified.
 *
 * @param <T> the type of the stored value and the domain element
 */
public interface Model<T> extends WritableModel<T> {

  /**
   * Sets the domain that constrains values for this model.
   * <p>
   * Changing the domain may affect the validity of the current value.
   *
   * @param domain the new domain, cannot be {@code null}
   */
  void setDomain(Domain<T> domain);
}
