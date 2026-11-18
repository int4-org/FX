package org.int4.fx.layout.flex;

import javafx.geometry.Orientation;

/**
 * Builder group for {@link Flex} layouts.
 * <p>
 * This is a composition interface used to expose Flex builders alongside other
 * builder groups. The standard instance is available as {@link #FLEXES}.
 */
public interface Flexes {

  /**
   * The Flex-only builder factory instance.
   */
  Flexes FLEXES = new Flexes() {};

  /**
   * Creates a builder for a {@link Flex} with the given orientation.
   *
   * @param orientation the initial orientation, can be {@code null} to use
   *   {@link Orientation#HORIZONTAL}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  default FlexBuilder flex(Orientation orientation, String... styleClasses) {
    return Flex.of(orientation, styleClasses);
  }

  /**
   * Creates a builder for a horizontal {@link Flex}.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  default FlexBuilder hflex(String... styleClasses) {
    return Flex.of(Orientation.HORIZONTAL, styleClasses);
  }

  /**
   * Creates a builder for a vertical {@link Flex}.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  default FlexBuilder vflex(String... styleClasses) {
    return Flex.of(Orientation.VERTICAL, styleClasses);
  }
}
