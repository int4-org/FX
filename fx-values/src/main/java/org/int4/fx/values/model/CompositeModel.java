package org.int4.fx.values.model;

import javafx.beans.value.ObservableValue;

import org.int4.fx.values.domain.Domain;

/**
 * A specialized model that manages a composite value composed of multiple elements.
 * <p>
 * A {@code CompositeModel} bridges the gap between a composite value of type {@code T}
 * (the "Whole") and its constituent elements of type {@code E} (the "Parts"). It
 * characterizes the validity of the composite value through a tiered validation
 * strategy:
 * <ul>
 *   <li><b>Composite Domain</b>: Constrains the collective value (e.g., ensuring
 *       a comma-separated string adheres to specific formatting or length rules).</li>
 *   <li><b>Element Domain</b>: Constrains the individual elements (e.g., ensuring
 *       each ID extracted from a list is a positive integer).</li>
 *   <li><b>Effective Domain</b>: A read-only, derived domain of type {@code T} (exposed
 *       via {@link #domain()}) that joins both the composite and element constraints.</li>
 * </ul>
 * <p>
 * The effective domain is managed internally by the model and is automatically
 * recomputed whenever the composite or element domains are modified. A value
 * is considered valid only if it satisfies the composite constraints <em>and</em>
 * its interpreted elements satisfy the element constraints.
 * <p>
 * <b>Typical Use Cases:</b>
 * <ul>
 *   <li><b>Multi-selection</b>: A {@code CompositeModel<List<E>, E>} where the
 *       value is a list of selected items, and elements are the individual choices.</li>
 *   <li><b>Encoded Strings</b>: A {@code CompositeModel<String, Integer>} where
 *       the value is a comma-separated string representing a set of integers.</li>
 * </ul>
 *
 * @param <T> the type of the composite value stored by this model
 * @param <E> the type of the individual elements
 */
public interface CompositeModel<T, E> extends WritableModel<T> {

  /**
   * Returns the current composite domain.
   * <p>
   * This domain defines constraints over the composite value of type {@code T}.
   *
   * @return the composite domain, never {@code null}
   */
  default Domain<T> getCompositeDomain() {
    return compositeDomain().getValue();
  }

  /**
   * An observable representing the domain that constrains the composite value.
   * <p>
   * Changes to this domain will trigger recomputation of the effective model
   * {@link #domain()}.
   *
   * @return an observable composite domain, never {@code null}
   */
  ObservableValue<Domain<T>> compositeDomain();

  /**
   * Sets the domain used to validate the composite value.
   *
   * @param domain the new composite domain, cannot be {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  void setCompositeDomain(Domain<T> domain);

  /**
   * Returns the current element domain.
   * <p>
   * This domain defines constraints over the individual elements of type {@code E}
   * that compose the composite value.
   *
   * @return the element domain, never {@code null}
   */
  default Domain<E> getElementDomain() {
    return elementDomain().getValue();
  }

  /**
   * An observable representing the domain that constrains individual elements.
   * <p>
   * Changes to this domain will trigger recomputation of the effective model
   * {@link #domain()}.
   *
   * @return an observable element domain, never {@code null}
   */
  ObservableValue<Domain<E>> elementDomain();

  /**
   * Sets the domain used to validate individual elements.
   *
   * @param domain the new element domain, cannot be {@code null}
   * @throws NullPointerException if {@code domain} is {@code null}
   */
  void setElementDomain(Domain<E> domain);
}
