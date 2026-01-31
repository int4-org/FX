package org.int4.fx.builders.shape;

import javafx.scene.shape.Path;

import org.int4.fx.builders.common.AbstractShapeBuilder;

/**
 * Builder for {@link Path} instances.
 */
public final class PathBuilder extends AbstractShapeBuilder<Path, PathBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public PathBuilder(String... styleClasses) {
    super(Path::new, styleClasses);
  }
}