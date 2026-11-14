package org.int4.fx.builders;

import org.int4.fx.builders.pane.BorderPaneBuilder;
import org.int4.fx.builders.pane.FlowPaneBuilder;
import org.int4.fx.builders.pane.GridPaneBuilder;
import org.int4.fx.builders.pane.GroupBuilder;
import org.int4.fx.builders.pane.HBoxBuilder;
import org.int4.fx.builders.pane.PaneBuilder;
import org.int4.fx.builders.pane.StackPaneBuilder;
import org.int4.fx.builders.pane.VBoxBuilder;

/**
 * Builder group for common JavaFX layout panes.
 * <p>
 * This is a mixin interface used to compose custom FX entry points. The
 * standard all-in-one factory is {@link CoreFX#FX}, and the panes-only
 * instance is {@link #PANES}. See {@link CoreFX} for shared builder
 * conventions and build-context behavior.
 */
public interface Panes {

  /**
   * The panes-only builder factory instance.
   */
  Panes PANES = new Panes() {};

  /**
   * Creates a builder for a {@link javafx.scene.layout.StackPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.StackPane
   */
  default StackPaneBuilder stack(String... styleClasses) {
    return new StackPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.HBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.HBox
   */
  default HBoxBuilder hbox(String... styleClasses) {
    return new HBoxBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.VBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.VBox
   */
  default VBoxBuilder vbox(String... styleClasses) {
    return new VBoxBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.FlowPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.FlowPane
   */
  default FlowPaneBuilder flow(String... styleClasses) {
    return new FlowPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.GridPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.GridPane
   */
  default GridPaneBuilder grid(String... styleClasses) {
    return new GridPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.BorderPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.BorderPane
   */
  default BorderPaneBuilder border(String... styleClasses) {
    return new BorderPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.Pane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.layout.Pane
   */
  default PaneBuilder pane(String... styleClasses) {
    return new PaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.Group}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.Group
   */
  default GroupBuilder group(String... styleClasses) {
    return new GroupBuilder(styleClasses);
  }
}
