package org.int4.fx.builders.control;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.ListCell;

import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.model.ValueModel;

/**
 * Abstract base builder for {@link ComboBox} instances.
 * <p>
 * This contains any untyped methods.
 *
 * @param <C> the concrete {@link ComboBox} type
 * @param <B> the concrete builder type
 */
public abstract class ComboBoxBuilder<C extends ComboBox<?>, B extends ComboBoxBuilder<C, B>> extends AbstractComboBoxBaseBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ComboBoxBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets the number of visible rows in the combo box popup.
   *
   * @param count the number of rows to show
   * @return the fluent builder, never {@code null}
   * @see ComboBox#setVisibleRowCount(int)
   */
  public B visibleRowCount(int count) {
    return apply(c -> c.setVisibleRowCount(count));
  }

  /*
   * Here are all the typed methods, but unexposed. The Raw builder exposes these
   * as type-determining methods, while the Typed builder exposes them as fixed
   * type methods.
   */

  static <T> Typed<T> cellFactory(Typed<T> builder, Supplier<ListCell<T>> cellFactory) {
    Objects.requireNonNull(cellFactory, "cellFactory");

    return builder.apply(c -> c.setCellFactory(lv -> cellFactory.get()));
  }

  static <T> Typed<T> buttonCell(Typed<T> builder, ListCell<T> cell) {
    Objects.requireNonNull(cell, "cell");

    return builder.apply(c -> c.setButtonCell(cell));
  }

  static <T> Typed<T> value(Typed<T> builder, Property<T> property) {
    Objects.requireNonNull(property, "property");

    return builder.apply(c -> c.valueProperty().bindBidirectional(property));
  }

  static <T> Typed<T> items(Typed<T> builder, List<T> items) {
    Objects.requireNonNull(items, "items");

    return builder.apply(c -> {
      if(builder.comparator != null) {
        c.setItems(new SortedList<>(c.getItems(), builder.comparator));
      }

      c.getItems().setAll(items);
    });
  }

  static <T> Typed<T> items(Typed<T> builder, ObservableList<T> items) {
    Objects.requireNonNull(items, "items");

    return builder.apply(c -> c.setItems(builder.comparator == null ? items : new SortedList<>(items, builder.comparator)));
  }

  static <T> Typed<T> model(Typed<T> builder, ValueModel<T> model) {
    Objects.requireNonNull(model, "model");

    return builder.apply(node -> {
      ObservableList<T> items = node.getItems();

      if(builder.comparator != null) {
        // hard replace the list with our own sorted list:
        node.setItems(new SortedList<>(items, builder.comparator));
      }

      ModelLinker<ComboBox<T>, T, T> linker = ModelLinker.link(
        node,
        model,
        () -> node.getSelectionModel().getSelectedItem(),
        v -> node.getSelectionModel().select(v),
        Function.identity(),
        r -> node.getSelectionModel().selectedItemProperty().subscribe(r::run)
      );

      linker.addSubscriber(() -> model.domainProperty().subscribe(
        d -> items.setAll(switch(d.view(IndexedView.class)) {
          case IndexedView<T> iv -> iv.asList();
          default -> List.of();
        })
      ));
    });
  }

  @SuppressWarnings("unchecked")
  final <T> Typed<T> toTyped() {
    Typed<T> builder = new Typed<>();

    return builder.apply(c -> this.initialize((C)c));  // effectively "copies" all previous options
  }

  /**
   * Raw builder variant for {@link ComboBox} instances.
   * <p>
   * This contains typed methods that result in a {@link Typed} variant of the builder.
   */
  public static final class Raw extends ComboBoxBuilder<ComboBox<?>, Raw> {

    /**
     * Creates a new builder with optional style classes.
     *
     * @param styleClasses the style classes, cannot be {@code null} but can be empty
     * @throws NullPointerException if any argument is {@code null}
     */
    public Raw(String... styleClasses) {
      super(ComboBox::new, styleClasses);
    }

    /**
     * Binds the selected value bidirectionally to a property.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the items and the new type of the builder
     * @param property the property to bind to, cannot be {@code null}
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see ComboBoxBase#valueProperty()
     */
    public final <T> Typed<T> value(Property<T> property) {
      return value(toTyped(), property);
    }

    /**
     * Sets the items from a fixed list.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the items and the new type of the builder
     * @param items the items to set, cannot be {@code null} but can be empty
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public final <T> Typed<T> items(List<T> items) {
      return items(toTyped(), items);
    }

    /**
     * Sets the items from an observable list.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the items and the new type of the builder
     * @param items the observable items list, cannot be {@code null} but can be empty
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public final <T> Typed<T> items(ObservableList<T> items) {
      return items(toTyped(), items);
    }

    /**
     * Populates the items from the model's domain and keeps the control's selection
     * synchronized with the model value.
     * <p>
     * The model is expected to expose an {@link IndexedView} via its domain; if no
     * such view is present, the control's items list will be empty.
     * <p>
     * In most cases this method is used with a {@link org.int4.fx.values.model.ChoiceModel}, but any
     * {@link ValueModel} providing an {@link IndexedView} is supported.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the items and the new type of the builder
     * @param model the value model backing this control; cannot be {@code null}
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see org.int4.fx.values.model.ChoiceModel
     * @see ValueModel
     * @see IndexedView
     */
    public final <T> Typed<T> model(ValueModel<T> model) {
      return model(toTyped(), model);
    }

    /**
     * Sets a comparator used to sort items.
     * <p>
     * When specified, item lists are wrapped in a {@link javafx.collections.transformation.SortedList}.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the items and the new type of the builder
     * @param comparator the comparator to use
     * @return the fluent builder, never {@code null}
     * @see javafx.collections.transformation.SortedList
     */
    public final <T> Typed<T> comparator(Comparator<T> comparator) {
      Typed<T> builder = toTyped();

      builder.comparator = comparator;

      return builder;
    }
  }

  /**
   * Typed builder variant for {@link ComboBox} instances.
   * <p>
   * This contains methods available after the type has been established.
   *
   * @param <T> the type of the combo box
   */
  public static final class Typed<T> extends ComboBoxBuilder<ComboBox<T>, Typed<T>> {
    private Comparator<T> comparator;

    Typed() {
      super(ComboBox::new);
    }

    /**
     * Binds the selected value bidirectionally to a property.
     *
     * @param property the property to bind to, cannot be {@code null}
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see ComboBoxBase#valueProperty()
     */
    public Typed<T> value(Property<T> property) {
      return value(this, property);
    }

    /**
     * Populates the items from the model's domain and keeps the control's selection
     * synchronized with the model value.
     * <p>
     * The model is expected to expose an {@link IndexedView} via its domain; if no
     * such view is present, the control's items list will be empty.
     * <p>
     * In most cases this method is used with a
     * {@link org.int4.fx.values.model.ChoiceModel}, but any
     * {@link ValueModel} providing an {@link IndexedView} is supported.
     *
     * @param model the value model backing this control; cannot be {@code null}
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see org.int4.fx.values.model.ChoiceModel
     * @see ValueModel
     * @see IndexedView
     */
    public Typed<T> model(ValueModel<T> model) {
      return model(this, model);
    }

    /**
     * Sets the cell factory used to create cells for the combo box popup.
     *
     * @param cellFactory a supplier creating new {@link ListCell} instances, cannot be {@code null}
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code cellFactory} is {@code null}
     * @see ComboBox#setCellFactory(javafx.util.Callback)
     */
    public Typed<T> cellFactory(Supplier<ListCell<T>> cellFactory) {
      return cellFactory(this, cellFactory);
    }

    /**
     * Sets the button cell used to render the selected item.
     *
     * @param cell the button cell
     * @return the fluent builder, never {@code null}
     * @see ComboBox#setButtonCell(ListCell)
     */
    public Typed<T> buttonCell(ListCell<T> cell) {
      return buttonCell(this, cell);
    }

    /**
     * Sets a comparator used to sort items.
     * <p>
     * When specified, item lists are wrapped in a {@link javafx.collections.transformation.SortedList}.
     *
     * @param comparator the comparator to use
     * @return the fluent builder, never {@code null}
     * @see javafx.collections.transformation.SortedList
     */
    public Typed<T> comparator(Comparator<T> comparator) {
      this.comparator = comparator;

      return this;
    }
  }
}