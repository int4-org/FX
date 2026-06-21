package org.int4.fx.controls.validation;

import java.util.List;
import java.util.function.Consumer;

import javafx.css.CssMetaData;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

import org.int4.fx.builders.Panes;
import org.int4.fx.controls.marker.AbstractMarkerPane;
import org.int4.fx.core.event.ValidationEvent;
import org.int4.fx.core.util.StyleSheets;

/**
 * A specialized marker overlay pane that displays validation markers on nodes.
 * <p>
 * This pane listens for {@link ValidationEvent#VALIDATION_CHANGED} events
 * and automatically adds or removes markers on target nodes depending on their
 * validation state.
 * <p>
 * Each marker consists of a small red circle with a white "X" drawn on top,
 * automatically scaled according to the node's font size.
 *
 * <h2>CSS Customization</h2>
 * The appearance of the markers is defined in a user agent stylesheet and can be
 * customized or overridden using standard CSS. The marker has a layered structure:
 * <pre>
 * .validation-marker-pane
 *   └── .overlay
 *       └── .marker (scaled via -fx-font)
 *           ├── .background
 *           └── .foreground
 * </pre>
 *
 * <h3>Key Style Classes</h3>
 * <ul>
 *   <li>{@code .validation-marker-pane}: The root of the overlay pane.</li>
 *   <li>{@code .marker}: The layer responsible for scaling and positioning relative
 *     to the target node's font. Uses {@code em} units for its size and its children's sizes.</li>
 *   <li>{@code .background}: The base shape of the marker (defaults to a red circle).</li>
 *   <li>{@code .foreground}: The icon shape on top of the base (defaults to a white cross).</li>
 * </ul>

 * <h3>Example: Customizing Marker Color</h3>
 * <pre>{@code
 * .validation-marker-pane .background {
 *   -fx-background-color: orange;
 * }
 * }</pre>
 */
public class ValidationMarkerPane extends AbstractMarkerPane {

  /**
   * Creates a builder for a {@link ValidationMarkerPane}, initialising
   * it with the given style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public static ValidationMarkerPaneBuilder of(String... styleClasses) {
    return new ValidationMarkerPaneBuilder(styleClasses);
  }

  private Consumer<Marker> markerCreatedHandler;

  /**
   * Creates a new validation marker overlay pane.
   */
  public ValidationMarkerPane() {
    getStyleClass().add("validation-marker-pane");

    addEventFilter(ValidationEvent.VALIDATION_CHANGED, e -> {
      if(e.getTarget() instanceof Node target) {
        if(e.isValid()) {
          removeMarker(target);
        }
        else {
          Marker marker = (Marker)getMarkerNode(target);

          if(marker == null) {
            installNewMarker(target, toValidationIssue(e));
          }
          else {
            marker.setValidationIssue(toValidationIssue(e));
          }
        }
      }
    });
  }

  /**
   * Set a callback that is called when a new marker is created. This
   * allows customizing the marker further. For example, a tooltip
   * could be installed on it.
   *
   * @param markerCreatedHandler a consumer that may customize the marker, can be {@code null}
   */
  public void setOnMarkerCreated(Consumer<Marker> markerCreatedHandler) {
    this.markerCreatedHandler = markerCreatedHandler;
  }

  private void installNewMarker(Node target, ValidationIssue<?> validationIssue) {
    Marker marker = new Marker(validationIssue);

    marker.setSnapToPixel(false);
    marker.setPickOnBounds(false);
    marker.setFont(resolveFont(target));
    marker.getStyleClass().add("marker");

    marker.getChildren().addAll(
      Panes.pane("background").ignoreBounds().build(),
      Panes.pane("foreground").ignoreBounds().build()
    );

    putMarker(target, marker);

    if(markerCreatedHandler != null) {
      markerCreatedHandler.accept(marker);
    }
  }

  private static ValidationIssue<?> toValidationIssue(ValidationEvent event) {
    return event.invalidValueTypeIncompatible()
      ? new ValidationIssue.Incompatible<>(event.template())
      : new ValidationIssue.Invalid<>(event.invalidValue(), event.template());
  }

  @Override
  public String getUserAgentStylesheet() {
    return StyleSheets.inline(
      """
      .validation-marker-pane {
        -fx-padding: 0;
        -fx-margin: 0;
      }

      // the container for all markers:
      .validation-marker-pane .overlay {}

      // individual markers:
      .validation-marker-pane .marker {
        -fx-opacity: 0.8;

        // Move marker (from top right position) slightly more into the control area:
        -fx-translate-x: -0.75em;
        -fx-translate-y: -0.25em;
      }

      // marker foreground graphic:
      .validation-marker-pane .marker .foreground {
        -fx-min-width: 0.7em;
        -fx-min-height: 0.7em;
        -fx-max-width: 0.7em;
        -fx-max-height: 0.7em;
        -fx-background-color: white;

        // a thick cross:
        -fx-shape: "
          M -0.35,-0.5
          L -0.5,-0.35
          L -0.15,0
          L -0.5,0.35
          L -0.35,0.5
          L 0,0.15
          L 0.35,0.5
          L 0.5,0.35
          L 0.15,0
          L 0.5,-0.35
          L 0.35,-0.5
          L 0,-0.15
          Z
        ";
      }

      // marker background graphic:
      .validation-marker-pane .marker .background {
        -fx-min-width: 1em;
        -fx-min-height: 1em;
        -fx-background-color: red;

        // a circle:
        -fx-shape: "
          M 0,0
          m -0.5,0
          a 0.5,0.5 0 1,0 1,0
          a 0.5,0.5 0 1,0 -1,0
        ";

        -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 1.0), 0.5em, 0.2, 0, 0);
      }
      """
    );
  }

  @Override
  protected Point2D computeMarkerPosition(Node node) {
    Bounds b = node.getLayoutBounds();

    return new Point2D(b.getMaxX(), b.getMinY());
  }

  private static Font resolveFont(Node node) {
    Node current = node;

    while(current != null) {
      if (current instanceof Labeled labeled) {
        return labeled.getFont();
      }

      if (current instanceof TextInputControl textInput) {
        return textInput.getFont();
      }

      @SuppressWarnings("unchecked")  // bit dirty, assume all CssMetaData properties are fonts :)
      List<CssMetaData<Node, Font>> cssMetaDataList = (List<CssMetaData<Node, Font>>)(List<?>)current.getCssMetaData();

      for(CssMetaData<Node, Font> md : cssMetaDataList) {
        if(md.getProperty().equals("-fx-font")) {  // this is a real font property
          return md.getStyleableProperty(current).getValue();
        }
      }

      current = current.getParent();
    }

    return Font.getDefault();
  }
}
