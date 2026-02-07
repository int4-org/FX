package org.int4.fx.builders.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.util.Subscription;

import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.builders.internal.CommitEvent;
import org.int4.fx.builders.internal.ShowingStateListener;
import org.int4.fx.core.event.BroadcastHandler;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.model.ConstrainedModel;
import org.int4.fx.values.model.ValueModel;

final class ModelLinker<N extends Node, R, T> {
  public static boolean RESPOND_TO_TEST_FOCUS_EVENT;

  private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
  private static final PseudoClass TOUCHED = PseudoClass.getPseudoClass("touched");
  private static final PseudoClass DIRTY = PseudoClass.getPseudoClass("dirty");

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

  static class TestFocusEvent extends Event {
    public static final EventType<TestFocusEvent> MODIFY = new EventType<>(EventType.ROOT, "modify");

    final boolean focused;

    public TestFocusEvent(boolean focused) {
      super(MODIFY);

      this.focused = focused;
    }
  }

  private final N node;
  private final ConstrainedModel<T, ?> model;
  private final Supplier<R> getter;
  private final Consumer<T> setter;
  private final Function<R, T> converter;
  private final Function<Runnable, Subscription> trigger;
  private final List<Supplier<Subscription>> subscribers = new ArrayList<>();
  private final BroadcastHandler<WindowEvent> handler = e -> {
    if(e.type() == WindowEvent.SHOWING) {
      activateAllSubscriptions();
    }
    else if(e.type() == WindowEvent.HIDDEN) {
      terminateAllSubscriptions();
    }
  };

  private Subscription subscription = Subscription.EMPTY;
  private Subscription masterSubscription = Subscription.EMPTY;

  private boolean modelInitiatedChange;
  private boolean controlInitiatedChange;

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

  ModelLinker(
    N node,
    ValueModel<T> model,
    Supplier<R> getter,
    Consumer<T> setter,
    Function<R, T> converter,
    Function<Runnable, Subscription> trigger
  ) {
    this.node = Objects.requireNonNull(node, "node");
    this.model = Objects.requireNonNull(model, "model");
    this.getter = Objects.requireNonNull(getter, "getter");
    this.setter = Objects.requireNonNull(setter, "setter");
    this.converter = Objects.requireNonNull(converter, "converter");
    this.trigger = Objects.requireNonNull(trigger, "trigger");

    addSubscriber(() -> {
      node.visibleProperty().bind(model.applicable());
      node.managedProperty().bind(model.applicable());

      return () -> {
        node.visibleProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
        node.managedProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
      };
    });

    addSubscriber(() -> model.applicable().subscribe(this::monitorModelWhenApplicable));

    /*
     * The scene property which is local to the Node is bound to determine when
     * to register and unregister listeners to attempt to remove references that
     * may cause object graphs to be retained.
     *
     * When the Scene broadcasts WindowEvents, this is further enhanced to only
     * have listeners registered when the scene determined it is showing and can be
     * interacted with. This means that the act of closing a window is sufficient
     * to trigger deregistration of all listeners registered in this way.
     */

    node.sceneProperty().subscribe(scene -> {
      terminateAllSubscriptions();

      Broadcasts.removeHandler(node, WindowEvent.ANY, handler);

      if(scene != null) {
        if(scene.hasProperties() && scene.getProperties().containsKey(ShowingStateListener.SHOW_STATE_MANAGING_SCENE)) {

          /*
           * This scene will automatically do a broadcast when show
           * status changes so add a handler for these here:
           */

          Broadcasts.addHandler(node, WindowEvent.ANY, handler);
        }
        else {

          /*
           * The scene offers no show state management, so just bind listeners when
           * the node is assigned a scene:
           */

          activateAllSubscriptions();
        }
      }
    });

    node.addEventHandler(CommitEvent.COMMIT, e -> updateModel());

    if(RESPOND_TO_TEST_FOCUS_EVENT) {
      node.addEventHandler(TestFocusEvent.MODIFY, e -> focusChanged(e.focused));
    }
  }

  public Subscription addSubscriber(Supplier<Subscription> subscriber) {
    subscribers.add(subscriber);

    return () -> subscribers.remove(subscriber);
  }

  private void monitorModelWhenApplicable(boolean applicable) {
    if(applicable) {
      masterSubscription = Subscription.combine(
        model.subscribe(v -> {
          if(!controlInitiatedChange) {
            doModelInitiatedChange(() -> setter.accept(model.getRawValue()));
          }
        }),
        model.valid().subscribe(v -> {
          node.pseudoClassStateChanged(INVALID, !v);

          // It is possible model became valid due to an external action, update field:
          if(model.isValid()) {
            doModelInitiatedChange(() -> setter.accept(model.getValue()));
          }
        }),
        trigger.apply(() -> {
          if(!modelInitiatedChange) {
            doControlInitiatedChange(this::updateModel);

            node.pseudoClassStateChanged(TOUCHED, true);
            node.pseudoClassStateChanged(DIRTY, true);
          }
        }),
        node.focusedProperty().subscribe((ov, focused) -> focusChanged(focused))
      );
    }
    else {
      masterSubscription.unsubscribe();
      masterSubscription = Subscription.EMPTY;
    }
  }

  private void focusChanged(boolean focused) {
    if(!focused) {
      if(node.getPseudoClassStates().contains(DIRTY)) {
        updateModel();

        /*
         * On focus loss, also correct the value to be in the standard format
         * for controls that use converters, if the model is valid:
         */

        if(model.isValid()) {
          setter.accept(model.getValue());
        }

        node.pseudoClassStateChanged(DIRTY, false);
      }
    }
  }

  private void activateAllSubscriptions() {
    if(!subscriptionsActive()) {
      List<Subscription> subscriptions = new ArrayList<>();

      for(Supplier<Subscription> supplier : subscribers) {
        subscriptions.add(supplier.get());
      }

      subscription = Subscription.combine(subscriptions.toArray(Subscription[]::new));
    }
  }

  private void terminateAllSubscriptions() {
    subscription.unsubscribe();
    subscription = Subscription.EMPTY;

    masterSubscription.unsubscribe();
    masterSubscription = Subscription.EMPTY;
  }

  private boolean subscriptionsActive() {
    return subscription != Subscription.EMPTY;
  }

  public boolean updateModel() {  // only ever called when applicable, as no subscriptions are present otherwise
    return model.trySet(getter.get(), converter);
  }

  private void doModelInitiatedChange(Runnable r) {
    if(!node.getPseudoClassStates().contains(DIRTY)) {
      modelInitiatedChange = true;

      try {
        r.run();
      }
      finally {
        modelInitiatedChange = false;
      }
    }
  }

  private void doControlInitiatedChange(Runnable r) {
    controlInitiatedChange = true;

    try {
      r.run();
    }
    finally {
      controlInitiatedChange = false;
    }
  }
}
