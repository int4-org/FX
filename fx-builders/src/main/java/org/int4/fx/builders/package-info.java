/**
 * Fluent builders for constructing JavaFX controls, layout panes, shapes, scenes and stylesheets.
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
 * <b>Builder conventions:</b>
 * <ul>
 *   <li>All builders follow a consistent fluent pattern: configuration methods register
 *       deferred options that are applied when the builder is finalized.</li>
 *   <li>Modifier methods (such as {@code style()}, {@code id()}, {@code prefWidth()}) return
 *       the builder itself and can be freely chained.</li>
 *   <li>Terminal methods finalize the builder and create the underlying JavaFX object:
 *     <ul>
 *       <li>{@code build()} creates the node directly.</li>
 *       <li>{@code with(...)} is primarily used by pane builders to add child nodes and
 *           finalize that branch of the scene graph.</li>
 *       <li>{@code of(...)} is a convenience for shape builders to supply mandatory
 *           construction parameters (for example {@code Shapes.circle().of(5.0, Color.WHITE)}),
 *           but the same shapes can also be configured using modifier methods followed
 *           by {@code build()}.</li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * Typical usage:
 * <pre>{@code
 * FX.textField("from-field")
 *   .promptText("Name")
 *   .model(myModel);
 *
 * Panes.hbox(
 *   "Name:",
 *   FX.textField().model(nameModel)
 * );
 * }</pre>
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
