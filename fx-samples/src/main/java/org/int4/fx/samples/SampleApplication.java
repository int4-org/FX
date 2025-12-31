package org.int4.fx.samples;

import java.time.LocalDate;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.int4.fx.builders.FX;
import org.int4.fx.builders.Panes;
import org.int4.fx.builders.Scenes;
import org.int4.fx.builders.StyleSheets;
import org.int4.fx.values.Observe;

/**
 * Sample application demonstrating the use of the {@code org.int4.fx.builders}
 * fluent builder API together with observable values and models.
 * <p>
 * This application presents a small booking-style form that showcases:
 * <ul>
 *   <li>Declarative UI construction using pane and control builders</li>
 *   <li>Implicit builder completion (no explicit {@code build()} calls)</li>
 *   <li>Binding JavaFX properties and observable values to controls</li>
 *   <li>Conditional visibility and enablement driven by observables</li>
 *   <li>Inline validation feedback via dynamic style classes</li>
 *   <li>Integration of {@code Observe} utilities for derived state</li>
 * </ul>
 * <p>
 * The example intentionally keeps all logic, layout, and styling in a single
 * class to provide a compact, self-contained reference that can be used both
 * as documentation and as a starting point for experimentation.
 */
public class SampleApplication extends Application {

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
     * Set-up properties for a booking form:
     */

    BooleanProperty smsAlerts = new SimpleBooleanProperty();
    StringProperty name = new SimpleStringProperty();
    StringProperty phoneNumber = new SimpleStringProperty();
    StringProperty luggageWeight = new SimpleStringProperty();
    StringProperty tripType = new SimpleStringProperty("Return");
    ObjectProperty<LocalDate> departureDate = new SimpleObjectProperty<>();
    ObjectProperty<LocalDate> returnDate = new SimpleObjectProperty<>();

    /*
     * Create a boolean which is true when it is a return trip that is being booked:
     */

    ObservableValue<Boolean> returnTrip = tripType.map("Return"::equals);

    /*
     * Validation helper to determine when the dates are valid:
     */

    ObservableValue<Boolean> datesValid = Observe.values(returnTrip, departureDate, returnDate.orElse(LocalDate.MIN))
      .map((rt, dd, rd) -> !rt || dd.compareTo(rd) < 0);

    /*
     * Validation helper to determine when the luggage weight is valid:
     */

    ObservableValue<Boolean> luggageWeightValid = luggageWeight.map(v -> v.matches("(([1-9][0-9]?)|100| *)"));

    /*
     * When SMS alerts are wanted, set the phone number valid observable to
     * check for a valid phone number:
     */

    ObservableValue<Boolean> phoneNumberValid = Observe.booleans(
      smsAlerts,
      phoneNumber.map(v -> !v.matches("[0-9]{5,}"))
    ).anyFalse();

    /*
     * Create the user interface:
     */

    Scene scene = Scenes.create(Panes.vbox("root").nodes(
      Panes.grid("form")
        .row(
          "Trip Type",
          FX.<String>comboBox().value(tripType).items(List.of("One way", "Return"))
        )
        .row(
          "Departure Date",
          FX.datePicker().promptText("Departure Date").value(departureDate)
        )
        .row(
          returnTrip,  // only show this row when true
          FX.label().target("return-date").text("_Return Date"),
          FX.datePicker().id("return-date").promptText("Return Date")
            .styleProvider(datesValid.map(v -> v ? null : "invalid"))
            .value(returnDate)
        )
        .row(
          "Name",
          FX.textField().promptText("Name")
            .styleProvider(name.map(v -> !v.isBlank() ? null : "invalid"))
            .value(name)
        )
        .row(
          "Luggage weight",
          FX.textField().promptText("Weight (in kg)")
            .styleProvider(luggageWeightValid.map(v -> v ? null : "invalid"))
            .value(luggageWeight)
        )
        .row(
          "SMS alerts",
          Panes.hbox().nodes(
            FX.checkBox().value(smsAlerts),
            FX.textField().enable(smsAlerts).promptText("Phone number")
              .styleProvider(phoneNumberValid.map(v -> v ? null : "invalid"))
              .value(phoneNumber)
          )
        ),

      FX.button().text("Book")
        .onAction(e -> new Alert(AlertType.CONFIRMATION, "Success!").showAndWait())
        .enable(
          Observe.booleans(
            name.map(v -> !v.isBlank()),
            datesValid,
            phoneNumberValid,
            luggageWeightValid
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

      .invalid {
        -fx-border-color: red;
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
