package org.int4.fx.core.event;

import java.util.Objects;

import javafx.event.Event;
import javafx.event.EventType;

import org.int4.fx.core.util.Template;

/**
 * An event that represents the result of a validation check. This event is typically
 * targeted at the {@code Node} that has a change in its validation state.
 * <p>
 * The event bubbles normally through the scene graph, allowing intermediate nodes
 * or containers to intercept it to render validation markers, summaries, or other
 * visual feedback for their children.
 */
public class ValidationEvent extends Event {

  /**
   * The event type for validation changes.
   */
  public static final EventType<ValidationEvent> VALIDATION_CHANGED = new EventType<>(Event.ANY, "VALIDATION_CHANGED");

  private final Template template;
  private final Object invalidValue;
  private final boolean invalidValueTypeIncompatible;

  private ValidationEvent(Template template, Object invalidValue, boolean invalidValueTypeIncompatible) {
    super(VALIDATION_CHANGED);

    this.template = template;
    this.invalidValue = invalidValue;
    this.invalidValueTypeIncompatible = invalidValueTypeIncompatible;
  }

  /**
   * Returns a successful validation event.
   *
   * @return a successful validation event, never {@code null}
   */
  public static ValidationEvent valid() {
    return new ValidationEvent(null, null, false);
  }

  /**
   * Returns a failed validation event with the specified template and invalid value.
   *
   * @param template the template describing the validation failure, cannot be {@code null}
   * @param value the value that failed validation, can be {@code null}
   * @param invalidValueTypeIncompatible whether the value was of an incompatible type (e.g., parsing/conversion failure)
   * @return a failed validation event, never {@code null}
   * @throws NullPointerException if {@code template} is {@code null}
   */
  public static ValidationEvent invalid(Template template, Object value, boolean invalidValueTypeIncompatible) {
    return new ValidationEvent(Objects.requireNonNull(template, "template"), value, invalidValueTypeIncompatible);
  }

  /**
   * Returns whether validation was successful.
   *
   * @return {@code true} if validation was successful, otherwise {@code false}
   */
  public boolean isValid() {
    return template == null;
  }

  /**
   * Returns the template describing the validation failure, or {@code null} if validation was successful.
   *
   * @return the template, or {@code null} if validation was successful
   */
  public Template template() {
    return template;
  }

  /**
   * Returns the value that failed validation, or {@code null} if validation was successful
   * or if the value was {@code null}.
   * <p>
   * When {@link #invalidValueTypeIncompatible()} is {@code true}, this may contain
   * the raw input (e.g., a String that failed parsing).
   *
   * @return the invalid value, or {@code null}
   */
  public Object invalidValue() {
    return invalidValue;
  }

  /**
   * Returns whether the value that failed validation was of an incompatible type.
   * <p>
   * This typically occurs when a value cannot be parsed or converted into the
   * expected domain type. In this state, the object returned by {@link #invalidValue()}
   * may be of a different type than what the validation logic normally expects.
   *
   * @return {@code true} if the type was incompatible, otherwise {@code false}
   */
  public boolean invalidValueTypeIncompatible() {
    return invalidValueTypeIncompatible;
  }
}
