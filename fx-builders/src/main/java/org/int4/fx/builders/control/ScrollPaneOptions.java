package org.int4.fx.builders.control;

import javafx.scene.control.ScrollPane;

import org.int4.common.builder.OptionBuilder;
import org.int4.fx.builders.common.AbstractNodeBuilder;

/**
 * Configuration options shared by builders that create a {@link ScrollPane}.
 *
 * @param <B> the concrete builder type
 */
interface ScrollPaneOptions<B extends AbstractNodeBuilder<ScrollPane, B>> extends OptionBuilder<ScrollPane, B> {

  /**
   * Configures the scroll pane to fit its content to the available width.
   *
   * @return the fluent builder, never {@code null}
   * @see ScrollPane#fitToWidthProperty()
   */
  default B fitToWidth() {
    return apply(c -> c.setFitToWidth(true));
  }

  /**
   * Configures the scroll pane to fit its content to the available height.
   *
   * @return the fluent builder, never {@code null}
   * @see ScrollPane#fitToHeightProperty()
   */
  default B fitToHeight() {
    return apply(c -> c.setFitToHeight(true));
  }
}
