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
import org.int4.fx.core.util.Observe;
import org.int4.fx.core.util.RecordBasedTemplate;
import org.int4.fx.core.util.Template;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.model.RawValue;
import org.int4.fx.values.model.WritableModel;

/**
 * Orchestrates the bidirectional synchronization between a JavaFX {@link Node}
 * and a {@link WritableModel}, managing dirty state, focus, and validation.
 *
 * @param <N> the node type
 * @param <R> the raw value type from the control
 * @param <T> the model's value type
 */
final class ModelLinker<N extends Node, R, T> {
  public static boolean RESPOND_TO_TEST_FOCUS_EVENT;

  private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
  private static final PseudoClass TOUCHED = PseudoClass.getPseudoClass("touched");
  private static final PseudoClass DIRTY = PseudoClass.getPseudoClass("dirty");

  record Incompatible() implements RecordBasedTemplate {
    @Override
    public String key() {
      return "conversion.incompatible";
    }
  }

  static final Template INCOMPATIBLE_TEMPLATE = new Incompatible();

  public static <N extends Node, T> ModelLinker<N, T, T> link(N node, WritableModel<T> model, Supplier<T> getter, Consumer<T> setter) {
    return new ModelLinker<>(node, model, getter, toSynchronizer(setter), Function.identity(), r -> Subscription.EMPTY);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, WritableModel<T> model, Supplier<R> getter, Consumer<T> setter, Function<R, T> converter) {
    return new ModelLinker<>(node, model, getter, toSynchronizer(setter), converter, r -> Subscription.EMPTY);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, WritableModel<T> model, Supplier<R> getter, Consumer<T> setter, Function<R, T> converter, Function<Runnable, Subscription> trigger) {
    return new ModelLinker<>(node, model, getter, toSynchronizer(setter), converter, trigger);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> link(N node, WritableModel<T> model, Property<T> property) {
    return new ModelLinker<>(node, model, property);
  }

  public static <N extends Node, R, T> ModelLinker<N, R, T> sync(N node, WritableModel<T> model, Supplier<R> getter, Consumer<ControlCommand<T>> setter, Function<R, T> converter, Function<Runnable, Subscription> trigger) {
    return new ModelLinker<>(node, model, getter, setter, converter, trigger);
  }

  private static <T> Consumer<ControlCommand<T>> toSynchronizer(Consumer<T> setter) {
    return s -> {
      switch(s) {
        case ControlCommand.ModelValue(T value) -> setter.accept(value);
        case ControlCommand.RawValue(Object _) -> setter.accept(null);
        case ControlCommand.Inapplicable() -> setter.accept(null);
      }
    };
  }

  static class TestFocusEvent extends Event {
    public static final EventType<TestFocusEvent> MODIFY = new EventType<>(EventType.ROOT, "modify");

    final boolean focused;

    public TestFocusEvent(boolean focused) {
      super(MODIFY);

      this.focused = focused;
    }
  }

  @SuppressWarnings("unused")
  sealed interface ControlCommand<T> {
    record ModelValue<T>(T value) implements ControlCommand<T> {}
    record RawValue<T>(Object rawValue) implements ControlCommand<T> {}
    record Inapplicable<T>() implements ControlCommand<T> {}
  }

  private final N node;
  private final WritableModel<T> model;
  private final Supplier<R> getter;
  private final Consumer<ControlCommand<T>> setter;
  private final Function<R, T> converter;
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

  private R controlRawValue;
  private boolean conversionFailed;
  private boolean modelInitiatedChange;
  private boolean controlInitiatedChange;

  @SuppressWarnings("unchecked")
  ModelLinker(N node, WritableModel<T> model, Property<T> property) {
    this(
      node,
      model,
      () -> (R)property.getValue(),
      s -> toSynchronizer(property::setValue),
      r -> (T)r,
      r -> property.subscribe((ov, nv) -> r.run())
    );
  }

  ModelLinker(
    N node,
    WritableModel<T> model,
    Supplier<R> getter,
    Consumer<ControlCommand<T>> setter,
    Function<R, T> converter,
    Function<Runnable, Subscription> trigger
  ) {
    this.node = Objects.requireNonNull(node, "node");
    this.model = Objects.requireNonNull(model, "model");
    this.getter = Objects.requireNonNull(getter, "getter");
    this.setter = Objects.requireNonNull(setter, "setter");
    this.converter = Objects.requireNonNull(converter, "converter");

    addSubscriber(() -> {
      node.visibleProperty().bind(model.applicable());
      node.managedProperty().bind(model.applicable());

      return () -> {
        node.visibleProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
        node.managedProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
      };
    });

    addSubscriber(() -> Observe.values(model.domain(), model.rawValue())
      .subscribe((d, rv) -> {
        if(!controlInitiatedChange) {
          if(!isDirty()) {  // should not propagate any model changes when control is dirty
            doModelInitiatedChange(() -> syncControl(rv));
          }

          refreshValidationState(rv);  // should still make control reflect validity of its value
        }
      })
    );

    addSubscriber(() -> trigger.apply(() -> {
      if(!modelInitiatedChange) {
        node.pseudoClassStateChanged(TOUCHED, true);
        node.pseudoClassStateChanged(DIRTY, true);

        doControlInitiatedChange(this::updateModel);

        refreshValidationState(model.getRawValue());
      }
    }));

    addSubscriber(() -> node.focusedProperty().subscribe((ov, focused) -> focusChanged(focused)));

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
      node.addEventHandler(TestFocusEvent.MODIFY, e -> {
        if(subscriptionsActive()) {
          focusChanged(e.focused);
        }
      });
    }
  }

