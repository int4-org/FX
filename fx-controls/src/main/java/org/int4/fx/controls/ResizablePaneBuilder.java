package org.int4.fx.controls;

import javafx.scene.control.ScrollPane;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Builder for {@link ResizablePane} instances.
 */
public final class ResizablePaneBuilder extends AbstractRegionBuilder<ResizablePane, ResizablePaneBuilder> {

  /**
   * Creates a new builder with a context and optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ResizablePaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Creates the resizable pane with the given content.
   *
   * @param content a content node or ResizablePane supported object, cannot be {@code null}
   * @return the created {@link ScrollPane}, never {@code null}
   * @throws IllegalArgumentException if {@code content} is {@code null}
   * @see ResizablePane#setContent(javafx.scene.Node)
   */
  public ResizablePaneBuilder content(Object content) {
    return applyContentStrategy(content, ResizablePane::setContent);
  }

  @Override
  public ResizablePane build(BuildContext context) {
    return initialize(context, new ResizablePane());
  }
}
