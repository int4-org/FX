package org.int4.fx.builders;

/**
 * Composite entry point for the fluent JavaFX builders.
 * <p>
 * {@link #FX} is the standard all-in-one factory, combining the builder groups
 * {@link Controls}, {@link Panes}, and {@link Shapes}. Scene construction is
 * provided separately by {@link Scenes}. Use {@link Controls#CONTROLS},
 * {@link Panes#PANES}, or {@link Shapes#SHAPES} when a narrower import is
 * preferred.
 * <p>
 * Builders are configured fluently; configuration methods return the builder,
 * and a terminal method such as {@code build()}, {@code with(...)}, or
 * {@code of(...)} creates the JavaFX object. Builders supplied as content or
 * children are finalized automatically. Strings are converted to labels and
 * other values are interpreted by the active build strategies; see
 * {@link org.int4.fx.builders.context.BuildContext}.
 * <p>
 * Applications can extend this interface with additional default methods and
 * expose an application-specific {@code FX} entry point.
 */
public interface CoreFX extends Controls, Panes, Shapes {

  /**
   * The standard builder factory instance.
   * <p>
   * Import this field statically to access the builder methods:
   * <pre>{@code
   * import static org.int4.fx.builders.CoreFX.FX;
   *
   * FX.label("header").text("Title");
   * }</pre>
   * Applications can extend this interface with additional default methods and
   * expose their own {@code FX} field to provide an extended builder vocabulary.
   */
  CoreFX FX = new CoreFX() {};
}
