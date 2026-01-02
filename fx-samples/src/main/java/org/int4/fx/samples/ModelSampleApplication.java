package org.int4.fx.samples;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.int4.fx.builders.FX;
import org.int4.fx.builders.Panes;
import org.int4.fx.builders.Scenes;
import org.int4.fx.builders.StyleSheets;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.BooleanModel;
import org.int4.fx.values.model.ChoiceModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.ObjectModel;
import org.int4.fx.values.model.StringModel;
import org.int4.fx.values.util.Observe;

/**
 * Demonstration application showcasing the integration of the {@code fx-builders}
 * UI DSL with the {@code fx-values} model and domain system.
 * <p>
 * The example implements a small booking form whose user interface is constructed
 * entirely using fluent builders. Application state and validation rules are
 * expressed through {@link org.int4.fx.values.model.ValueModel ValueModel}
 * instances rather than raw JavaFX properties.
 * <p>
 * Key aspects demonstrated by this sample include:
 * <ul>
 *   <li>Declarative UI construction using {@code FX}, {@code Panes}, and {@code Scenes}</li>
 *   <li>Domain-driven validation (ranges, regular expressions, applicability)</li>
 *   <li>Dynamic domain updates based on other model values</li>
 *   <li>Automatic enablement and validation feedback for controls</li>
 *   <li>Clear separation of UI layout from validation and business rules</li>
 * </ul>
 * <p>
 * The sample is intended as a reference and starting point for applications that
 * prefer a model-centric approach over direct manipulation of JavaFX properties.
 */
public class ModelSampleApplication extends Application {

  /**
   * Main method.
   *
   * @param args arguments from the commandline, if any
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    /*
     * Set-up models. These differ from regular FX properties in that they know
     * when they are valid. They can also provide additional information to set
     * up controls:
     */

    BooleanModel smsAlerts = BooleanModel.of();
    StringModel name = StringModel.regex(".{5,}");
    StringModel phoneNumber = StringModel.of();
    IntegerModel luggageWeight = IntegerModel.nullableRange(0, 100);
    ChoiceModel<String> tripType = ChoiceModel.of("Return", "One Way");
    ObjectModel<LocalDate> departureDate = ObjectModel.of(Domain.of(dd -> dd.getDayOfWeek().getValue() < 6));
    ObjectModel<LocalDate> returnDate = ObjectModel.ofNullable();

    /*
     * Create a boolean which is true when it is a return trip that is being booked:
     */

    ObservableValue<Boolean> returnTrip = tripType.map("Return"::equals);

    /*
     * The possible domain of the return date changes depending on the trip type and
     * then chosen departure date. Let's update it when any of those change:
     */

    Observe.values(returnTrip, departureDate).compute((isReturnTrip, dd) ->
      !isReturnTrip ? Domain.<LocalDate>empty()  // Return Date is not applicable
        : dd == null ? Domain.<LocalDate>any()  // Return Date can be anything as no departure date was chosen
        : Domain.bounded(dd.plusDays(1), LocalDate.MAX)  // Return Date must be after departure date
    ).subscribe(returnDate.domainProperty()::setValue);

    /*
     * When SMS alerts are wanted, set the phone number domain to require
     * a valid phone number:
     */

    smsAlerts.map(v -> v ? Domain.regex("[0-9]{5,}") : Domain.<String>any())
      .subscribe(phoneNumber.domainProperty()::setValue);

    /*
     * The phone number must be provided and valid only when SMS alerts are requested.
     * Let's set up a conditional for this here:
     */

    ObservableValue<Boolean> phoneNumberValid = Observe.booleans(
      smsAlerts,
      phoneNumber.invalid()
    ).anyFalse();

    /*
     * Create the user interface:
     */

    Scene scene = Scenes.create(Panes.vbox("root").nodes(
      Panes.grid("form")
        .row(
          "Trip Type",
          FX.<String>comboBox().model(tripType)
        )
        .row(
          "Departure Date",
          FX.datePicker().promptText("Departure Date").model(departureDate)
        )
        .row(
          FX.label().target("return-date").text("_Return Date").visible(returnDate.applicable()),
          FX.datePicker().id("return-date").promptText("Return Date").model(returnDate)
        )
        .row(
          "Name",
          FX.textField().promptText("Name").model(name)
        )
        .row(
          "Luggage weight",
          FX.textField().promptText("Weight (in kg)").model(luggageWeight)
        )
        .row(
          "SMS alerts",
          Panes.hbox().nodes(
            FX.checkBox().model(smsAlerts),
            FX.textField().enable(smsAlerts).promptText("Phone number").model(phoneNumber)
          )
        ),

      FX.button().text("Book")
        .onAction(e -> new Alert(AlertType.CONFIRMATION, "Success!").showAndWait())
        .enable(
          Observe.booleans(
            name.valid(),
            returnDate.valid(),
            departureDate.valid(),
            phoneNumberValid,
            luggageWeight.valid()
          )
          .allTrue()
        )
    ));

    /*
     * Auto-size the scene when trip type changes:
     */

    tripType.subscribe(() -> primaryStage.sizeToScene());

    /*
     * Add an inline style sheet to make this example self-contained:
     */

    scene.getStylesheets().add(StyleSheets.inline(
      """
      .root {
        -fx-padding: 1em;
      }

      HBox {
        -fx-alignment: center-left;
        -fx-spacing: 0.5em;
      }

      *:touched:invalid {
        -fx-border-color: red;
        -fx-border-style: solid;
        -fx-border-width: 0.1em;
        -fx-border-insets: -0.1em;
      }

      .form {
        -fx-hgap: 1em;
        -fx-vgap: 0.1em;
      }
      """
    ));

    /*
     * Show the sample application:
     */

    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }
}
