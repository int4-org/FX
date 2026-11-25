package org.int4.fx.values.model;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;

import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource;

/**
 * A {@code WritableModel} extends {@link ObservableModel} with support for
 * validation and value mutation, while exposing a read-only view of its
 * associated {@link Domain}.
 * <p>
 * Use {@link Model} when the model must also be able to replace the domain
 * source it follows.
 * <p>
 * <strong>Value semantics:</strong>
 * <ul>
 *   <li>{@link #getValue()} returns {@code null} if the model is not
 *       applicable, currently invalid, or if the stored value is
 *       actually {@code null}.</li>
 *   <li>Setting a value via {@link #setValue(Object)} or {@link #trySet(Object, Function, Template)}
 *       updates the stored value and triggers listener notifications.</li>
 * </ul>
 *
 * @param <T> the internal value type stored by this model
 */
public interface WritableModel<T> extends ObservableModel<T> {

  /**
   * Returns the domain that constrains values for this model.
   *
   * @return the current domain, never {@code null}
   */
  default Domain<T> getDomain() {
    return transitions().getValue().successor();
  }

  /**
   * The observable domain transitions of this model.
   * <p>
   * The value is published atomically with the model's value, validity and
   * applicability, so observers always see a consistent snapshot. The initial
   * value is a transition from {@code null} to the model's initial domain with
   * an {@link org.int4.fx.values.domain.Edit.Replaced} edit.
   * <p>
   * Changes to the domain may affect the validity of the current value.
   *
   * @return an observable value containing the last domain transition, never {@code null}
   */
  ObservableValue<DomainSource.DomainTransition<T>> transitions();

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
   * @return an observable value indicating whether the model is currently invalid, never {@code null}
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
   * @return an observable value indicating whether the model is currently valid, never {@code null}
   */
  ObservableValue<Boolean> valid();

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
  T getValue();

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
   * {@link org.int4.fx.core.util.RawValue.Incompatible}, and the method returns {@code false}.
   * <p>
   * Conversion success does not imply validity; validity is determined
   * independently by the model, based on the current domain and other
   * constraints. Therefore, a {@code false} return value does not
   * necessarily mean the model is invalid; it may also mean the value
   * was unchanged.
   *
   * @param <S> the source value type
   * @param value the value to convert, may be {@code null}
   * @param converter a conversion function producing a model value, cannot be
   *   {@code null}; may throw an exception
   * @param template a template, cannot be {@code null}
   * @return {@code true} if the conversion succeeded and the value was updated,
   *   otherwise {@code false} if conversion failed or the value was unchanged
   * @throws NullPointerException if {@code converter} is {@code null}
   */
  <S> boolean trySet(S value, Function<S, T> converter, Template template);

  /**
   * Sets the stored value of this model.
   * <p>
   * The supplied value becomes the model's current value and its validity
   * is recomputed against the current domain.
   *
   * @param newValue the new value to store
   */
  void setValue(T newValue);
}
