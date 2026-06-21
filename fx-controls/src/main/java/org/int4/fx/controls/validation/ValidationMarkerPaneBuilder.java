package org.int4.fx.controls.validation;

import java.util.function.Consumer;

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

  /**
   * Sets a handler for when a new marker is create.
   *
   * @param markerCreatedHandler a handler, can be {@code null}
   * @return this builder, never {@code null}
   */
  public ValidationMarkerPaneBuilder onMarkerCreated(Consumer<Marker> markerCreatedHandler) {
    return apply(n -> n.setOnMarkerCreated(markerCreatedHandler));
  }

  @Override
  public ValidationMarkerPane build(BuildContext context) {
    return initialize(context, new ValidationMarkerPane());
  }
}
