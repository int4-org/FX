package org.int4.fx.builders.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import org.int4.common.builder.AbstractOptionBuilder;
import org.int4.fx.builders.context.BuildContext;
import org.int4.fx.builders.context.BuildContexts;
import org.int4.fx.builders.context.BuildStrategy;
import org.int4.fx.builders.strategy.ChildrenStrategy;
import org.int4.fx.builders.strategy.ContentStrategy;
import org.int4.fx.builders.strategy.TextStrategy;

/**
 * Base class for fluent builders that configure JavaFX {@link Node} instances.
 * <p>
 * This builder exposes common node-related configuration options such as
 * visibility, enablement, styling, mouse handling, and event handlers.
 * It also integrates with {@link BuildContext} and {@link BuildStrategy} to
 * apply content or children strategies to the constructed nodes.
 * <p>
 * All methods are fluent and return the concrete builder type.
 *
 * @param <N> the type of {@link Node} being built
 * @param <B> the concrete builder type
 */
public abstract class AbstractNodeBuilder<N extends Node, B extends AbstractNodeBuilder<N, B>> extends AbstractOptionBuilder<N, B> implements NodeBuilder<N> {
  private BuildContext context;

  /**
   * Constructs a new instance with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  protected AbstractNodeBuilder(String... styleClasses) {
    style(styleClasses);
  }

  /**
   * Sets a context on this builder node, allowing some or all
   * {@link BuildStrategy strategies} to be overridden.
   * <p>
   * If {@code context} is {@code null}, the parent context during building is
   * used.
   *
   * @param context a build context, can be {@code null}
   * @return the fluent builder, never {@code null}
   */
  public final B context(BuildContext context) {
    this.context = context;

    return self();
  }

  /**
   * Resolves a strategy from the active build context and applies it to the
   * constructed node.
   * <p>
   * The supplied strategy supplier provides the base strategy to use if no
   * override is present in the active build context. The resolved strategy
   * is then invoked using the given consumer.
   *
   * @param <S> the type of strategy
   * @param supplier a supplier providing the base strategy, cannot be {@code null}
   * @param consumer a consumer that applies the resolved strategy to the node,
   *  cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  protected final <S extends BuildStrategy<S>> B applyStrategy(Supplier<S> supplier, BiConsumer<S, N> consumer) {
    Objects.requireNonNull(supplier, "supplier");
    Objects.requireNonNull(consumer, "consumer");

    return apply(node -> consumer.accept(BuildContexts.activeContext().resolve(supplier), node));
  }

  /**
   * Applies the active {@link ContentStrategy} to a single content value.
   * <p>
   * If the content value is a builder, it is first converted to a {@link Node};
   * all other types (Node, String, Enum, etc.) are passed unchanged. The resulting
   * object is then processed by the content strategy, which applies it to the
   * target node via the provided setter.
   *
   * @param content the content value supplied to the builder, cannot be {@code null}
   * @param setter a consumer that applies the resolved content node to the
   *   constructed node, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  protected final B applyContentStrategy(Object content, BiConsumer<N, Node> setter) {
    Objects.requireNonNull(content, "content");
    Objects.requireNonNull(setter, "setter");

    return apply(node -> BuildContexts.activeContext().resolve(ContentStrategy::base).apply(
      node,
      resolveBuilder(content),
      resolvedContent -> setter.accept(node, resolvedContent)
    ));
  }

  /**
   * Applies the active {@link ChildrenStrategy} to a collection of child values.
   * <p>
   * Each value is pre-processed such that builders are converted to {@link Node}
   * instances, while all other types (Node, String, Enum, etc.) are passed through
   * unchanged. The resulting list is then processed by the children strategy, which
   * applies it to the target node using the provided setter.
   *
   * @param nodes the child values supplied to the builder, cannot be {@code null}, but can be empty
   *   or contain {@code null}s
   * @param setter a consumer that applies the resolved children to the
   *   constructed node, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  protected final B applyChildrenStrategy(Object[] nodes, BiConsumer<N, List<Node>> setter) {
    Objects.requireNonNull(nodes, "nodes");
    Objects.requireNonNull(setter, "setter");

    return apply(node -> BuildContexts.activeContext().resolve(ChildrenStrategy::base).apply(
      node,
      Arrays.stream(nodes).map(AbstractNodeBuilder::resolveBuilder).toList(),
      list -> setter.accept(node, list)
    ));
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
   * Sets the accessible text of the node.
   * <p>
   * Any object is accepted, and provided to the active {@link TextStrategy}.
   *
   * @param text the accessible text to set, may be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Node#setAccessibleText(String)
   */
  public final B accessibleText(Object text) {
    return applyStrategy(TextStrategy::base, (s, node) -> s.apply(node, text, node::setAccessibleText));
  }

  /**
   * Sets the accessible help text of the node.
   * <p>
   * Any object is accepted, and provided to the active {@link TextStrategy}.
   *
   * @param text the accessible text to set, may be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Node#setAccessibleHelp(String)
   */
  public final B accessibleHelp(Object text) {
    return applyStrategy(TextStrategy::base, (s, node) -> s.apply(node, text, node::setAccessibleHelp));
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
   * Sets {@link Node#pickOnBoundsProperty()} to {@code true}
   * on the resulting node.
   *
   * @return the fluent builder, never {@code null}
   * @see Node#pickOnBoundsProperty()
   */
  public final B pickOnBounds() {
    return pickOnBounds(true);
  }

  /**
   * Sets {@link Node#pickOnBoundsProperty()} to {@code false}
   * on the resulting node.
   *
   * @return the fluent builder, never {@code null}
   * @see Node#pickOnBoundsProperty()
   */
  public final B ignoreBounds() {
    return pickOnBounds(false);
  }

  /**
   * Sets {@link Node#pickOnBoundsProperty()} on the resulting node.
   *
   * @param pickOnBounds whether the node should receive mouse events
   *   based on its bounds rather than its visible pixels
   * @return the fluent builder, never {@code null}
   * @see Node#pickOnBoundsProperty()
   */
  public final B pickOnBounds(boolean pickOnBounds) {
    return apply(n -> n.setPickOnBounds(pickOnBounds));
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

  /**
   * Applies all registered configuration options to the given node.
   * <p>
   * The node-level context (set via {@link #context(BuildContext)}) is merged
   * with the given parent context, or with the current active context if it
   * is {@code null}. If no node-level context is set, the parent context or
   * active context is used as-is. All registered options are applied with the
   * merged context as the active build context.
   *
   * @param parentContext a {@link BuildContext}, can be {@code null}
   * @param instance the object to initialize, cannot be {@code null}
   * @return the now initialized node for fluent chaining, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  protected final N initialize(BuildContext parentContext, N instance) {
    Objects.requireNonNull(instance, "instance");

    BuildContext parent = parentContext == null ? BuildContexts.activeContext() : parentContext;
    BuildContext mergedContext = context == null ? parent : parent.merge(context);

    return BuildContexts.with(mergedContext, () -> initialize(instance));
  }

  private static Object resolveBuilder(Object potentialNode) {
    return potentialNode instanceof NodeBuilder nb ? nb.build() : potentialNode;
  }
}
