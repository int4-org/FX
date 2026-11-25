package org.int4.fx.builders;

import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.int4.fx.builders.control.ButtonBuilder;
import org.int4.fx.builders.control.CheckBoxBuilder;
import org.int4.fx.builders.control.ColorPickerBuilder;
import org.int4.fx.builders.control.ComboBoxBuilder;
import org.int4.fx.builders.control.DatePickerBuilder;
import org.int4.fx.builders.control.LabelBuilder;
import org.int4.fx.builders.control.ListViewBuilder;
import org.int4.fx.builders.control.MenuButtonBuilder;
import org.int4.fx.builders.control.ScrollPaneBuilder;
import org.int4.fx.builders.control.SliderBuilder;
import org.int4.fx.builders.control.SpinnerBuilder;
import org.int4.fx.builders.control.SplitPaneBuilder;
import org.int4.fx.builders.control.TabPaneBuilder;
import org.int4.fx.builders.control.TextAreaBuilder;
import org.int4.fx.builders.control.TextFieldBuilder;
import org.int4.fx.builders.control.TitledPaneBuilder;
import org.int4.fx.builders.control.ToggleButtonBuilder;
import org.int4.fx.builders.custom.CustomNodeBuilder;
import org.int4.fx.builders.custom.CustomRegionBuilder;

/**
 * Builder group for standard JavaFX controls.
 * <p>
 * This is a mixin interface used to compose custom FX entry points. The
 * standard all-in-one factory is {@link CoreFX#FX}, and the controls-only
 * instance is {@link #CONTROLS}. See {@link CoreFX} for shared builder
 * conventions and build-context behavior.
 */
public interface Controls {

  /**
   * The controls-only builder factory instance.
   */
  Controls CONTROLS = new Controls() {};

  /**
   * Creates a builder for a {@link javafx.scene.control.Label}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.Label
   */
  default LabelBuilder label(String... styleClasses) {
    return new LabelBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.Button}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.Button
   */
  default ButtonBuilder button(String... styleClasses) {
    return new ButtonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TextField}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.TextField
   */
  default TextFieldBuilder textField(String... styleClasses) {
    return new TextFieldBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TextArea}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.TextArea
   */
  default TextAreaBuilder textArea(String... styleClasses) {
    return new TextAreaBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ComboBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.ComboBox
   */
  default ComboBoxBuilder.Raw comboBox(String... styleClasses) {
    return new ComboBoxBuilder.Raw(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ListView}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.ListView
   */
  default ListViewBuilder.Raw listView(String... styleClasses) {
    return new ListViewBuilder.Raw(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.CheckBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.CheckBox
   */
  default CheckBoxBuilder checkBox(String... styleClasses) {
    return new CheckBoxBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.Slider}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.Slider
   */
  default SliderBuilder slider(String... styleClasses) {
    return new SliderBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.Spinner}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.Spinner
   */
  default SpinnerBuilder.Raw spinner(String... styleClasses) {
    return new SpinnerBuilder.Raw(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ToggleButton}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.ToggleButton
   */
  default ToggleButtonBuilder toggleButton(String... styleClasses) {
    return new ToggleButtonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ColorPicker}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.ColorPicker
   */
  default ColorPickerBuilder colorPicker(String... styleClasses) {
    return new ColorPickerBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.DatePicker}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.DatePicker
   */
  default DatePickerBuilder datePicker(String... styleClasses) {
    return new DatePickerBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.MenuButton}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.MenuButton
   */
  default MenuButtonBuilder menuButton(String... styleClasses) {
    return new MenuButtonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.SplitPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.SplitPane
   */
  default SplitPaneBuilder splitPane(String... styleClasses) {
    return new SplitPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ScrollPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.ScrollPane
   */
  default ScrollPaneBuilder scrollPane(String... styleClasses) {
    return new ScrollPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TabPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.TabPane
   */
  default TabPaneBuilder tabPane(String... styleClasses) {
    return new TabPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TitledPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   * @see javafx.scene.control.TitledPane
   */
  default TitledPaneBuilder titledPane(String... styleClasses) {
    return new TitledPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a custom {@link javafx.scene.layout.Region}, initialising
   * it with the given style classes.
   *
   * @param <R> the type of the region
   * @param regionSupplier a supplier that creates the region, cannot be {@code null}
   * @param styleClasses optional style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  default <R extends Region> CustomRegionBuilder<R> customRegion(Supplier<R> regionSupplier, String... styleClasses) {
    return new CustomRegionBuilder<>(regionSupplier, styleClasses);
  }

  /**
   * Creates a builder for a custom {@link javafx.scene.Node}, initialising
   * it with the given style classes.
   *
   * @param <N> the type of the node
   * @param nodeSupplier a supplier that creates the node, cannot be {@code null}
   * @param styleClasses optional style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  default <N extends Node> CustomNodeBuilder<N> customNode(Supplier<N> nodeSupplier, String... styleClasses) {
    return new CustomNodeBuilder<>(nodeSupplier, styleClasses);
  }
}
