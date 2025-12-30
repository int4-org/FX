package org.int4.fx.builders.control;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 * Builder for {@link ComboBox} instances.
 *
 * @param <T> the type of the items contained in the combo box
 */
public final class ComboBoxBuilder<T> extends AbstractComboBoxBuilder<T, ComboBox<T>, ComboBoxBuilder<T>> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public ComboBoxBuilder(String... styleClasses) {
    super(ComboBox::new, styleClasses);
  }

  /**
   * Sets the cell factory used to create cells for the combo box popup.
   *
   * @param cellFactory a supplier creating new {@link ListCell} instances, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code cellFactory} is {@code null}
   * @see ComboBox#setCellFactory(javafx.util.Callback)
   */
  public ComboBoxBuilder<T> cellFactory(Supplier<ListCell<T>> cellFactory) {
    Objects.requireNonNull(cellFactory, "cellFactory");

    return apply(c -> c.setCellFactory(lv -> cellFactory.get()));
  }

  /**
   * Sets the button cell used to render the selected item.
   *
   * @param cell the button cell
   * @return the fluent builder, never {@code null}
   * @see ComboBox#setButtonCell(ListCell)
   */
  public ComboBoxBuilder<T> buttonCell(ListCell<T> cell) {
    return apply(c -> c.setButtonCell(cell));
  }
}