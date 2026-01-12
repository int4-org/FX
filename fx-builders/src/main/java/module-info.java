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

