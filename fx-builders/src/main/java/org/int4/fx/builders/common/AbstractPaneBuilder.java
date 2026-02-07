package org.int4.fx.builders.common;

import javafx.scene.layout.Pane;

/**
 * Base class for builders that create JavaFX {@link Pane} instances.
 *
 * @param <T> the concrete {@link Pane} type being built
 * @param <B> the concrete builder type (self type)
 */
public abstract class AbstractPaneBuilder<T extends Pane, B extends AbstractPaneBuilder<T, B>> extends AbstractNodeBuilder<T, B> {

  /**
   * Creates a new region builder, initializing the resulting region with
   * the given style classes.
   *
   * @param styleClasses the style classes to apply, cannot be {@code null} but may be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  protected AbstractPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }
}
