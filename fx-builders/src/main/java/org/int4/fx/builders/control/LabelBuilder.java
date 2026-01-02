package org.int4.fx.builders.control;

import javafx.scene.control.Label;

/**
 * Builder for {@link Label} instances.
 */
public final class LabelBuilder extends AbstractLabeledBuilder<Label, LabelBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public LabelBuilder(String... styleClasses) {
    super(Label::new, styleClasses);
  }

  /**
   * Configures this label as a mnemonic target for the node with the given id.
   * <p>
   * Mnemonic parsing is enabled automatically. The target node is resolved
   * once the label is attached to a scene.
   *
   * @param id the id of the target node
   * @return the fluent builder, never {@code null}
   * @see Label#setLabelFor(javafx.scene.Node)
   * @see Label#setMnemonicParsing(boolean)
   */
  public LabelBuilder target(String id) {
    return apply(c -> {
      c.setMnemonicParsing(true);
      c.sceneProperty().subscribe(s -> {
        if(s != null) {
          c.setLabelFor(s.getRoot().lookup("#" + id));
        }
      });
    });
  }
}