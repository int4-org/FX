package org.int4.fx.builders.control;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.internal.DomainListBridge;
import org.int4.fx.builders.internal.ModelLinker;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.model.WritableModel;

/**
 * Abstract base builder for {@link ListView} instances.
 * <p>
 * This contains any untyped methods.
 *
 * @param <C> the concrete {@link ListView} type
 * @param <B> the concrete builder type
 */
public abstract class ListViewBuilder<C extends ListView<?>, B extends ListViewBuilder<C, B>> extends AbstractControlBuilder<C, B> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ListViewBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
  }

  /**
   * Sets the orientation of the list.
   *
   * @param orientation the orientation
   * @return the fluent builder, never {@code null}
   * @see ListView#setOrientation(Orientation)
   */
  public B orientation(Orientation orientation) {
    return apply(c -> c.setOrientation(orientation));
  }

  /**
   * Sets the node shown when the list is empty.
   *
   * @param node the placeholder node, can be {@code null}
   * @return the fluent builder, never {@code null}
   * @see ListView#setPlaceholder(Node)
   */
  public B placeholder(Node node) {
    return apply(c -> c.setPlaceholder(node));
  }

  /**
   * Sets the fixed cell size for all cells, or {@code 0} to size cells
   * individually.
   *
   * @param size the cell size, or {@code 0}
   * @return the fluent builder, never {@code null}
   * @see ListView#setFixedCellSize(double)
   */
  public B fixedCellSize(double size) {
    return apply(c -> c.setFixedCellSize(size));
  }

  /**
   * Sets whether the list is editable.
   *
   * @param editable whether the list is editable
   * @return the fluent builder, never {@code null}
   * @see ListView#setEditable(boolean)
   */
  public B editable(boolean editable) {
    return apply(c -> c.setEditable(editable));
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

  static <T> Typed<T> items(Typed<T> builder, List<T> items) {
    Objects.requireNonNull(items, "items");

    return builder.apply(c -> {
      ObservableList<T> list = FXCollections.observableArrayList(items);

      c.setItems(builder.comparator == null ? list : new SortedList<>(list, builder.comparator));
    });
  }

  static <T> Typed<T> items(Typed<T> builder, ObservableList<T> items) {
    Objects.requireNonNull(items, "items");

    return builder.apply(c -> c.setItems(builder.comparator == null ? items : new SortedList<>(items, builder.comparator)));
  }

  static <T> Typed<T> model(Typed<T> builder, WritableModel<T> model) {
    Objects.requireNonNull(model, "model");

    return builder.apply(c -> {
      DomainListBridge<T> items = DomainListBridge.of(model.getDomain());

      c.setItems(builder.comparator == null ? items : new SortedList<>(items, builder.comparator));

      ModelLinker<ListView<T>, T> linker = ModelLinker.sync(
        c,
        model,
        rv -> c.getSelectionModel().select(switch(rv) {
          case RawValue.Converted<T> converted -> converted.value();
          case RawValue.Incompatible<T> _ -> null;
        })
      );

      c.getSelectionModel().selectedItemProperty().subscribe((ov, nv) -> linker.push(nv));

      linker.onTransition(t -> {

        /*
         * Item changes may clear the selection when the selected value is no longer
         * present. Keep the selection so an out-of-domain value survives a domain
         * change; the model value is applied afterwards when it is not dirty.
         */

        T selection = c.getSelectionModel().getSelectedItem();

        c.getSelectionModel().clearSelection();

        items.apply(t);

        c.getSelectionModel().select(model.isApplicable() ? selection : null);
      });
    });
  }

  @SuppressWarnings("unchecked")
  final <T> Typed<T> toTyped() {
    Typed<T> builder = new Typed<>();

    return builder.apply(c -> this.initialize((C)c));  // effectively "copies" all previous options
  }

  /**
   * Raw builder variant for {@link ListView} instances.
   * <p>
   * This contains typed methods that result in a {@link Typed} variant of the builder.
   */
  public static final class Raw extends ListViewBuilder<ListView<?>, Raw> {

    /**
     * Creates a new builder with optional style classes.
     *
     * @param styleClasses the style classes, cannot be {@code null} but can be empty
     * @throws NullPointerException if any argument is {@code null}
     */
    public Raw(String... styleClasses) {
      super(ListView::new, styleClasses);
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
     * The list is used directly, so changes to it are reflected in the control.
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
     * Populates the items from the model's domain and keeps both the items and
     * the selection synchronized with the model.
     * <p>
     * The model's domain provides the items; it is expected to expose an
     * {@link IndexedView}, and the items list will be empty if no such view is
     * present. The list is read-only, so the control cannot hand out write access
     * to it. The list's single selection is bound to the model value, like a
     * combo box with its popup always expanded.
     * <p>
     * This method establishes the concrete type of the builder.
     *
     * @param <T> the type of the items and the new type of the builder
     * @param model the model providing the items, cannot be {@code null}
     * @return the fluent builder, typed to {@code T}, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see IndexedView
     */
    public final <T> Typed<T> model(WritableModel<T> model) {
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
   * Typed builder variant for {@link ListView} instances.
   * <p>
   * This contains methods available after the type has been established.
   *
   * @param <T> the type of the list view
   */
  public static final class Typed<T> extends ListViewBuilder<ListView<T>, Typed<T>> {
    private Comparator<T> comparator;

    Typed() {
      super(ListView::new);
    }

    /**
     * Sets the items from a fixed list.
     *
     * @param items the items to set, cannot be {@code null} but can be empty
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public Typed<T> items(List<T> items) {
      return items(this, items);
    }

    /**
     * Sets the items from an observable list.
     * <p>
     * The list is used directly, so changes to it are reflected in the control.
     *
     * @param items the observable items list, cannot be {@code null} but can be empty
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public Typed<T> items(ObservableList<T> items) {
      return items(this, items);
    }

    /**
     * Populates the items from the model's domain and keeps both the items and
     * the selection synchronized with the model.
     * <p>
     * The model's domain provides the items; it is expected to expose an
     * {@link IndexedView}, and the items list will be empty if no such view is
     * present. The list is read-only, so the control cannot hand out write access
     * to it. The list's single selection is bound to the model value, like a
     * combo box with its popup always expanded.
     *
     * @param model the model providing the items, cannot be {@code null}
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code model} is {@code null}
     * @see IndexedView
     */
    public Typed<T> model(WritableModel<T> model) {
      return model(this, model);
    }

    /**
     * Sets the cell factory used to create cells for the list.
     *
     * @param cellFactory a supplier creating new {@link ListCell} instances, cannot be {@code null}
     * @return the fluent builder, never {@code null}
     * @throws NullPointerException if {@code cellFactory} is {@code null}
     * @see ListView#setCellFactory(javafx.util.Callback)
     */
    public Typed<T> cellFactory(Supplier<ListCell<T>> cellFactory) {
      return cellFactory(this, cellFactory);
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
