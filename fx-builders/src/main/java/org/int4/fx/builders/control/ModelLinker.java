package org.int4.fx.builders.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.util.Subscription;

import org.int4.common.collection.Immutable;
import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.builders.internal.CommitEvent;
import org.int4.fx.builders.internal.ShowingStateListener;
import org.int4.fx.core.event.BroadcastHandler;
import org.int4.fx.core.event.ValidationEvent;
import org.int4.fx.core.util.Observe;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Membership;
import org.int4.fx.values.model.WritableModel;

/**
 * Orchestrates bidirectional synchronization between a JavaFX {@link Node}
 * and a {@link WritableModel}, managing connection state, editing state,
 * validation, and value synchronization.
 *
 * <h3>Connection States</h3>
 * Synchronization is active only while the control is part of an active scene
 * and the model is applicable.
 * <ul>
 * <li><b>Disconnected (Scene Graph):</b> When the control leaves the active
 * scene, all listeners are terminated to prevent memory leaks. The control is
 * left in its current state and no longer participates in model synchronization.
 * When reattached, the current model state is immediately replayed to restore
 * synchronization.</li>
 * <li><b>Paused (Model Inapplicability):</b> When the model becomes
 * inapplicable, synchronization is suspended and the current editing session
 * ends. Controls typically adapt themselves through the domain change callback,
 * for example by clearing domain-derived state such as available options or
 * ranges. When the model becomes applicable again, synchronization resumes from
 * the model's current state.</li>
 * </ul>
 *
 * <h3>Editing State & Domain Changes</h3>
 * A control enters an editing (dirty) state after accepting user input and
 * remains dirty until the editing session ends.
 * While dirty, model-driven value synchronization is suppressed and validation
 * is performed against the current control input.
 * <ul>
 * <li><b>Value Persistence:</b> Model value changes do not overwrite the
 * control's current input while it is dirty.</li>
 * <li><b>Domain Transitions:</b> Applicable domain changes immediately
 * re-evaluate the current input. Controls capable of representing
 * out-of-domain values may preserve the user's input and mark it invalid,
 * while constrained controls (for example a {@code Slider}) may be forced by
 * their implementation to clamp or otherwise adapt the value.</li>
 * <li><b>Editing Session End:</b> Losing focus, becoming inapplicable, or
 * disconnecting from the scene ends the editing session and discards any
 * remaining transient editing state.</li>
 * <li><b>Focus Loss:</b> On focus loss, the current input is synchronized with
 * the model and the control is refreshed from the model's value, allowing its
 * presentation to be normalized (for example {@code 1} becoming
 * {@code 1.0}) while preserving its semantic value.</li>
 * </ul>
 *
 * @param <N> the node type
 * @param <T> the model's value type
 */
final class ModelLinker<N extends Node, T> {
  public static boolean RESPOND_TO_TEST_FOCUS_EVENT;

  private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
  private static final PseudoClass TOUCHED = PseudoClass.getPseudoClass("touched");
  private static final PseudoClass DIRTY = PseudoClass.getPseudoClass("dirty");

  /*
   * All in one; no linker is returned on purpose, as it is fully configured here. If not statisfactory,
   * use sync.
   */
  public static <N extends Node, T> void link(N node, WritableModel<T> model, Property<T> property) {
    ModelLinker<N, T> linker = new ModelLinker<>(node, model, toSynchronizer(property::setValue));

    property.subscribe(linker::push);

    linker.onDomainChanged(d -> {
      if(d.equals(Domain.inapplicable())) {
        property.setValue(null);
      }
    });
  }

  /*
   * When using sync, must use push/pushIncompatible, and probably register a domain listener via onDomainChanged
   * to reset the control when its model domain becomes inapplicable.
   */
  public static <N extends Node, T> ModelLinker<N, T> sync(N node, WritableModel<T> model, Consumer<RawValue<T>> setter) {
    return new ModelLinker<>(node, model, setter);
  }

