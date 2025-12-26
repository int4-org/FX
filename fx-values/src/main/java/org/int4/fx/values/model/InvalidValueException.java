package org.int4.fx.values.model;

/**
 * Signals that a {@link ConstrainedModel} is in an invalid state when
 * its value was accessed via the model specific {@code get} method.
 * <p>
 * This exception indicates a logic or design error: the model should
 * never be invalid at the point where it is about to be persisted,
 * sent to a server, or otherwise relied upon. It is not intended for
 * normal runtime handling.
 * <p>
 * Use {@link ConstrainedModel#getValue()} or {@link ConstrainedModel#getRawValue()}
 * if you need to safely access the value of a model that may be invalid.
 */
public class InvalidValueException extends RuntimeException {

  /**
   * Creates a new {@code InvalidValueException} for the given model.
   *
   * @param model the model that was in an invalid state, cannot be {@code null}
   */
  public InvalidValueException(ConstrainedModel<?, ?> model) {
    super("Model " + model + " is invalid");
  }
}
