package org.int4.fx.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A simple event trigger that allows multiple listeners to be notified when
 * an event occurs. Each listener is either a {@link Consumer} accepting a payload
 * of type {@code T}, or a {@link Runnable} if the payload is not needed.
 *
 * <p>This class can be used to decouple event sources from handlers in a
 * fluent or reactive style API.
 * <p>
 * Subscribers can either receive the payload of type {@code T} or ignore it:
 * <pre>{@code
 * Trigger<String> trigger = Trigger.of();
 * trigger.onFire(msg -> System.out.println("Received: " + msg));
 * trigger.onFire(() -> System.out.println("Received a message"));
 * trigger.fire("Hello");
 * }</pre>
 * This will output:
 * <pre>
 * Received: Hello
 * Received a message
 * </pre>
 *
 * @param <T> the type of the payload that is sent to listeners when the trigger fires
 */
public class Trigger<T> {

  /**
   * Creates a new trigger instance.
   *
   * @param <T> the type of the payload
   * @return a new {@link Trigger} instance
   */
  public static <T> Trigger<T> create() {
    return of();
  }

  /**
   * Creates a new trigger instance.
   *
   * @param <T> the type of the payload
   * @return a new {@link Trigger} instance
   */
  public static <T> Trigger<T> of() {
    return new Trigger<>();
  }

  private final List<Consumer<T>> consumers = new ArrayList<>();

  private Trigger() {
  }

  /**
   * Registers a listener that will be called with the payload whenever
   * the trigger fires.
   *
   * @param callback a {@link Consumer} to handle the payload, cannot be {@code null}
   * @throws NullPointerException if {@code callback} is {@code null}
   */
  public void onFire(Consumer<T> callback) {
    this.consumers.add(Objects.requireNonNull(callback, "callback"));
  }

  /**
   * Registers a listener that will be called whenever the trigger fires.
   * The listener does not receive the payload.
   *
   * @param callback a {@link Runnable} to execute when the trigger fires, cannot be {@code null}
   * @throws NullPointerException if {@code callback} is {@code null}
   */
  public void onFire(Runnable callback) {
    Objects.requireNonNull(callback, "callback");

    this.consumers.add(x -> callback.run());
  }

  /**
   * Fires the trigger, notifying all registered listeners with the given payload.
   *
   * @param payload the payload to send to listeners
   */
  public void fire(T payload) {
    for(Consumer<T> consumer : consumers) {
      consumer.accept(payload);
    }
  }
}