  private static <T> Consumer<RawValue<T>> toSynchronizer(Consumer<T> setter) {
    return rv -> setter.accept(switch(rv) {
      case RawValue.Converted<T> c -> c.value();
      case RawValue.Incompatible(_) -> null;
    });
  }

  static class TestFocusEvent extends Event {
    public static final EventType<TestFocusEvent> MODIFY = new EventType<>(EventType.ROOT, "modify");

    final boolean focused;

    public TestFocusEvent(boolean focused) {
      super(MODIFY);

      this.focused = focused;
    }
  }

  /*
   * Synchronization protocol:
   *
   * - Only compatible values are synchronized to controls via RawValue:
   *   - RawValue.Valid
   *   - RawValue.Invalid
   *
   * - RawValue.Incompatible is never echoed back to the originating control,
   *   allowing it to continue displaying the user's incompatible input while
   *   showing it as invalid.
   *
   * - Other controls attached to the same model do receive RawValue.Incompatible,
   *   allowing them to clear their displayed value, as the model cannot represent
   *   or propagate the original incompatible input.
   *
   * - Controls are responsible for adapting to domain changes through the
   *   onDomainChanged callback. In particular, transitioning to the inapplicable
   *   domain is handled there (for example by clearing items or ranges); the
   *   linker does not issue any additional synchronization for this.
   *
   * - Scene attachment only controls whether synchronization is active.
   *   Disconnecting from the scene leaves the control unchanged. Upon
   *   reconnection, the current model state and domain are replayed immediately,
   *   restoring synchronization.
   *
   * Synchronization notes:
   *
   * - Value synchronization only occurs while the model is applicable. When the
   *   model becomes inapplicable, the linker no longer synchronizes values or
   *   propagates validation state.
   *
   * - Domain changes are treated differently: the onDomainChanged callback is
   *   always invoked (if connected to a scene), including transitions to the
   *   inapplicable domain and the initial callback after reconnect. Controls are
   *   expected to update any domain-derived state (for example items, ranges or
   *   other restrictions) from this callback.
   *
   * - Consequently, reconnecting to an inapplicable model may still cause the
   *   control to change via onDomainChanged, even though no value
   *   synchronization takes place. This is intentional: structural adaptation is
   *   driven by the domain, while value synchronization is suspended.
   *
   * - RawValue.Incompatible is not echoed back to the originating control,
   *   allowing it to continue displaying the user's incompatible input. Other
   *   controls attached to the same model do receive the incompatible state,
   *   since the original incompatible input itself is not represented by the
   *   model.
   */

  private enum Origin { NONE, MODEL, CONTROL }

  private final N node;
  private final WritableModel<T> model;
  private final Consumer<RawValue<T>> setter;
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

  private T controlInput;
  private Template incompatibleReason;  // if non-null it means controlInput was incompatible
  private Origin changeOrigin = Origin.NONE;
  private Consumer<Domain<T>> onDomainChanged;
  private Domain<T> domain;

