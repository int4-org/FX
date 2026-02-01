/**
 * Build-time context and strategy infrastructure for the fluent JavaFX builders.
 * <p>
 * This package defines the mechanisms by which builder behavior can be customized,
 * overridden, and composed without changing the builders themselves. It introduces
 * the concept of a {@link org.int4.fx.builders.context.BuildContext}, which acts as an immutable container for
 * {@link org.int4.fx.builders.context.BuildStrategy strategies} that influence how builder inputs are interpreted
 * and applied during node construction.
 * <p>
 * <b>Build contexts</b>
 * <p>
 * A {@link org.int4.fx.builders.context.BuildContext} represents a set of strategies available during a build
 * operation. Contexts are immutable and composable:
 * <ul>
 *   <li>Adding or removing a strategy produces a new context.</li>
 *   <li>Contexts can be merged, with later contexts overriding earlier ones.</li>
 *   <li>Contexts are typically passed down a builder hierarchy and may be partially
 *       overridden at any level.</li>
 * </ul>
 * <p>
 * <b>Active and root contexts</b>
 * <p>
 * The {@link org.int4.fx.builders.context.BuildContexts} utility class manages the currently active context
 * using thread-local storage. It also defines a configurable root context that
 * serves as the default starting point for all build operations.
 * <p>
 * The root context is intended to be configured once during application startup,
 * before builders are used, and provides a convenient way to define application-wide
 * defaults such as localization, node conversion, or child handling behavior.
 * <p>
 * <b>Build strategies</b>
 * <p>
 * A {@link org.int4.fx.builders.context.BuildStrategy} defines a single, focused piece of build-time behavior
 * (for example: applying text, resolving children, or converting content). Strategies
 * are identified by their strategy type rather than by instance identity.
 * <p>
 * Builders resolve strategies from the active {@link org.int4.fx.builders.context.BuildContext}, supplying a
 * base strategy that is used when no override is present. This enables:
 * <ul>
 *   <li>Declarative defaults with selective overrides</li>
 *   <li>Context-sensitive behavior without builder specialization</li>
 *   <li>Extension of builder behavior for advanced use cases</li>
 * </ul>
 */
package org.int4.fx.builders.context;
