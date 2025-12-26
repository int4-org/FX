package org.int4.fx.values.model;

import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;

import org.int4.fx.values.domain.Domain;

/**
 * A stateful, observable model whose value is constrained by a {@link Domain}
 * and whose validity and applicability are explicitly exposed.
 * <p>
 * A {@code ConstrainedModel} owns a current value of type {@code M} and
 * governs whether that value is considered valid according to a domain of
 * values of type {@code E}. The model is observable and intended to integrate
 * directly with UI components and validation logic.
 * <p>
 * In addition to validity, a model may be <em>applicable</em>, allowing UI
 * logic to distinguish between values that are temporarily irrelevant and
 * values that are actively invalid.
 * <p>
 * This interface extends {@link ObservableValue}, allowing clients to listen
 * for changes to the model's value.
 * <p>
 * <strong>Value semantics:</strong>
 * <ul>
 *   <li>{@link #getValue()} may return {@code null} if the model is not
 *       applicable or currently invalid. Use {@link #getRawValue()} to
 *       access the underlying stored value unconditionally.</li>
 *   <li>Setting a value via {@link #setValue(Object)} or {@link #trySet(Object, java.util.function.Function)}
 *       may update the stored value and trigger listener notifications.</li>
 * </ul>
 *
 * @param <M> the internal value type stored by this model
 * @param <E> the external value type governed by the domain
 */
public interface ConstrainedModel<M, E> extends ObservableValue<M> {

  /**
   * Returns the domain that constrains values for this model.
   *
   * @return the current domain, never {@code null}
   */
  default Domain<E> getDomain() {
    return domainProperty().get();
  }

  /**
   * Sets the domain that constrains values for this model.
   *
   * @param domain the new domain, cannot be {@code null}
   */
  default void setDomain(Domain<E> domain) {
    domainProperty().set(domain);
  }

  /**
   * The observable domain property of this model.
   * <p>
   * Changes to the domain may affect the validity of the current value.
   *
   * @return the domain property, never {@code null}
   */
  ObjectProperty<Domain<E>> domainProperty();

  /**
   * Returns {@code true} if this model is currently applicable.
   * <p>
   * Applicability indicates whether the model should participate in validation
   * and UI logic. A non-applicable model is neither valid nor invalid in a
   * semantic sense.
   *
   * @return {@code true} if this model is applicable
   */
  default boolean isApplicable() {
    return applicable().getValue();
  }

  /**
   * An observable indicating whether this model is currently applicable.
   *
   * @return an observable applicability flag, never {@code null}
   */
  ObservableValue<Boolean> applicable();

  /**
   * Returns {@code true} if this model is currently invalid.
   *
   * @return {@code true} if the model is invalid
   */
  default boolean isInvalid() {
    return !isValid();
  }

  /**
   * An observable indicating whether this model is currently invalid.
   * <p>
   * This observable is the logical negation of {@link #valid()}.
   *
   * @return an observable invalid flag, never {@code null}
   */
  default ObservableValue<Boolean> invalid() {
    return valid().map(v -> !v);
  }

  /**
   * Returns {@code true} if this model is currently valid.
   *
   * @return {@code true} if the model is valid
   */
  default boolean isValid() {
    return valid().getValue();
  }

  /**
   * An observable indicating whether this model is currently valid.
   * <p>
   * Validity reflects whether the model's current value satisfies the
   * associated domain and conversion constraints.
   *
   * @return an observable validity flag, never {@code null}
   */
  ObservableValue<Boolean> valid();

  /**
   * Attempts to set this model's value using the supplied converter.
   * <p>
   * The provided conversion function may throw an exception. If the conversion
   * succeeds, the model's value is updated and the method returns {@code true}.
   * If the conversion fails by throwing an exception, the model remains unchanged,
   * is marked as invalid due to unconvertible input, and the method returns {@code false}.
   * <p>
   * Conversion success does not imply validity; validity is determined independently
   * by the model, based on the current domain and other constraints.
   *
   * @param <T> the source value type
   * @param value the value to convert, may be {@code null}
   * @param converter a conversion function producing a model value, cannot be
   *   {@code null}; may throw an exception
   * @return {@code true} if the conversion succeeded and the value was updated,
   *   otherwise {@code false} if conversion failed
   * @throws NullPointerException if {@code converter} is {@code null}
   */
  <T> boolean trySet(T value, Function<T, M> converter);

  /**
   * Returns the raw value currently stored by this model.
   * <p>
   * This method unconditionally accesses the stored value, regardless of
   * applicability or validity.
   *
   * @return the stored value, may be {@code null}
   */
  M getRawValue();

  /**
   * Returns the current value of the model, or {@code null} if the model
   * is not applicable or currently invalid. Use {@link #getRawValue()}
   * to access the underlying stored value unconditionally.
   *
   * @return the current value of the model, or {@code null} if the model
   *   is not applicable or currently invalid
   */
  @Override
  M getValue();

  /**
   * Sets the stored value of this model.
   * <p>
   * The supplied value becomes the model's current value and may affect
   * validity and observability.
   *
   * @param newValue the new value to store
   */
  void setValue(M newValue);
}
