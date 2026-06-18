package org.int4.fx.controls.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.FontConverter;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * A {@link StackPane} that supports a CSS {@code -fx-font} property.
 * <p>
 * This allows children of this pane to use relative {@code em} units in CSS,
 * which will resolve against the font set on this pane.
 */
class ScaledStackPane extends StackPane {
  private final ObjectProperty<Font> font = new StyleableObjectProperty<>(Font.getDefault()) {
    @Override
    public CssMetaData<ScaledStackPane, Font> getCssMetaData() {
      return FONT;
    }

    @Override
    public Object getBean() {
      return ScaledStackPane.this;
    }

    @Override
    public String getName() {
      return "font";
    }
  };

  /**
   * Sets the font for this pane.
   *
   * @param value the {@link Font} to set, cannot be {@code null}
   */
  public final void setFont(Font value) {
    fontProperty().set(value);
  }

  /**
   * Returns the font for this pane.
   *
   * @return the {@link Font}, never {@code null}
   */
  public final Font getFont() {
    return font.get();
  }

  /**
   * Returns the font property for this pane.
   *
   * @return the font property, never {@code null}
   */
  public final ObjectProperty<Font> fontProperty() {
    return font;
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return getClassCssMetaData();
  }

  private static final CssMetaData<ScaledStackPane, Font> FONT = new CssMetaData<>("-fx-font", FontConverter.getInstance(), Font.getDefault()) {
    @Override
    public boolean isSettable(ScaledStackPane node) {
      return node.font == null || !node.font.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<Font> getStyleableProperty(ScaledStackPane node) {
      return (StyleableProperty<Font>)node.fontProperty();
    }
  };

  private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

  static {
    List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(StackPane.getClassCssMetaData());

    styleables.add(FONT);

    STYLEABLES = Collections.unmodifiableList(styleables);
  }

  /**
   * Returns the list of CSS metadata associated with this class.
   *
   * @return a list of CSS metadata, never {@code null}
   */
  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return STYLEABLES;
  }
}
