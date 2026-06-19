/**
 * Contains basic FX utility classes which only depend on {@code javafx.base}.
 */
module org.int4.fx.core {
  requires transitive javafx.base;
  requires transitive org.int4.common.function;
  requires transitive org.int4.common.collection;

  exports org.int4.fx.core.event;
  exports org.int4.fx.core.util;
}

