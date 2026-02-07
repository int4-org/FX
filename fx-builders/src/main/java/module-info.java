/**
 * Provides fluent, declarative builders for JavaFX controls, layout panes, and shapes.
 * <p>
 * This module enables construction of JavaFX scene graphs in a model-driven, reactive style.
 * Builders support:
 * <ul>
 *   <li>Declarative creation of nodes, panes, and shapes through a fluent API.</li>
 *   <li>Automatic integration with {@link org.int4.fx.values.model.ValueModel} for bidirectional binding.</li>
 *   <li>Reactive style and visibility binding via observable properties.</li>
 *   <li>Extensible build-time behavior through configurable build contexts and strategies.</li>
 *   <li>Custom node and region builders for extensibility beyond standard JavaFX controls.</li>
 *   <li>Show-state-aware scene and listener management to help prevent memory leaks.</li>
 * </ul>
 * <p>
 * Advanced users can customize builder behavior globally or locally by supplying
 * alternative build strategies via build contexts, without modifying individual
 * builders or fluent APIs.
 */
module org.int4.fx.builders {
  requires transitive javafx.base;
  requires transitive javafx.controls;
  requires transitive org.int4.fx.core;
  requires transitive org.int4.fx.scene;
  requires transitive org.int4.fx.values;

  exports org.int4.fx.builders;
  exports org.int4.fx.builders.common;
  exports org.int4.fx.builders.context;
  exports org.int4.fx.builders.control;
  exports org.int4.fx.builders.custom;
  exports org.int4.fx.builders.event;
  exports org.int4.fx.builders.pane;
  exports org.int4.fx.builders.shape;
  exports org.int4.fx.builders.strategy;
}

