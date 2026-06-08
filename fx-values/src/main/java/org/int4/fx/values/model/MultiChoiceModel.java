package org.int4.fx.values.model;

import java.util.List;

import org.int4.fx.values.domain.Domain;

/**
 * A composite model that allows multiple choices to be selected from a domain.
 * <p>
 * This model manages an ordered collection of selected items of type {@code T}.
 * It characterises the validity of the selection through a tiered validation
 * strategy:
 * <ul>
 *   <li><b>Composite Domain</b>: Validates the collective selection (e.g.,
 *       checking if the number of selected items is within a specific range).</li>
 *   <li><b>Element Domain</b>: Validates the individual elements (e.g.,
 *       checking if each selected item is a member of an expected set).</li>
 * </ul>
 * <p>
 * The effective domain of the model is automatically recomputed whenever the
 * composite or element domains are modified. If either the composite or element
 * domain is {@link Domain#inapplicable()}, the model's effective domain also
 * becomes inapplicable.
 * <p>
 * <b>Behavioral Notes:</b>
 * <ul>
 *   <li><b>Permissiveness</b>: This model is permissive. Methods like
 *       {@link #add(Object)} and {@link #setValue(Object)} will always update
 *       the model's state, even if the resulting selection violates the
 *       constraints of the composite or element domains. In such cases, the
 *       model simply becomes {@link #valid() invalid}. Note that {@link #getValue()}
 *       returns {@code null} when the model is invalid or inapplicable.</li>
 *   <li><b>Duplicates</b>: Duplicate items can be added to the selection. This model
 *       does not enforce uniqueness. Whether a selection containing duplicates is
 *       valid depends on the composite domain.</li>
 *   <li><b>Order</b>: The order of elements is preserved as they are added.</li>
 *   <li><b>Incompatible State</b>: If the model is in an {@link RawValue.Incompatible}
 *       state (e.g., due to a failed conversion), calling {@link #add(Object)}
 *       will recover the model by starting a new selection from an empty list.</li>
 *   <li><b>Null Items</b>: {@code null} items can be added. The resulting
 *       validity is determined by the element domain.</li>
 * </ul>
 *
 * @param <T> the type of the elements in the selection
 */
public interface MultiChoiceModel<T> extends CompositeModel<List<T>, T> {

  /**
   * Creates a new {@code MultiChoiceModel} with an unrestricted selection and
   * a non-null element domain.
   *
   * @param <T> the type of the elements
   * @return a new multi-choice model, never {@code null}
   */
  public static <T> MultiChoiceModel<T> of() {
    return of(Domain.nonNull());
  }

  /**
   * Creates a new {@code MultiChoiceModel} with an unrestricted selection and
   * the specified element domain.
   *
   * @param <T> the type of the elements
   * @param elementDomain the domain used to validate individual elements, cannot be {@code null}
   * @return a new multi-choice model, never {@code null}
   * @throws NullPointerException if {@code elementDomain} is {@code null}
   */
  public static <T> MultiChoiceModel<T> of(Domain<T> elementDomain) {
    return of(List.of(), elementDomain);
  }

  /**
   * Creates a new {@code MultiChoiceModel} with the specified initial selection
   * and element domain.
   *
   * @param <T> the type of the elements
   * @param initialValue the initial list of selected items, cannot be {@code null}
   * @param elementDomain the domain used to validate individual elements, cannot be {@code null}
   * @return a new multi-choice model, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <T> MultiChoiceModel<T> of(List<T> initialValue, Domain<T> elementDomain) {
    return of(initialValue, Domain.nonNull(), elementDomain);
  }

  /**
   * Creates a new {@code MultiChoiceModel} with the specified initial selection,
   * composite domain, and element domain.
   *
   * @param <T> the type of the elements
   * @param initialValue the initial list of selected items, cannot be {@code null}
   * @param compositeDomain the domain used to validate the composite selection, cannot be {@code null}
   * @param elementDomain the domain used to validate individual elements, cannot be {@code null}
   * @return a new multi-choice model, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static <T> MultiChoiceModel<T> of(List<T> initialValue, Domain<List<T>> compositeDomain, Domain<T> elementDomain) {
    return new SimpleMultiChoiceModel<>(initialValue, compositeDomain, elementDomain);
  }

  /**
   * Adds an item to the selection.
   * <p>
   * The item is added to the end of the current selection. If the model is
   * currently in an {@link RawValue.Incompatible} state, the selection is
   * reset to an empty list before adding the item.
   *
   * @param item the item to select, may be {@code null}
   */
  void add(T item);

  /**
   * Removes an item from the selection.
   * <p>
   * The first occurrence of the item is removed from the current selection.
   * If the model is in an {@link RawValue.Incompatible} state, this method
   * has no effect.
   *
   * @param item the item to deselect, may be {@code null}
   */
  void remove(T item);

}
