package org.int4.fx.builders.control;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

/**
 * Builder for {@link ToggleButton} instances.
 */
public final class ToggleButtonBuilder extends AbstractButtonBaseBuilder<ToggleButton, ToggleButtonBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public ToggleButtonBuilder(String... styleClasses) {
    super(ToggleButton::new, styleClasses);
  }

  /**
   * Marks the toggle button as selected.
   *
   * @return the fluent builder, never {@code null}
   * @see ToggleButton#selectedProperty()
   */
  public ToggleButtonBuilder selected() {
    return apply(c -> c.setSelected(true));
  }

  /**
   * Assigns the toggle button to a toggle group.
   *
   * @param group the toggle group, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see ToggleButton#setToggleGroup(ToggleGroup)
   */
  public ToggleButtonBuilder toggleGroup(ToggleGroup group) {
    return apply(c -> c.setToggleGroup(group));
  }
}