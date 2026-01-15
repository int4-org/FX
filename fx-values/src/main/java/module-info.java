/**
 * Domain-driven value and model abstractions for JavaFX with explicit validity,
 * applicability, and UI-oriented constraints.
 * <p>
 * This module defines reusable value domains that describe validity rules and
 * optional UI-relevant views, modifiable observable models that expose
 * domain-based validation and applicability semantics, and utility helpers for
 * composing observables and structuring declarative UI logic.
 */
module org.int4.fx.values {
  requires transitive javafx.base;
  requires transitive org.int4.common.function;

  exports org.int4.fx.values.domain;
  exports org.int4.fx.values.model;
  exports org.int4.fx.values.util;
}