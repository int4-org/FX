package org.int4.fx.builders.control;

import javafx.css.PseudoClass;

public class ControlBuilderTest extends FXTest {
  protected static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
  protected static final PseudoClass TOUCHED = PseudoClass.getPseudoClass("touched");
  protected static final PseudoClass DIRTY = PseudoClass.getPseudoClass("dirty");

  static {
    ModelLinker.RESPOND_TO_TEST_FOCUS_EVENT = true;
  }
}
