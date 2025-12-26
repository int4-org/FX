package org.int4.fx.values.model;

/**
 * A constrained model whose stored value and domain element type are the same.
 * <p>
 * This interface specializes {@link ConstrainedModel} for the common case
 * where the type of values held by the model ({@code T}) matches the type
 * of values allowed by its domain.
 * <p>
 * This simplifies usage in UI builders and other contexts where no
 * type conversion is required between domain elements and stored values.
 *
 * @param <T> the type of the stored value and the domain element
 */
public interface ValueModel<T> extends ConstrainedModel<T, T> {
}
