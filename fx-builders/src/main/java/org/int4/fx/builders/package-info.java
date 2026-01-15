/**
 * Entry point for declarative JavaFX builders.
 * <p>
 * This package provides a set of fluent builders for constructing JavaFX controls,
 * layout panes, shapes, and scenes. The builders allow for:
 * <ul>
 *   <li>Declarative scene graph construction without manually managing node variables.</li>
 *   <li>Automatic integration with {@link org.int4.fx.values.model.ValueModel} instances for
 *       reactive model-binding.</li>
 *   <li>Binding of common node properties (visibility, enablement, styles) to
 *       {@link javafx.beans.value.ObservableValue} instances.</li>
 *   <li>Nested builders, where child builders are automatically finalized when added
 *       to a parent builder.</li>
 *   <li>Support for custom nodes and regions via {@code customNode} and {@code customRegion}.</li>
 * </ul>
 * <p>
 * The main entry classes in this package are:
 * <ul>
 *   <li>{@link org.int4.fx.builders.FX} – factory methods for standard JavaFX controls.</li>
 *   <li>{@link org.int4.fx.builders.Panes} – factory methods for common layout panes.</li>
 *   <li>{@link org.int4.fx.builders.Shapes} – factory methods for JavaFX shapes.</li>
 *   <li>{@link org.int4.fx.builders.Scenes} – utilities for creating scenes with automatic show-state listener support.</li>
 * </ul>
 */
package org.int4.fx.builders;
