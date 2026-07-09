package org.int4.fx.controls;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.int4.fx.core.util.StyleSheets;

/**
 * A pane that wraps arbitrary content and provides resize handles along the bottom and right edges,
 * together with a larger visual grip in the bottom-right corner.
 * <p>
 * The mouse cursor changes to indicate the active resize direction when it is positioned over a
 * resize handle. When the grip is made visible, the pane displays a visual indicator showing that
 * it can be resized by dragging the bottom-right corner. If no content is set, the pane and its
 * resize handles are effectively hidden.
 * <p>
 * The appearance of the grip and the size of the resize handles are defined by the control's user
 * agent stylesheet. These styles can be overridden to customize the size, color, or other visual
 * properties.
 * <p>
 * Example usage:
 * <pre>{@code
 * TextArea textArea = new TextArea();
 * ResizablePane pane = new ResizablePane(textArea);
 * }</pre>
 */
public class ResizablePane extends Region {
  private static final PseudoClass SHOW_GRIP_PSEUDO_CLASS = PseudoClass.getPseudoClass("show-grip");
  private static final String STYLESHEET = StyleSheets.inline(
    """
    .resizable-pane > .thumb {
      -fx-pref-width: 0.6em;
      -fx-pref-height: 0.6em;
      -fx-max-width: 0.6em;
      -fx-max-height: 0.6em;
      -fx-cursor: se-resize;
    }

    .resizable-pane:show-grip > .thumb-graphics {
      -fx-pref-width: 1.5em;
      -fx-pref-height: 1.5em;
      -fx-max-width: 1.5em;
      -fx-max-height: 1.5em;
      -fx-background-color:
        linear-gradient(to bottom right, transparent 50%, -fx-box-border 50%, -fx-box-border 60%, transparent 60%, transparent 70%, -fx-box-border 70%, -fx-box-border 80%, transparent 80%);
      -fx-background-insets: 0.2em;
    }

    .resizable-pane > .bottom-edge {
      -fx-pref-height: 0.15em;
      -fx-max-height: 0.15em;
      -fx-cursor: s-resize;
    }

    .resizable-pane > .right-edge {
      -fx-pref-width: 0.15em;
      -fx-max-width: 0.15em;
      -fx-cursor: e-resize;
    }
    """
  );

  /**
   * Creates a builder for a {@link ResizablePane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static ResizablePaneBuilder of(String... styleClasses) {
    return new ResizablePaneBuilder(styleClasses);
  }

  private final Region thumb = new Region();
  private final Region thumbGraphic = new Region();
  private final Region bottomEdge = new Region();
  private final Region rightEdge = new Region();

  private Node content;
  private double resizeAnchorX;
  private double resizeAnchorY;

  /**
   * Constructs a new instance.
   */
  public ResizablePane() {
    this(null);
  }

  /**
   * Constructs a new instance using the given node as content.
   *
   * @param content the content, can be {@code null}
   */
  public ResizablePane(Node content) {
    getStyleClass().add("resizable-pane");

    thumbGraphic.getStyleClass().add("thumb-graphics");
    thumbGraphic.setMouseTransparent(true);

    thumb.getStyleClass().add("thumb");

    thumb.setOnMousePressed(e -> {
      resizeAnchorX = getWidth() - e.getSceneX();
      resizeAnchorY = getHeight() - e.getSceneY();
    });

    thumb.setOnMouseDragged(e -> setPrefSize(Math.max(minWidth(-1), resizeAnchorX + e.getSceneX()), Math.max(minHeight(-1), resizeAnchorY + e.getSceneY())));

    bottomEdge.getStyleClass().add("bottom-edge");

    bottomEdge.setOnMousePressed(e -> {
      resizeAnchorX = getWidth() - e.getSceneX();
      resizeAnchorY = getHeight() - e.getSceneY();
    });

    bottomEdge.setOnMouseDragged(e -> setPrefSize(getWidth(), Math.max(minHeight(-1), resizeAnchorY + e.getSceneY())));

    rightEdge.getStyleClass().add("right-edge");

    rightEdge.setOnMousePressed(e -> {
      resizeAnchorX = getWidth() - e.getSceneX();
      resizeAnchorY = getHeight() - e.getSceneY();
    });

    rightEdge.setOnMouseDragged(e -> setPrefSize(Math.max(minWidth(-1), resizeAnchorX + e.getSceneX()), getHeight()));

    setContent(content);
  }

  /**
   * Sets the main content of this resizable pane.
   *
   * @param content the content node, can be {@code null}
   */
  public final void setContent(Node content) {
    this.content = content;

    if(content == null) {
      getChildren().clear();
    }
    else {
      getChildren().setAll(content, thumbGraphic, bottomEdge, rightEdge, thumb);
    }
  }

  /**
   * Toggles the visibility of the visual grip stripes in the corner thumb.
   *
   * @param visible true to show the stripes, false to hide them
   */
  public final void setGripVisible(boolean visible) {
    pseudoClassStateChanged(SHOW_GRIP_PSEUDO_CLASS, visible);
  }

  @Override
  public String getUserAgentStylesheet() {
    return STYLESHEET;
  }

  @Override
  protected double computeMinWidth(double height) {
    return snapSizeX((content != null ? content.minWidth(height) : 0) + getInsets().getLeft() + getInsets().getRight());
  }

  @Override
  protected double computeMinHeight(double width) {
    return snapSizeY((content != null ? content.minHeight(width) : 0) + getInsets().getTop() + getInsets().getBottom());
  }

  @Override
  protected double computePrefWidth(double height) {
    return snapSizeX((content != null ? content.prefWidth(height) : 0) + getInsets().getLeft() + getInsets().getRight());
  }

  @Override
  protected double computePrefHeight(double width) {
    return snapSizeY((content != null ? content.prefHeight(width) : 0) + getInsets().getTop() + getInsets().getBottom());
  }

  @Override
  protected void layoutChildren() {
    if(content != null) {
      content.resizeRelocate(0, 0, getWidth(), getHeight());

      double thumbWidth = thumb.prefWidth(-1);
      double thumbHeight = thumb.prefHeight(-1);
      double thumbGraphicsWidth = thumbGraphic.prefWidth(-1);
      double thumbGraphicsHeight = thumbGraphic.prefHeight(-1);
      double rightEdgeWidth = rightEdge.prefWidth(-1);
      double bottomEdgeWidth = bottomEdge.prefHeight(-1);

      thumb.resizeRelocate(getWidth() - thumbWidth, getHeight() - thumbHeight, thumbWidth, thumbHeight);
      thumbGraphic.resizeRelocate(getWidth() - thumbGraphicsWidth, getHeight() - thumbGraphicsHeight, thumbGraphicsWidth, thumbGraphicsHeight);
      bottomEdge.resizeRelocate(0, getHeight() - bottomEdgeWidth, getWidth() - thumbWidth, bottomEdgeWidth);
      rightEdge.resizeRelocate(getWidth() - rightEdgeWidth, 0, rightEdgeWidth, getHeight() - thumbHeight);
    }
  }
}
