package org.int4.fx.builders.context;

/**
 * Marker interface for build-time strategies.
 * <p>
 * A {@code BuildStrategy} defines a customizable behavior that can be
 * supplied, overridden, and resolved from a {@code BuildContext}.
 * <p>
 * Each strategy is identified by a stable strategy type, typically the
 * implementing interface itself, which is used to resolve and override
 * strategies within a context.
 *
 * @param <S> the self type of the strategy
 */
public interface BuildStrategy<S> {

  /**
   * Returns the identifying type of this strategy.
   * <p>
   * The returned class is used to associate and resolve strategy instances
   * within a build context. Implementations typically return their own
   * interface type.
   *
   * @return the identifying strategy type, never {@code null}
   */
  Class<S> type();
}