  private void refreshValidationState(RawValue<T> rawValue) {
    applyValidationState(isDirty() ? evaluateCurrentInput() : rawValue);
  }

  private RawValue<T> evaluateCurrentInput() {
    if(!model.isApplicable()) {
      return getter.get() == null
        ? RawValue.valid(null)
        : RawValue.incompatible(INCOMPATIBLE_TEMPLATE);
    }

    try {
      T value = converter.apply(getter.get());

      return switch(model.getDomain().evaluate(value)) {
        case Membership.Member() -> RawValue.valid(value);
        case Membership.Excluded(Template reason) -> RawValue.invalid(value, reason);
      };
    }
    catch(Exception e) {
      return RawValue.incompatible(INCOMPATIBLE_TEMPLATE);
    }
  }

  public Subscription addSubscriber(Supplier<Subscription> subscriber) {
    subscribers.add(subscriber);

    return () -> subscribers.remove(subscriber);
  }

  private void activateAllSubscriptions() {
    if(!subscriptionsActive()) {
      // as subscribing may cause value changes, ensure they're not seen as user changes:
      doModelInitiatedChange(() -> {
        List<Subscription> subscriptions = new ArrayList<>();

        for(Supplier<Subscription> supplier : subscribers) {
          subscriptions.add(supplier.get());
        }

        subscription = Subscription.combine(subscriptions.toArray(Subscription[]::new));
      });
    }
  }

  private void terminateAllSubscriptions() {
    if(subscriptionsActive()) {

      /*
       * Handle DIRTY state first by flushing to model:
       */

      focusChanged(false);

      /*
       * Unsubscribe everything:
       */

      subscription.unsubscribe();
      subscription = Subscription.EMPTY;

      /*
       * When previously connected, and now disconnected, reset value, touched
       * and invalid states. New data may be present on next reconnect so none
       * of these values are sensible to keep:
       */

      doModelInitiatedChange(() -> setter.accept(new ControlCommand.Inapplicable<>()));

      controlRawValue = null;
      conversionFailed = false;

      node.pseudoClassStateChanged(TOUCHED, false);
      node.pseudoClassStateChanged(INVALID, false);
    }
  }

  private void focusChanged(boolean focused) {
    if(!focused && isDirty()) {

      /*
       * When focus is lost, and the control is dirty, update the model with
       * the control's value. The control is now no longer dirty.
       *
       * In the not dirty state, the control should always reflect the model.
       * Afterwards, the control should be synced with the model still because:
       *
       * 1. A valid model may have slightly different formatting due to a conversion (eg. 1 -> 1.0)
       * 2. An invalid model must be reflected as empty or null in the control
       */

      updateModel();

      node.pseudoClassStateChanged(DIRTY, false);

      RawValue<T> rawValue = model.getRawValue();

      doModelInitiatedChange(() -> syncControl(rawValue));
      applyValidationState(rawValue);
    }
  }

  private void syncControl(RawValue<T> rv) {
    setter.accept(model.isApplicable()
      ? switch(rv) {
          case RawValue.Incompatible(Template _) when conversionFailed -> new ControlCommand.RawValue<>(controlRawValue);
          default -> {
            conversionFailed = false;

            yield new ControlCommand.ModelValue<>(rv.orElse(null));
          }
        }
      : new ControlCommand.Inapplicable<>()
    );
  }

  private void applyValidationState(RawValue<T> value) {
    boolean valid = model.isApplicable() ? value instanceof RawValue.Valid : getter.get() == null;

    if(node.getPseudoClassStates().contains(INVALID) == valid) {
      node.pseudoClassStateChanged(INVALID, !valid);
    }
  }

  private boolean subscriptionsActive() {
    return subscription != Subscription.EMPTY;
  }

  public void updateModel() {  // only ever called when applicable, as no subscriptions are present otherwise
    assert subscriptionsActive();

    this.controlRawValue = getter.get();

    model.trySet(controlRawValue, converter, INCOMPATIBLE_TEMPLATE);

    this.conversionFailed = model.getRawValue() instanceof RawValue.Incompatible;
  }

  /**
   * Runs the given {@link Runnable} as a model initiated change. This means that any
   * change made, and any changes that may trigger won't be interpreted as a user interaction.
   *
   * @param runnable a {@link Runnable} to run, cannot be {@code null}
   */
  protected void doModelInitiatedChange(Runnable runnable) {
    boolean previousState = modelInitiatedChange;

    modelInitiatedChange = true;

    try {
      runnable.run();
    }
    finally {
      modelInitiatedChange = previousState;
    }
  }

  private boolean isDirty() {
    return node.getPseudoClassStates().contains(DIRTY);
  }

  private void doControlInitiatedChange(Runnable runnable) {
    boolean previousState = controlInitiatedChange;

    controlInitiatedChange = true;

    try {
      runnable.run();
    }
    finally {
      controlInitiatedChange = previousState;
    }
  }
}
