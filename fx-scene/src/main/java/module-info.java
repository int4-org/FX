/**
 * Contains basic FX utility classes which depend on {@code javafx.graphics}.
 */
module org.int4.fx.scene {
  requires transitive javafx.base;
  requires transitive javafx.graphics;
  requires transitive org.int4.fx.core;

  exports org.int4.fx.scene.event;
}

