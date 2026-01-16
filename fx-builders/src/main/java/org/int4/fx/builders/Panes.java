package org.int4.fx.builders;

import org.int4.fx.builders.pane.BorderPaneBuilder;
import org.int4.fx.builders.pane.GridPaneBuilder;
import org.int4.fx.builders.pane.GroupBuilder;
import org.int4.fx.builders.pane.HBoxBuilder;
import org.int4.fx.builders.pane.StackPaneBuilder;
import org.int4.fx.builders.pane.VBoxBuilder;

/**
 * Entry point for creating builders for JavaFX common layout panes.
 */
public class Panes {

  /**
   * Creates a builder for a {@link javafx.scene.layout.StackPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.layout.StackPane
   */
  public static StackPaneBuilder stack(String... styleClasses) {
    return new StackPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.HBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.layout.HBox
   */
  public static HBoxBuilder hbox(String... styleClasses) {
    return new HBoxBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.VBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.layout.VBox
   */
  public static VBoxBuilder vbox(String... styleClasses) {
    return new VBoxBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.GridPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.layout.GridPane
   */
  public static GridPaneBuilder grid(String... styleClasses) {
    return new GridPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.layout.BorderPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.layout.BorderPane
   */
  public static BorderPaneBuilder border(String... styleClasses) {
    return new BorderPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.Group}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.Group
   */
  public static GroupBuilder group(String... styleClasses) {
    return new GroupBuilder(styleClasses);
  }

  private Panes() {}
}
