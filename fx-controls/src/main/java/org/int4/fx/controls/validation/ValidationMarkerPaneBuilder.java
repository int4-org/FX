package org.int4.fx.controls.validation;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * A builder for {@link ValidationMarkerPane}.
 */
public class ValidationMarkerPaneBuilder extends AbstractRegionBuilder<ValidationMarkerPane, ValidationMarkerPaneBuilder> {

  /**
   * Creates a new builder with a context and optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public ValidationMarkerPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Sets the content of the {@link ValidationMarkerPane}.
   *
   * @param content the content, can be {@code null}
   * @return this builder, never {@code null}
   */
  public ValidationMarkerPaneBuilder content(Object content) {
    return applyContentStrategy(content, ValidationMarkerPane::setContent);
  }

  @Override
  public ValidationMarkerPane build(BuildContext context) {
    return initialize(context, new ValidationMarkerPane());
  }
}
