package org.int4.fx.builders.common;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

// General guidelines for the builders:
// - noun methods result in a builder, possibly a nested one
// - verb methods result in a parent builder or the final product

/**
 * Base class for fluent builders that configure JavaFX {@link Node} instances.
 * <p>
 * This builder exposes common node-related configuration options such as
 * visibility, enablement, styling, mouse handling, and event handlers.
 * All methods are fluent and return the concrete builder type.
 *
 * @param <N> the type of {@link Node} being built
 * @param <B> the concrete builder type
 */
public abstract class AbstractNodeBuilder<N extends Node, B extends AbstractNodeBuilder<N, B>> extends AbstractOptionBuilder<N, B> implements NodeBuilder {

  /**
   * Constructs a new instance with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  protected AbstractNodeBuilder(String... styleClasses) {
    style(styleClasses);
  }

  /**
   * Adds the given style classes to the resulting node.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return the fluent builder, never {@code null}
   * @see Node#getStyleClass()
   */
  public final B style(String... styleClasses) {
    Objects.requireNonNull(styleClasses, "styleClasses");

    return apply(n -> n.getStyleClass().addAll(Arrays.asList(styleClasses)));
  }

  /**
   * Adds the given style provider to provide styles to the resulting node.
   * <p>
   * Whenever the observable value changes, the previously applied style
   * (if any) is removed and the new one is added.
   *
   * @param styleProvider an observable value providing a style name, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleProvider} is {@code null}
   * @see Node#getStyleClass()
   */
  public final B styleProvider(ObservableValue<String> styleProvider) {
    Objects.requireNonNull(styleProvider, "styleProvider");

    return apply(n -> styleProvider.subscribe(new Consumer<String>() {
      private String valueSet;

      @Override
      public void accept(String v) {
        if(valueSet != null) {
          n.getStyleClass().remove(valueSet);
          valueSet = null;
        }
        if(v != null && !v.isBlank()) {
          valueSet = v;
          n.getStyleClass().add(valueSet);
        }
      }
    }));
  }

  /**
   * Sets {@link Node#mouseTransparentProperty()} to {@code true}
   * on the resulting node.
   *
   * @return the fluent builder, never {@code null}
   * @see Node#mouseTransparentProperty()
   */
  public final B mouseTransparent() {
    return mouseTransparent(true);
  }

  /**
   * Sets {@link Node#mouseTransparentProperty()} on the resulting node.
   *
   * @param mouseTransparent the new value for {@link Node#mouseTransparentProperty()}
   * @return the fluent builder, never {@code null}
   * @see Node#mouseTransparentProperty()
   */
  public final B mouseTransparent(boolean mouseTransparent) {
    return apply(n -> n.setMouseTransparent(mouseTransparent));
  }

  /**
   * Binds the visibility and managed state of the node to the negation of the given condition.
   * <p>
   * This is a convenience method equivalent to
   * {@code visible(when.map(b -> !b))}.
   *
   * @param when an observable condition controlling invisibility, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code when} is {@code null}
   * @see Node#visibleProperty()
   * @see Node#managedProperty()
   */
  public final B invisible(ObservableValue<Boolean> when) {
    return visible(when.map(b -> !b));
  }

  /**
   * Binds both {@link Node#visibleProperty()} and
   * {@link Node#managedProperty()} to the given condition.
   * <p>
   * When the condition is {@code false}, the node will be hidden and
   * excluded from layout calculations.
   *
   * @param when an observable condition controlling visibility
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code when} is {@code null}
   * @see Node#visibleProperty()
   * @see Node#managedProperty()
   */
  public final B visible(ObservableValue<Boolean> when) {
    Objects.requireNonNull(when, "when");

    return apply(n -> {
      n.managedProperty().bind(when);
      n.visibleProperty().bind(when);
    });
  }

  /**
   * Disables the resulting node.
   *
   * @return the fluent builder, never {@code null}
   * @see Node#disableProperty()
   */
  public final B disable() {
    return disable(true);
  }

  /**
   * Sets {@link Node#disableProperty()} on the resulting node.
   *
   * @param disable whether the node should be disabled
   * @return the fluent builder, never {@code null}
   * @see Node#disableProperty()
   */
  public final B disable(boolean disable) {
    return apply(n -> n.setDisable(disable));
  }

  /**
   * Enables the node while the given condition is {@code true}.
   * <p>
   * This is a convenience method that binds disablement to the
   * negation of the given observable.
   *
   * @param when an observable condition controlling enablement
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code when} is {@code null}
   * @see Node#disableProperty()
   */
  public final B enable(ObservableValue<Boolean> when) {
    return disable(when.map(b -> !b).orElse(true));
  }

  /**
   * Binds {@link Node#disableProperty()} to the given observable.
   *
   * @param when an observable condition controlling disablement, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code when} is {@code null}
   * @see Node#disableProperty()
   */
  public final B disable(ObservableValue<Boolean> when) {
    Objects.requireNonNull(when, "when");

    return apply(n -> n.disableProperty().bind(when));
  }

  /**
   * Sets {@link Node#onMouseEnteredProperty()} on the resulting node.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Node#onMouseEnteredProperty()
   */
  public final B onMouseEntered(EventHandler<? super MouseEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(n -> n.setOnMouseEntered(eventHandler));
  }

  /**
   * Sets {@link Node#onMouseExitedProperty()} on the resulting node.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Node#onMouseExitedProperty()
   */
  public final B onMouseExited(EventHandler<? super MouseEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(n -> n.setOnMouseExited(eventHandler));
  }

  /**
   * Sets {@link Node#onMouseClickedProperty()} on the resulting node.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Node#onMouseClickedProperty()
   */
  public final B onMouseClicked(EventHandler<? super MouseEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(n -> n.setOnMouseClicked(eventHandler));
  }

  /**
   * Sets {@link Node#onMousePressedProperty()} on the resulting node.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Node#onMousePressedProperty()
   */
  public final B onMousePressed(EventHandler<? super MouseEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(n -> n.setOnMousePressed(eventHandler));
  }

  /**
   * Sets {@link Node#onMouseReleasedProperty()} on the resulting node.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Node#onMouseReleasedProperty()
   */
  public final B onMouseReleased(EventHandler<? super MouseEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(n -> n.setOnMouseReleased(eventHandler));
  }

  /**
   * Sets {@link Node#onScrollProperty()} on the resulting node.
   *
   * @param eventHandler the event handler to set, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code eventHandler} is {@code null}
   * @see Node#onScrollProperty()
   */
  public final B onScroll(EventHandler<? super ScrollEvent> eventHandler) {
    Objects.requireNonNull(eventHandler, "eventHandler");

    return apply(n -> n.setOnScroll(eventHandler));
  }

  /**
   * Sets {@link Node#idProperty()} of the resulting node.
   *
   * @param id the node id, can be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Node#idProperty()
   */
  public final B id(String id) {
    return apply(n -> n.setId(id));
  }
}
