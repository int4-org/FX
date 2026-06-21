package org.int4.fx.controls.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.FontConverter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * Represents a validation marker that can be further customized. It exposes
 * a tooltip property and the current validation issue associated with the marker.
 * <p>
 * The marker also has a CSS {@code -fx-font} property; this allows children of
 * the marker to use relative {@code em} units in CSS, which will resolve against
 * the font set on this marker.
 */
public class Marker extends StackPane {
  private final ObjectProperty<Font> font = new StyleableObjectProperty<>(Font.getDefault()) {
    @Override
    public CssMetaData<Marker, Font> getCssMetaData() {
      return FONT;
    }

    @Override
    public Object getBean() {
      return Marker.this;
    }

    @Override
    public String getName() {
      return "font";
    }
  };

  private final ObjectProperty<Tooltip> tooltip = new ObjectPropertyBase<>() {
    private Tooltip old;

    @Override
    protected void invalidated() {
      Tooltip t = get();
      // install / uninstall
      if(t != old) {
        if(old != null) {
          Tooltip.uninstall(Marker.this, old);
        }

        if(t != null) {
          Tooltip.install(Marker.this, t);
        }

        old = t;
      }
    }

    @Override
    public Object getBean() {
      return Marker.this;
    }

    @Override
    public String getName() {
      return "tooltip";
    }
  };

  private final ReadOnlyObjectWrapper<ValidationIssue<?>> validationIssue = new ReadOnlyObjectWrapper<>(this, "validation-issue");

  Marker(ValidationIssue<?> validationIssue) {
    Objects.requireNonNull(validationIssue, "validationIssue");

    this.validationIssue.set(validationIssue);
  }

  /**
   * Sets the font for this pane.
   *
   * @param value the {@link Font} to set, can be {@code null}
   */
  public final void setFont(Font value) {
    fontProperty().set(value);
  }

  /**
   * Returns the font for this pane.
   *
   * @return the {@link Font}, can be {@code null}
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

  /**
   * Sets the tooltip for this pane.
   *
   * @param value the tooltip to set, can be {@code null}
   */
  public final void setTooltip(Tooltip value) {
    tooltipProperty().setValue(value);
  }

  /**
   * Returns the validation issue for this pane.
   *
   * @return the {@link ValidationIssue}, never {@code null}
   */
  public final ValidationIssue<?> getValidationIssue() {
    return validationIssue.get();
  }

  /**
   * Returns the validation issue property for this pane.
   *
   * @return the validation issue property, never {@code null}
   */
  public final ReadOnlyObjectProperty<ValidationIssue<?>> validationIssueProperty() {
    return validationIssue.getReadOnlyProperty();
  }

  /**
   * Sets the validation issue for this pane.
   *
   * @param value the validation issue to set, can be {@code null}
   */
  final void setValidationIssue(ValidationIssue<?> value) {
    validationIssue.setValue(Objects.requireNonNull(value, "value"));
  }

  /**
   * Returns the tooltip for this pane.
   *
   * @return the {@link Tooltip}, can be {@code null}
   */
  public final Tooltip getTooltip() {
    return tooltip.get();
  }

  /**
   * Returns the tooltip property for this pane.
   *
   * @return the tooltip property, never {@code null}
   */
  public final ObjectProperty<Tooltip> tooltipProperty() {
    return tooltip;
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return getClassCssMetaData();
  }

  private static final CssMetaData<Marker, Font> FONT = new CssMetaData<>("-fx-font", FontConverter.getInstance(), Font.getDefault()) {
    @Override
    public boolean isSettable(Marker node) {
      return node.font == null || !node.font.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<Font> getStyleableProperty(Marker node) {
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
