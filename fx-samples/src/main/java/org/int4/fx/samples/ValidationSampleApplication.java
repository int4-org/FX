package org.int4.fx.samples;

import java.text.MessageFormat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.int4.fx.builders.FX;
import org.int4.fx.builders.Panes;
import org.int4.fx.builders.Scenes;
import org.int4.fx.controls.validation.ValidationIssue;
import org.int4.fx.controls.validation.ValidationMarkerPane;
import org.int4.fx.core.util.Observe;
import org.int4.fx.core.util.StyleSheets;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.StringModel;

/**
 * A sample application demonstrating model-driven validation with visual markers.
 * <p>
 * This application uses {@link ValidationMarkerPane} as its root container.
 * When a control's associated model becomes invalid, a marker is automatically
 * displayed on the control with a tooltip explaining the error.
 */
public class ValidationSampleApplication extends Application {

  /**
   * Main entry point.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    StringModel name = StringModel.regex("[A-Z][a-z]+");
    IntegerModel age = IntegerModel.of(null, Domain.bounded(18, 120));

    /*
     * Create a ValidationMarkerPane with a message resolver.
     * The resolver translates internal ValidationIssue objects into
     * human-readable strings for tooltips.
     */

    ValidationMarkerPane root = new ValidationMarkerPane(issue -> switch(issue) {
      case ValidationIssue.Invalid(Object _, Template template) -> toMessage(template);
      case ValidationIssue.Incompatible(Template template) -> toMessage(template);
    });

    /*
     * Set the content of the marker pane. Markers will be overlaid
     * on the controls within this content.
     */

    root.setContent(Panes.vbox("form").nodes(
      Panes.grid("grid")
        .row("Name", FX.textField().promptText("Name (e.g. John)").model(name))
        .row("Age", FX.textField().promptText("Age (18-120)").model(age)),
      FX.button().text("Submit")
        .enable(Observe.booleans(name.valid(), age.valid()).allTrue())
    ).build());

    Scene scene = Scenes.create(root);

    /*
     * Add some basic styling.
     */

    scene.getStylesheets().add(StyleSheets.inline(
      """
      .form {
        -fx-padding: 2em;
        -fx-spacing: 1.5em;
        -fx-alignment: top-center;
      }
      .grid {
        -fx-hgap: 1em;
        -fx-vgap: 1em;
      }
      """
    ));

    primaryStage.setScene(scene);
    primaryStage.setTitle("Validation Marker Sample");
    primaryStage.sizeToScene();
    primaryStage.show();
  }

  static String toMessage(Template template) {
    return MessageFormat.format(
      switch(template.key()) {
        case "domain.missing" -> "Must not be empty";
        case "domain.invalid" -> "Must be a valid value";
        case "domain.notContained" -> "Must be one of {0}";
        case "domain.noMatch" -> "Must match regular expression {0}";
        case "domain.outOfRange" -> "Must be between {0} and {1}";
        case "domain.misaligned" -> "Must be a multiple of {1} starting from {0}";
        case "conversion.incompatible" -> "Must be a compatible value";
        default -> "Invalid (" + template.key() + ")";
      },
      template.args().values().toArray()
    );
  }
}
