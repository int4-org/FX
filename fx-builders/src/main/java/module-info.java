/**
 * Provides fluent, declarative builders for JavaFX controls, layout panes, and shapes.
 * <p>
 * This module enables construction of JavaFX scene graphs in a model-driven, reactive style.
 * Builders support:
 * <ul>
 *   <li>Declarative creation of nodes, panes, and shapes with fluent API.</li>
 *   <li>Automatic integration with {@link org.int4.fx.values.model.ValueModel} for bidirectional binding.</li>
 *   <li>Reactive style and visibility binding via observable properties.</li>
 *   <li>Custom node and region builders for extensibility beyond standard JavaFX controls.</li>
 *   <li>Show-state-aware scene and listener management to prevent memory leaks.</li>
 * </ul>
 */
module org.int4.fx.builders {
  requires transitive javafx.base;
  requires transitive javafx.controls;
  requires transitive org.int4.fx.values;

  exports org.int4.fx.builders;
  exports org.int4.fx.builders.common;
  exports org.int4.fx.builders.control;
  exports org.int4.fx.builders.pane;
  exports org.int4.fx.builders.shape;
}

