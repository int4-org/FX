package org.int4.fx.values.model;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource;

/**
 * A writable model whose domain source can be replaced.
 * <p>
 * This is the type returned to the creator of a model: it can be re-bound to a
 * different {@link DomainSource} with {@link #setSource}, or to a plain domain
 * with {@link #setDomain}. The previous source is left untouched, so other
 * models still following it are unaffected.
 * <p>
 * Code that only observes or writes the value should accept a
 * {@link WritableModel} instead, so it cannot replace the domain.
 *
 * @param <T> the type of the stored value and the domain element
 */
public interface Model<T> extends WritableModel<T> {

  /**
   * Replaces the domain source this model follows.
   * <p>
   * The current source is unsubscribed and the model adopts the domain of the
   * new source, publishing that change as a transition with an
   * {@link org.int4.fx.values.domain.Edit.Replaced} edit. Subsequent
   * transitions from the new source are published as-is. The new source is
   * adopted without publishing a transition if it already has the same domain
   * instance.
   *
   * @param source the new domain source, cannot be {@code null}
   * @throws NullPointerException if {@code source} is {@code null}
   */
  void setSource(DomainSource<T> source);

  /**
   * Replaces the domain that constrains values for this model.
   * <p>
   * This is a convenience for {@code setSource(DomainSources.of(domain))}: the
   * model stops following its previous source, which remains unaffected.
   * Replacing the domain may affect the validity of the current value.
   *
   * @param domain the new domain, cannot be {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  void setDomain(Domain<T> domain);
}
