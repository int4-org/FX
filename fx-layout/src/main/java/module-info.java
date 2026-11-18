/**
 * Provides the {@code Flex} layout container..
 */
module org.int4.fx.layout {
  requires transitive javafx.base;
  requires transitive javafx.graphics;
  requires transitive org.int4.fx.builders;
  requires org.int4.common.function;

  exports org.int4.fx.layout.flex;
}
