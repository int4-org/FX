/**
 * Utility helpers for building declarative, reactive business and UI logic
 * on top of JavaFX observables.
 * <p>
 * This package provides lightweight infrastructure classes that complement
 * JavaFX's observable APIs without introducing additional framework concepts.
 * It focuses on:
 * <ul>
 *   <li>Lazy composition of {@link javafx.beans.value.ObservableValue ObservableValue}
 *       instances, allowing derived values to be computed only while observed
 *       ({@link org.int4.fx.values.util.Observe}).</li>
 *   <li>Simple event-style signaling to decouple producers and consumers in
 *       declarative or builder-based UIs ({@link org.int4.fx.values.util.Trigger}).</li>
 * </ul>
 * <p>
 * The utilities are designed for deterministic listener management, clear
 * semantics, and seamless integration with JavaFX application-thread usage.
 */
package org.int4.fx.values.util;