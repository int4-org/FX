package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link Label} instances.
 */
public final class LabelBuilder extends AbstractControlBuilder<Label, LabelBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public LabelBuilder(String... styleClasses) {
    super(Label::new, styleClasses);
  }

  /**
   * Sets the text of the label.
   *
   * @param text the label text
   * @return the fluent builder, never {@code null}
   * @see Label#setText(String)
   */
  public LabelBuilder text(String text) {
    return apply(c -> c.setText(text));
  }

  /**
   * Binds the text of the label to the given observable value.
   *
   * @param value the observable text value, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @see Label#textProperty()
   */
  public LabelBuilder text(ObservableValue<String> value) {
    return apply(c -> c.textProperty().bind(value));
  }

  /**
   * Enables text wrapping on the label.
   *
   * @return the fluent builder, never {@code null}
   * @see Label#setWrapText(boolean)
   */
  public LabelBuilder wrapText() {
    return apply(c -> c.setWrapText(true));
  }

  /**
   * Sets the graphic of the label.
   *
   * @param graphic a graphic node or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code graphic} is {@code null}
   * @see Label#setGraphic(javafx.scene.Node)
   */
  public LabelBuilder graphic(Object graphic) {
    Objects.requireNonNull(graphic, "graphic");

    return apply(c -> c.setGraphic(Builders.toNode(graphic)));
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