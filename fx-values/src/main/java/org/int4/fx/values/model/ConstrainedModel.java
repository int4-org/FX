package org.int4.fx.values.model;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;

import org.int4.fx.core.util.Value;
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
    return domain().getValue();
  }

  /**
   * Sets the domain that constrains values for this model.
   * <p>
   * Changing the domain may affect the validity of the current value.
   *
   * @param domain the new domain, cannot be {@code null}
   */
  void setDomain(Domain<E> domain);

  /**
   * The observable domain value of this model.
   * <p>
   * Changes to the domain may affect the validity of the current value.
   *
   * @return the domain {@link ObservableValue}, never {@code null}
   */
  ObservableValue<Domain<E>> domain();

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
   * <p>
   * A model is typically considered valid if it is not applicable, or if
   * its current value satisfies the associated domain and conversion
   * constraints.
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
   * The provided conversion function may throw an exception.
   * <p>
   * If conversion succeeds and the model's value changes as a result, the
   * method returns {@code true}.
   * <p>
   * If the conversion fails (by throwing an exception), the model is marked
   * as invalid due to unconvertible input, its {@link #getRawValue()} becomes
   * {@link org.int4.fx.core.util.Value.Absent}, and the method returns {@code false}.
   * <p>
   * Conversion success does not imply validity; validity is determined
   * independently by the model, based on the current domain and other
   * constraints. Therefore, a {@code false} return value does not
   * necessarily mean the model is invalid; it may also mean the value
   * was unchanged.
   *
   * @param <T> the source value type
   * @param value the value to convert, may be {@code null}
   * @param converter a conversion function producing a model value, cannot be
   *   {@code null}; may throw an exception
   * @return {@code true} if the conversion succeeded and the value was updated,
   *   otherwise {@code false} if conversion failed or the value was unchanged
   * @throws NullPointerException if {@code converter} is {@code null}
   */
  <T> boolean trySet(T value, Function<T, M> converter);

  /**
   * Returns the raw value currently stored by this model.
   * <p>
   * The returned {@link Value} can be {@link org.int4.fx.core.util.Value.Present} or {@link org.int4.fx.core.util.Value.Absent}:
   * <ul>
   *   <li>When present, the value is the stored value, regardless of applicability or
   *       validity, and may be {@code null}.</li>
   *   <li>When absent, the model is in an error state representing a failure to
   *       convert an input value to the required type (e.g., via {@link #trySet(Object, Function)}).</li>
   * </ul>
   *
   * @return the raw value, never {@code null}
   */
  default Value<M> getRawValue() {
    return rawValue().getValue();
  }

  /**
   * An observable representing the raw value currently stored by this model.
   * <p>
   * This observable provides access to the underlying value even when the model
   * is invalid or not applicable.
   *
   * @return an observable raw value, never {@code null}
   */
  ObservableValue<Value<M>> rawValue();

  /**
   * Returns the current value of the model if it is both applicable and valid.
   * <p>
   * This method returns {@code null} if:
   * <ul>
   *   <li>The model is not {@link #isApplicable()}</li>
   *   <li>The model is {@link #isInvalid()}</li>
   *   <li>The stored value is actually {@code null}</li>
   * </ul>
   * Use {@link #getRawValue()} to access the underlying stored value
   * unconditionally.
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