  private ModelLinker(N node, WritableModel<T> model, Consumer<RawValue<T>> setter) {
    this.node = Objects.requireNonNull(node, "node");
    this.model = Objects.requireNonNull(model, "model");
    this.setter = Objects.requireNonNull(setter, "setter");

    addSubscriber(() -> {
      node.visibleProperty().bind(model.applicable());
      node.managedProperty().bind(model.applicable());

      return () -> {
        node.visibleProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
        node.managedProperty().unbind();  // not strictly needed, weak bound (but this is more deterministic)
      };
    });

    addSubscriber(() -> Observe.values(model.domain(), model.rawValue()).subscribe((d, rv) -> {
      boolean domainChanged = !Objects.equals(d, domain);

      this.domain = d;

      if(domainChanged && onDomainChanged != null) {
        doOriginatedChange(Origin.MODEL, () -> onDomainChanged.accept(d));
      }

      if(changeOrigin != Origin.CONTROL) {
        // should not propagate any model changes when control is dirty or model is not applicable
        if(model.isApplicable()) {
          if(!isDirty()) {
            doOriginatedChange(Origin.MODEL, () -> setter.accept(rv));
          }

          refreshValidationState(rv);  // should still make control reflect validity of its value as domain may have changed
        }
        else {
          disconnect();
        }
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

    node.addEventHandler(CommitEvent.COMMIT, e -> {
      if(isConnected()) {
        updateModel();
      }
    });

    if(RESPOND_TO_TEST_FOCUS_EVENT) {
      node.addEventHandler(TestFocusEvent.MODIFY, e -> {
        if(subscriptionsActive()) {
          focusChanged(e.focused);
        }
      });
    }
  }

  /**
   * Sets a consumer that is called whenever the domain changes. This is the preferred
   * way to monitor model domain changes, as the callback is called just before a control
   * command will be sent, allowing controls to first set up their restrictions, and then
   * receive the control command which sets the expected value.
   *
   * @param consumer a {@link Consumer}, can be {@code null}
   */
  public void onDomainChanged(Consumer<Domain<T>> consumer) {
    this.onDomainChanged = consumer;
  }

  /**
   * Pushes a value from the control to the model. The value is ignored if the model is
   * not connected.
   *
   * @param newValue a new value, can be {@code null}
   */
  public void push(T newValue) {
    if(isConnected()) {
      acceptControlInput(newValue, null);
    }
  }

  /**
   * Pushes an incompatible value from the control to the model. This will set a {@link RawValue}
   * on the model to {@code Incompatible}, and a template {@code conversion.incompatible}. The
   * template will contain the raw value as its first argument under the key {@code value}.
   *
   * @param raw a new raw value, can be {@code null}
   */
  public void pushIncompatible(Object raw) {
    if(isConnected()) {
      acceptControlInput(
        null,
        Template.of(
          "conversion.incompatible",
          Immutable.sequencedMap("value", raw)
        )
      );
    }
  }

  private void acceptControlInput(T newValue, Template incompatibleReason) {

    // if it is not model initiated, then the control becomes dirty:
    if(changeOrigin != Origin.MODEL) {
      node.pseudoClassStateChanged(TOUCHED, true);
      node.pseudoClassStateChanged(DIRTY, true);
    }

    /*
     * Only if the control is dirty (or just became dirty) accept any of its inputs, even if triggered by a model
     * change; this can happen when the domain of the model changes to a more restrictive domain, and the control
     * aggressively clamps its current value.
     */

    if(isDirty()) {
      this.controlInput = newValue;
      this.incompatibleReason = incompatibleReason;
    }

    // if the new input came from the control, forward to the model:
    if(changeOrigin != Origin.MODEL) {
      doOriginatedChange(Origin.CONTROL, this::updateModel);
    }

    /*
     * A rare situation can occur:
     *
     * - Control is dirty
     * - Domain changes (model initiated change)
     * - Control is updated to reflect limits of new domain, which happens to exclude the current value
     * - Some controls clamp the value (Slider) even if the control is dirty, altering user input
     * - After clamping, the value must be valid, and since it is still dirty, this overrules model validity
     *
     * In that case, even for a model initiated change, the validation state must be refreshed here:
     */

    refreshValidationState(model.getRawValue());
  }

  private Subscription addSubscriber(Supplier<Subscription> subscriber) {
    subscribers.add(subscriber);

    return () -> subscribers.remove(subscriber);
  }

  private void activateAllSubscriptions() {
    if(!subscriptionsActive()) {
      // as subscribing may cause value changes, ensure they're not seen as user changes:
      doOriginatedChange(Origin.MODEL, () ->
        subscription = Subscription.combine(subscribers.stream().map(Supplier::get).toArray(Subscription[]::new))
      );
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

      Subscription s = subscription;

      subscription = Subscription.EMPTY;  // empty it first, so subscriptionsActive will return false, as unsubscribes can trigger control updates that should be ignored immediately

      s.unsubscribe();

      /*
       * When previously connected, and now disconnected, reset value, touched
       * and invalid states. New data may be present on next reconnect so none
       * of these values are sensible to keep:
       */

      domain = null;  // ensures that on reconnect, a domain change is always triggered

      disconnect();
    }
  }

  private void focusChanged(boolean focused) {
    if(!focused && isDirty()) {

      /*
       * Focus loss ends the editing session.
       *
       * If the control is dirty, its current value is (once more) written to the model.
       * The control is then synchronized from the model state and normalized (eg. 1 -> 1.0),
       * if not incompatible, to match model representation rules.
       *
       * Transient editing state is cleared regardless of whether a model update occurred.
       */

      updateModel();

      RawValue<T> rawValue = model.getRawValue();

      if(rawValue.isPresent()) {
        doOriginatedChange(Origin.MODEL, () -> setter.accept(rawValue));
      }

      refreshValidationState(rawValue);
      resetEditingState();
    }
  }

  private void disconnect() {
    resetEditingState();

    node.pseudoClassStateChanged(TOUCHED, false);
    node.pseudoClassStateChanged(INVALID, false);
  }

  private void resetEditingState() {
    node.pseudoClassStateChanged(DIRTY, false);

    controlInput = null;
    incompatibleReason = null;
  }

  private void refreshValidationState(RawValue<T> rawValue) {
    RawValue<T> value = isDirty() ? evaluateCurrentInput() : rawValue;
    boolean valid = !model.isApplicable() || value instanceof RawValue.Valid;

    node.pseudoClassStateChanged(INVALID, !valid);
    node.fireEvent(switch(value) {
      case RawValue.Valid(_) -> ValidationEvent.valid();
      case RawValue.Invalid(Object v, Template template) -> ValidationEvent.invalid(template, v, false);
      case RawValue.Incompatible(Template template) -> ValidationEvent.invalid(template, null, true);
    });
  }

  private RawValue<T> evaluateCurrentInput() {
    if(incompatibleReason != null) {
      return RawValue.incompatible(incompatibleReason);
    }

    return switch(model.getDomain().evaluate(controlInput)) {
      case Membership.Member() -> RawValue.valid(controlInput);
      case Membership.Excluded(Template reason) -> RawValue.invalid(controlInput, reason);
    };
  }

  private void updateModel() {  // only ever called when applicable, as no subscriptions are present otherwise
    assert isConnected();

    if(incompatibleReason != null) {
      model.trySet(
        null,
        x -> { throw new RuntimeException(); },  // TODO ugly, to be resolved when trySet is replaced
        incompatibleReason
      );
    }
    else {
      model.setValue(controlInput);
    }
  }

  /**
   * Runs the given {@link Runnable} as a change with a specific origin. This means that any
   * change made by the given {@link Runnable}, and any changes that may trigger, won't be interpreted
   * as a change of a different origin.
   *
   * @param runnable a {@link Runnable} to run, cannot be {@code null}
   */
  private void doOriginatedChange(Origin origin, Runnable runnable) {
    Origin previous = changeOrigin;

    assert origin != Origin.NONE;
    assert previous == Origin.NONE || previous == origin;

    this.changeOrigin = origin;

    try {
      runnable.run();
    }
    finally {
      this.changeOrigin = previous;
    }
  }

  /**
   * Returns the current dirty state of the control. A dirty control will ignore model
   * changes until the control loses focus or becomes clean in some other way.
   *
   * @return {@code true} if the control is in a dirty state, otherwise {@code false}
   */
  private boolean isDirty() {
    return node.getPseudoClassStates().contains(DIRTY);
  }

  private boolean isConnected() {
    return subscriptionsActive() && model.isApplicable();
  }

  private boolean subscriptionsActive() {
    return subscription != Subscription.EMPTY;
  }
}
