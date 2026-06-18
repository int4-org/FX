/**
 * Provides specialized JavaFX controls, validation infrastructure, and marker overlays.
 * <p>
 * This module contains custom JavaFX components that integrate with the
 * builder and core modules to provide high-level UI features.
 */
module org.int4.fx.controls {
  requires transitive org.int4.fx.builders;
  requires transitive org.int4.fx.core;

  exports org.int4.fx.controls.validation;
  exports org.int4.fx.controls.marker;
}

