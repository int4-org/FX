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

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.values.domain.IndexedView;
import org.int4.fx.values.model.ChoiceModel;

/**
 * Base builder for {@link ComboBox}-based controls.
 *
 * @param <T> the item type
 * @param <C> the concrete {@link ComboBox} type
 * @param <B> the concrete builder type
 */
public abstract class AbstractComboBoxBuilder<T, C extends ComboBox<T>, B extends AbstractComboBoxBuilder<T, C, B>> extends AbstractControlBuilder<C, B> {
  private Comparator<T> comparator;

  /**
   * Creates a new builder with optional style classes.
   *
   * @param instantiator the control instantiator, cannot be {@code null}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code instantiator} or {@code styleClasses} is {@code null}
   */
  protected AbstractComboBoxBuilder(Supplier<C> instantiator, String... styleClasses) {
    super(instantiator, styleClasses);
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
  @SuppressWarnings("unchecked")
  public final B comparator(Comparator<T> comparator) {
    this.comparator = comparator;

    return (B)this;
  }

  /**
   * Binds the selected value bidirectionally to a property.
   *
   * @param property the property to bind to, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code property} is {@code null}
   * @see ComboBox#getSelectionModel()
   */
  public final B value(Property<T> property) {
    Objects.requireNonNull(property, "property");

    return apply(c -> {
      property.subscribe(v -> c.getSelectionModel().select(v));
      c.getSelectionModel().selectedItemProperty().subscribe(property::setValue);
    });
  }

  /**
   * Sets the items from a fixed list.
   *
   * @param items the items to set, cannot be {@code null} but can be empty
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code items} is {@code null}
   */
  public final C items(List<T> items) {
    C node = build();

    if(comparator != null) {
      node.setItems(new SortedList<>(node.getItems(), comparator));
    }

    node.getItems().setAll(Objects.requireNonNull(items, "items"));

    return node;
  }

  /**
   * Sets the items from an observable list.
   *
   * @param items the observable items list, cannot be {@code null} but can be empty
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code items} is {@code null}
   */
  public final C items(ObservableList<T> items) {
    C node = build();

    node.setItems(comparator == null ? items : new SortedList<>(items, comparator));

    return node;
  }

  /**
   * Populates the items from a {@link ChoiceModel} and keeps selection synchronized.
   *
   * @param model the choice model, cannot be {@code null}
   * @return the created control, never {@code null}
   * @throws NullPointerException if {@code model} is {@code null}
   * @see ChoiceModel
   */
  public final C items(ChoiceModel<T> model) {
    C node = build();
    ObservableList<T> items = node.getItems();

    if(comparator != null) {
      // hard replace the list with our own sorted list:
      node.setItems(new SortedList<>(items, comparator));
    }

    ModelLinker<C, T, T> linker = ModelLinker.link(
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

    return node;
  }
}