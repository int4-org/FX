package org.int4.fx.builders.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents an immutable, composable context for providing build-time strategies
 * to builders. A {@code BuildContext} allows registering, overriding, and resolving
 * strategies used during the construction of objects.
 * <p>
 * A {@link BuildContext} is typically passed down a builder hierarchy and may be
 * selectively overridden at any level.
 * <p>
 * Instances are immutable: methods return a new context without modifying the original.
 * <p>
 * Typical usage:
 * <pre>{@code
 * BuildContext context = BuildContext.of(TextStrategy.class, myTextStrategy)
 *     .with(ChildrenStrategy.class, myChildrenStrategy);
 * }</pre>
 */
public class BuildContext {

  /**
   * A static build context without any strategies.
   */
  static final BuildContext EMPTY = new BuildContext(Map.of());

  /**
   * Creates a new {@code BuildContext} containing a single strategy.
   * <p>
   * This is a convenience method for starting a context chain.
   *
   * @param <S> the type of strategy
   * @param type the class of the strategy, cannot be {@code null}
   * @param strategy the strategy instance, cannot be {@code null}
   * @return a new {@code BuildContext} containing the given strategy, never {@code null}
   * @throws NullPointerException if {@code type} or {@code strategy} is null
   */
  public static <S extends BuildStrategy<S>> BuildContext of(Class<S> type, S strategy) {
    return EMPTY.with(type, strategy);
  }

  /**
   * Creates a new {@code BuildContext} containing a single self-identifying strategy.
   * <p>
   * The strategy's {@link BuildStrategy#type()} method defines its identity.
   *
   * @param <S> the type of strategy
   * @param strategy the strategy instance, cannot be {@code null}
   * @return a new {@code BuildContext} containing the given strategy, never {@code null}
   * @throws NullPointerException if {@code strategy} is null
   */
  public static <S extends BuildStrategy<S>> BuildContext of(S strategy) {
    return EMPTY.with(strategy);
  }

  private final Map<Class<? extends BuildStrategy<?>>, Object> strategies;

  private BuildContext(Map<Class<? extends BuildStrategy<?>>, Object> strategies) {
    this.strategies = strategies;
  }

  /**
   * Returns a new {@code BuildContext} that includes the given strategy, overriding
   * any existing strategy of the same type.
   * <p>
   * Use this to add or replace a strategy in an existing context.
   *
   * @param <S> the type of strategy
   * @param strategy the strategy instance, cannot be {@code null}
   * @return a new {@code BuildContext} containing the added strategy, never {@code null}
   * @throws NullPointerException if {@code strategy} is null
   */
  public <S extends BuildStrategy<S>> BuildContext with(S strategy) {
    Objects.requireNonNull(strategy, "strategy");

    return merge(new BuildContext(Map.of(Objects.requireNonNull(strategy.type(), "strategy.type()"), strategy)));
  }

  /**
   * Returns a new {@code BuildContext} that includes the given strategy for the
   * specified type, overriding any existing strategy of the same type.
   * <p>
   * This overload is useful when supplying strategies as lambdas or anonymous classes
   * that cannot self-identify.
   *
   * @param <S> the type of strategy
   * @param type the class of the strategy, cannot be {@code null}
   * @param strategy the strategy instance, cannot be {@code null}
   * @return a new {@code BuildContext} containing the added strategy, never {@code null}
   * @throws NullPointerException if {@code type} or {@code strategy} is null
   */
  public <S extends BuildStrategy<S>> BuildContext with(Class<S> type, S strategy) {
    Objects.requireNonNull(strategy, "strategy");
    Objects.requireNonNull(type, "type");

    return merge(new BuildContext(Map.of(type, strategy)));
  }

  /**
   * Returns a new {@code BuildContext} with the strategy of the given type removed.
   * <p>
   * If the context does not contain a strategy of that type, the current context
   * is returned unchanged.
   *
   * @param <S> the type of strategy
   * @param cls the class of the strategy to remove
   * @return a new {@code BuildContext} without the specified strategy, never {@code null}
   */
  public <S extends BuildStrategy<S>> BuildContext remove(Class<S> cls) {
    if(cls == null || !strategies.containsKey(cls)) {
      return this;
    }

    Map<Class<? extends BuildStrategy<?>>, Object> map = new HashMap<>(strategies);

    map.remove(cls);

    return new BuildContext(map);
  }

  /**
   * Resolves a strategy from the context, using the supplied base strategy if
   * no strategy of that type has been registered.
   * <p>
   * This allows callers to provide a default while still respecting overrides
   * present in the context.
   *
   * @param <S> the type of strategy
   * @param defaultStrategySupplier a supplier returning the base/default strategy
   * @return the strategy from the context if present, otherwise the supplied base
   * @throws IllegalStateException if the supplier returns {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public <S extends BuildStrategy<S>> S resolve(Supplier<S> defaultStrategySupplier) {
    S instance = defaultStrategySupplier.get();  // base instance

    if(instance == null) {
      throw new IllegalStateException("strategy supplier must return a non-null value: " + defaultStrategySupplier);
    }

    @SuppressWarnings("unchecked")
    S strategy = (S)strategies.getOrDefault(instance.type(), instance);

    return strategy;
  }

  /**
   * Merges this context with the given context, overriding any strategies in
   * this context. The merged context is returned.
   *
   * @param other a context with overriding strategies, cannot be {@code null}
   * @return the merged context, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public BuildContext merge(BuildContext other) {
    Objects.requireNonNull(other, "other");

    Map<Class<? extends BuildStrategy<?>>, Object> merged = new HashMap<>();

    merged.putAll(strategies);
    merged.putAll(other.strategies);

    return new BuildContext(merged);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[strategies=" + strategies.keySet() + "]";
  }
}