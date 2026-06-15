package org.int4.fx.values.model;

import javafx.beans.value.ObservableValue;

import org.int4.fx.core.util.RawValue;

/**
 * A stateful, observable model that exposes a current value and its applicability.
 * <p>
 * An {@code ObservableModel} owns a current value of type {@code T}.
 * <p>
 * A model may be <em>applicable</em>, allowing distinguishing between values that are
 * temporarily irrelevant and values that are contextually valid.
 * <p>
 * This interface extends {@link ObservableValue}, allowing clients to listen
 * for changes to the model's value.
 * <p>
 * <strong>Value semantics:</strong>
 * <ul>
 *   <li>{@link #getValue()} returns the model’s semantic value.</li>
 *   <li>{@code null} may be returned either because:
 *     <ul>
 *       <li>the model is not applicable, or</li>
 *       <li>the stored value is explicitly {@code null}</li>
 *     </ul>
 *   </li>
 *   <li>Validity constraints are not part of this type; a {@code null} value
 *       does not indicate any validity state.</li>
 *   <li>Use {@link #getRawValue()} to distinguish between absence and an
 *       explicit {@code null} value.</li>
 * </ul>
 *
 * @param <T> the internal value type stored by this model
 */
public interface ObservableModel<T> extends ObservableValue<T> {

  /**
   * Returns {@code true} if this model is currently applicable.
   *
   * @return {@code true} if this model is currently applicable, otherwise {@code false}
   */
  default boolean isApplicable() {
    return applicable().getValue();
  }

  /**
   * An observable value indicating whether this model is currently applicable.
   * <p>
   * Applicability indicates whether the model is currently active in its context.
   * A non-applicable model has no validity semantics (neither valid nor invalid).
   *
   * @return an observable value indicating whether this model is currently applicable, never {@code null}
   */
  ObservableValue<Boolean> applicable();

  /**
   * Returns the raw value currently stored by this model.
   * <p>
   * The returned {@link RawValue} is a representation of the model's
   * state, encompassing the data, validity, and compatibility status.
   *
   * @return the raw value, never {@code null}
   */
  default RawValue<T> getRawValue() {
    return rawValue().getValue();
  }

  /**
   * An observable representing the raw value currently stored by this model.
   * <p>
   * This observable provides access to the underlying {@link RawValue} state
   * even when the model is invalid or not applicable.
   *
   * @return an observable raw value, never {@code null}
   */
  ObservableValue<RawValue<T>> rawValue();

}
