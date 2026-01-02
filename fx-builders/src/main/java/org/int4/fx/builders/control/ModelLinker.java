package org.int4.fx.builders.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.util.Subscription;

import org.int4.fx.builders.internal.ShowingStateListener;
import org.int4.fx.event.CommitEvent;
import org.int4.fx.values.model.ConstrainedModel;
import org.int4.fx.values.model.ValueModel;

final class ModelLinker<N extends Node, R, T> {
  private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
  private static final PseudoClass TOUCHED = PseudoClass.getPseudoClass("touched");

  public static <N extends Node, T> ModelLinker<N, T, T> link(N node, ValueModel<T> model, Supplier<T> getter, Consumer<T> setter) {
    return new ModelLinker<>(node, model, getter, setter, Function.identity(), r -> Subscription.EMPTY);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, ValueModel<T> model, Supplier<R> getter, Consumer<T> setter, Function<R, T> converter) {
    return new ModelLinker<>(node, model, getter, setter, converter, r -> Subscription.EMPTY);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, ValueModel<T> model, Supplier<R> getter, Consumer<T> setter, Function<R, T> converter, Function<Runnable, Subscription> trigger) {
    return new ModelLinker<>(node, model, getter, setter, converter, trigger);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, ValueModel<T> model, Property<T> property) {
    return new ModelLinker<>(node, model, property);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, ValueModel<T> model, Property<T> property, T nullAlternative) {
    return new ModelLinker<>(node, model, property, nullAlternative);
  }

  private final ConstrainedModel<T, ?> model;
  private final Supplier<R> getter;
  private final Function<R, T> converter;
  private final List<Supplier<Subscription>> subscribers = new ArrayList<>();

  private Subscription subscription = Subscription.EMPTY;
  private Subscription masterSubscription = Subscription.EMPTY;

  ModelLinker(
    N node,
    ValueModel<T> model,
    Supplier<R> getter,
    Consumer<T> setter,
    Function<R, T> converter,
    Function<Runnable, Subscription> trigger
  ) {
    this.model = Objects.requireNonNull(model, "model");
    this.getter = Objects.requireNonNull(getter, "getter");
    this.converter = Objects.requireNonNull(converter, "converter");

    Objects.requireNonNull(node, "node");
    Objects.requireNonNull(setter, "setter");
    Objects.requireNonNull(trigger, "trigger");

    addSubscriber(() -> {
      node.visibleProperty().bind(model.applicable());
      node.managedProperty().bind(model.applicable());

      return () -> {
        node.visibleProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
        node.managedProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
      };
    });

    addSubscriber(() -> model.applicable()
      .subscribe(applicable -> {
        if(applicable) {
          masterSubscription = Subscription.combine(
            model.subscribe(v -> setter.accept(model.getRawValue())),
            model.valid().subscribe(v -> node.pseudoClassStateChanged(INVALID, !v)),
            trigger.apply(() -> {
              if(updateMaster()) {
                node.pseudoClassStateChanged(TOUCHED, true);
              }
            }),
            node.focusedProperty().subscribe((ov, focused) -> {
              if(!focused && updateMaster()) {
                node.pseudoClassStateChanged(TOUCHED, true);
              }
            })
          );
        }
        else {
          masterSubscription.unsubscribe();
          masterSubscription = Subscription.EMPTY;
        }
      })
    );

    /*
     * The scene property which is local to the Node is bound to determine when
     * to register and unregister listeners to attempt to remove references that
     * may cause object graphs to be retained.
     *
     * When the Scene sends show state signals, this is further enhanced to only
     * have listeners registered when the scene determined it is showing and can be
     * interacted with. This means that the act of closing a window is sufficient
     * to trigger deregistration of all listeners registered in this way.
     */

    node.sceneProperty().subscribe(v -> {
      if(v == null) {
        unbind();
      }
      else {
        if(v.hasProperties() && v.getProperties().containsKey(ShowingStateListener.SHOW_STATE_MANAGING_SCENE)) {

          /*
           * This scene will automatically call a ShowingStateListener when show
           * status changes so provide one here:
           */

          node.getProperties().put(ShowingStateListener.KEY, (ShowingStateListener)ModelLinker.this::bind);
        }
        else {

          /*
           * The scene offers no show state management, so just bind listeners when
           * the node is assigned a scene:
           */

          bind();
        }
      }
    });

    node.addEventHandler(CommitEvent.COMMIT, e -> updateMaster());
  }

  public Subscription addSubscriber(Supplier<Subscription> subscriber) {
    subscribers.add(subscriber);

    return () -> subscribers.remove(subscriber);
  }

  private void bind(boolean showing) {
    if(showing) {
      bind();
    }
    else {
      unbind();
    }
  }

  private void bind() {
    List<Subscription> subscriptions = new ArrayList<>();

    for(Supplier<Subscription> supplier : subscribers) {
      subscriptions.add(supplier.get());
    }

    subscription = Subscription.combine(subscriptions.toArray(Subscription[]::new));
  }

  private void unbind() {
    subscription.unsubscribe();
    subscription = Subscription.EMPTY;

    masterSubscription.unsubscribe();
    masterSubscription = Subscription.EMPTY;
  }

  ModelLinker(N node, ValueModel<T> model, Property<T> property) {
    this(node, model, property, null);
  }

  @SuppressWarnings("unchecked")
  ModelLinker(N node, ValueModel<T> model, Property<T> property, T nullAlternative) {
    this(
      node,
      model,
      () -> (R)property.getValue(),
      v -> property.setValue(v == null ? nullAlternative : v),
      r -> (T)r,
      r -> property.subscribe((ov, nv) -> r.run())
    );
  }

  public boolean updateMaster() {
    if(model.isApplicable()) {
      return model.trySet(getter.get(), converter);
    }

    return false;
  }
}
