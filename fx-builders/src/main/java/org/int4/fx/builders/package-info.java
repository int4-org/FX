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
 * <b>Build contexts and strategies:</b>
 * <p>
 * Builders are extensible through the use of <em>build strategies</em>, which
 * encapsulate specific aspects of how builder input is applied to JavaFX nodes.
 * Examples include strategies for handling text, content nodes, child nodes,
 * or other configurable concerns.
 * <p>
 * Strategies are resolved through a {@link org.int4.fx.builders.context.BuildContext},
 * which acts as an immutable registry of strategy implementations. A build context
 * may provide custom behavior by overriding one or more strategies while leaving
 * others unchanged.
 * <p>
 * Contexts are composable and hierarchical:
 * <ul>
 *   <li>A root context can be defined to act as a global default.</li>
 *   <li>Parent builders pass their context to child builders.</li>
 *   <li>Individual builders may supply a local context that partially overrides
 *       the inherited one.</li>
 * </ul>
 * <p>
 * During node construction, builders resolve the active context and apply the
 * relevant strategies. This allows application-wide customization of builder
 * behavior (such as localization, node conversion, or styling policies) without
 * modifying individual builders.
 * <p>
 * This mechanism is intentionally orthogonal to the fluent builder API: most users
 * will never need to interact with contexts or strategies directly, while advanced
 * users can leverage them to introduce cross-cutting behavior in a structured
 * and reusable way.
 * <p>
 * <b>Typical usage:</b>
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
 *   <li>{@link org.int4.fx.builders.Scenes} – utilities for creating scenes.</li>
 * </ul>
 */
package org.int4.fx.builders;
