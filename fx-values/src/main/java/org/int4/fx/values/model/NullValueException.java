package org.int4.fx.values.model;

/**
 * Thrown when attempting to retrieve a value from a model that is valid but
 * currently {@code null} and cannot represent {@code null} (for example, a
 * primitive-backed model such as {@link IntegerModel} or {@link DoubleModel}).
 * <p>
 * This exception signals a logical error in accessing the model's value in
 * a context where {@code null} is not allowed. Typically, it should not be
 * caught, similar to {@link IllegalStateException} or
 * {@link IllegalArgumentException}.
 */
public class NullValueException extends RuntimeException {

  /**
   * Creates a new exception for the given model.
   *
   * @param model the model that held {@code null}; cannot be {@code null}
   */
  public NullValueException(ConstrainedModel<?, ?> model) {
    super("Model " + model + " held null and is not representable");
  }
}
