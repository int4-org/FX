/**
 * Concrete build strategies used by the fluent JavaFX builders.
 * <p>
 * This package contains strategy interfaces that define how specific aspects
 * of builder input are applied to JavaFX nodes during construction. Each strategy
 * encapsulates a single area of behavior, such as:
 * <ul>
 *   <li>Applying textual content to labeled controls</li>
 *   <li>Resolving and attaching child nodes</li>
 *   <li>Setting single content nodes for containers or controls</li>
 *   <li>Converting arbitrary builder input into JavaFX nodes</li>
 * </ul>
 * <p>
 * <b>Extensibility</b>
 * <p>
 * Applications may provide custom implementations of these strategy interfaces
 * to introduce cross-cutting behavior such as localization, node transformation,
 * styling policies, or alternative content resolution. Custom strategies can be
 * registered and composed through build contexts.
 */
package org.int4.fx.builders.strategy;
