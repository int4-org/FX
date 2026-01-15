package org.int4.fx.builders;

import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.int4.fx.builders.common.NodeBuilder;
import org.int4.fx.builders.control.ButtonBuilder;
import org.int4.fx.builders.control.CheckBoxBuilder;
import org.int4.fx.builders.control.ColorPickerBuilder;
import org.int4.fx.builders.control.ComboBoxBuilder;
import org.int4.fx.builders.control.DatePickerBuilder;
import org.int4.fx.builders.control.LabelBuilder;
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
 * Entry point for creating JavaFX control builders.
 * <p>
 * Builders produced by this class are typically used to declaratively construct
 * JavaFX scene graphs. When builders are passed as children to other builders,
 * there is no need to explicitly call {@link NodeBuilder#build()}:
 * builders are finalized automatically where required.
 * <p>
 * Child values are interpreted as follows:
 * <ul>
 *   <li>A builder is built and converted into a {@link javafx.scene.Node}.</li>
 *   <li>A {@link javafx.scene.Node} is accepted directly.</li>
 *   <li>A {@link String} is converted into a {@link javafx.scene.control.Label}.</li>
 *   <li>Any other object results in a placeholder node indicating an unsupported type.</li>
 * </ul>
 * <p>
 * Builder methods are named to match the JavaFX properties they manipulate.
 * For boolean properties, builders typically provide both positive and negative
 * convenience methods without parameters, in addition to the standard setter-style
 * method. Builders may also offer higher-level convenience methods that configure
 * multiple commonly used properties together.
 */
public class FX {

  /**
   * Creates a builder for a {@link javafx.scene.control.Label}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static LabelBuilder label(String... styleClasses) {
    return new LabelBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.Button}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static ButtonBuilder button(String... styleClasses) {
    return new ButtonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TextField}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static TextFieldBuilder textField(String... styleClasses) {
    return new TextFieldBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TextArea}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static TextAreaBuilder textArea(String... styleClasses) {
    return new TextAreaBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ComboBox}, initialising
   * it with the given style classes.
   *
   * @param <T> the type of values in the combo box
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static <T> ComboBoxBuilder<T> comboBox(String... styleClasses) {
    return new ComboBoxBuilder<>(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.CheckBox}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static CheckBoxBuilder checkBox(String... styleClasses) {
    return new CheckBoxBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.Slider}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static SliderBuilder slider(String... styleClasses) {
    return new SliderBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.Spinner}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static SpinnerBuilder spinner(String... styleClasses) {
    return new SpinnerBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ToggleButton}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static ToggleButtonBuilder toggleButton(String... styleClasses) {
    return new ToggleButtonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ColorPicker}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static ColorPickerBuilder colorPicker(String... styleClasses) {
    return new ColorPickerBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.DatePicker}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static DatePickerBuilder datePicker(String... styleClasses) {
    return new DatePickerBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.MenuButton}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   */
  public static MenuButtonBuilder menuButton(String... styleClasses) {
    return new MenuButtonBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.SplitPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.control.SplitPane
   */
  public static SplitPaneBuilder splitPane(String... styleClasses) {
    return new SplitPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.ScrollPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.control.ScrollPane
   */
  public static ScrollPaneBuilder scrollPane(String... styleClasses) {
    return new ScrollPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TabPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.control.TabPane
   */
  public static TabPaneBuilder tabPane(String... styleClasses) {
    return new TabPaneBuilder(styleClasses);
  }

  /**
   * Creates a builder for a {@link javafx.scene.control.TitledPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses optional style classes, cannot be {@code null} but may be empty
   * @return a fluent builder, never {@code null}
   * @see javafx.scene.control.TitledPane
   */
  public static TitledPaneBuilder titledPane(String... styleClasses) {
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
   * @throws NullPointerException if {@code regionSupplier} is {@code null}
   */
  public static <R extends Region> CustomRegionBuilder<R> customRegion(Supplier<R> regionSupplier, String... styleClasses) {
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
   * @throws NullPointerException if {@code nodeSupplier} is {@code null}
   */
  public static <N extends Node> CustomNodeBuilder<N> customNode(Supplier<N> nodeSupplier, String... styleClasses) {
    return new CustomNodeBuilder<>(nodeSupplier, styleClasses);
  }
}
