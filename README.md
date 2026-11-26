# FXFlow: Fluent UI Construction and Modeling for JavaFX

[![Maven Central Version](https://img.shields.io/maven-central/v/org.int4.fx/parent)](https://mvnrepository.com/artifact/org.int4.fx) [![Build Status](https://github.com/int4-org/FX/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/int4-org/FX/actions) [![Coverage](https://codecov.io/gh/int4-org/FX/branch/master/graph/badge.svg?token=QCNNRFYF98)](https://codecov.io/gh/int4-org/FX) [![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT) [![javadoc](https://javadoc.io/badge2/org.int4.fx/parent/javadoc.svg)](https://javadoc.io/doc/org.int4.fx/parent)

This library provides a set of lightweight, composable utilities aimed at making JavaFX applications more declarative, expressive, and maintainable. The focus is on *fluent UI construction* and *reactive value modeling*, while staying close to core JavaFX concepts.

The project is intended for JavaFX developers who value clarity, type safety, and explicitness, and who want to reduce boilerplate without obscuring how JavaFX works.

## Goals

* Provide a fluent, readable API for constructing JavaFX scenes and nodes
* Improve modeling of application state and validation beyond raw JavaFX properties
* Enable reactive composition of values without introducing a full reactive framework
* Remain small, modular, and dependency-light
* Integrate naturally with existing JavaFX code

## Fluent UI Construction

The library offers builder-style utilities for constructing nodes, panes, scenes, and controls in a concise and readable way.

The standard builder factory is `CoreFX.FX`; import it statically:

```java
import static org.int4.fx.builders.CoreFX.FX;
```

The examples below assume this import. Applications can extend `CoreFX` with additional default builder methods and expose their own `FX` field; import that field instead to use the extended builder vocabulary. For narrower imports, use `Controls.CONTROLS`, `Panes.PANES`, or `Shapes.SHAPES` instead.

Example:

```java
Scene scene = Scenes.create(
    FX.vbox("form").nodes(
        "Name",
        FX.textField().text(nameProperty),
        FX.button().text("Submit").onAction(e -> submit())
    )
);
```

Key characteristics:

* Builders always produce standard JavaFX nodes.
* Style classes can be specified on builder construction.
* Properties, bindings, and event handlers remain fully accessible.
* Layout structure is readable top-to-bottom, reflecting the UI hierarchy.
* Decorators (`decorate(...)`) wrap a builder in another node such as a `ScrollPane` or `ResizablePane`, retaining the wrapper builder's fluent API and following the wrapped content's visibility.
* Fluent construction allows integration of custom nodes, and/or manipulation of properties not exposed directly.
* Builders that support content or children accept any object convertible to nodes, such as:
  - Standard JavaFX nodes
  - Other builders producing a `Node` (`build` is called automatically to reduce clutter)
  - Strings (automatically converted to `Label`)

The same example with standard FX:

```java
TextField nameField = new TextField();
Label nameLabel = new Label("Name");
Button submitButton = new Button("Submit");
VBox root = new VBox(nameLabel, nameField, submitButton);

nameField.textProperty().bindBidirectional(nameProperty);
submitButton.setOnAction(e -> submit());
root.getStyleClass().add("form");

Scene scene = new Scene(root);
```

### Decorators

Decorators wrap an existing node in another node. Each `decorate` call returns the wrapper's builder, so wrapper-specific methods remain available and the node can be wrapped again:

```java
import org.int4.fx.builders.control.ScrollPaneDecorator;
import org.int4.fx.controls.ResizablePaneDecorator;

ResizablePane form = FX.vbox("form")
    .nodes(
        FX.label().text("Name"),
        FX.textField().promptText("Name")
    )
    .decorate(ScrollPaneDecorator.INSTANCE)
    .fitToWidth()
    .decorate(ResizablePaneDecorator.INSTANCE)
    .build();
```

Here the `VBox` is wrapped in a scroll pane, which is then wrapped in a resizable pane. The final `build()` returns the outermost node. Decorator builders derive their visibility and managed state from the wrapped content, so the wrapper disappears when its content does. They do not expose content or visibility configuration; style and wrapper-specific options are set on the returned builder.

## Flex Layout

The `fx-layout` module provides `Flex`, a row/column container whose orientation can be changed at runtime. It supports `Priority.ALWAYS` growth, spacing, and alignment.

Its aligned mode makes nested rows share a common grid, so a form's inputs line up even when its labels have different lengths:

```java
Flex form = Flex.vflex().aligned()
    .with(
        Flex.hflex().with(
            FX.label().text("Name"),
            FX.textField()
        ),
        Flex.hflex().with(
            FX.label().text("Email address"),
            FX.textField()
        )
    );
```

Both text fields start at the same offset, in a column sized by the widest label. And unlike `HBox` and `VBox`, a `Flex` can switch between horizontal and vertical at runtime.

See [fx-layout/README.md](fx-layout/README.md) for the full Flex guide and examples.

## Value Models and Domains

Beyond raw JavaFX properties, the library contains *models* that represent user-editable state together with validation and applicability.

Examples include:

* `StringModel`, `IntegerModel`, `BooleanModel`
* Choice and multi-choice models
* Object models with optional domains

Models:

* Expose observable values compatible with JavaFX controls
* Track validity and applicability explicitly
* Can dynamically change their allowed domain, either by replacing it or by following a `DomainSource`

This enables UI logic such as:

* Enabling/disabling controls based on validity
* Showing or hiding controls when a value is applicable
* Centralizing validation rules outside the UI

Example:

```java
// Define a model with a valid range
IntegerModel ageModel = IntegerModel.range(0, 120);

// Create a spinner bound to the model
Spinner<Integer> ageSpinner = FX.spinner().model(ageModel).build();
```

Key characteristics:

* Declarative model: The IntegerModel defines valid values upfront.
* Automatic validation: The spinner will only allow values within the specified range.
* Two-way binding: Changes in the spinner update the model, and vice versa.
* Concise and readable: Minimal code to connect data and UI.

Domains are immutable values that define which values a model accepts. A
`DomainSource` is a source that provides the current domain and publishes each
successor along with the edit that produced it. Following one lets a model track
evolving state -- such as an editable list -- without being rebuilt on every
change, which is what makes large, frequently edited lists practical.

### List-backed domains and editors

A `ListDomain` -- returned by `Domain.of(...)` or `Domain.from(List)` -- provides
an `editor()` that returns a `ListEditor`. The editor is a `DomainSource`, so each
edit produces the next immutable domain and publishes it as a transition that
models can follow. Controls bound to a model update incrementally: items are not
copied, and nothing is rebuilt on every change.

```java
ListEditor<String> employees = Domain.of("Alice", "Bob", "Charlie").editor();

// A model that follows the editor's domain:
ChoiceModel<String> selection = ChoiceModel.of(employees);

Scene scene = Scenes.create(FX.vbox().nodes(
    FX.listView().model(selection),      // items and single selection
    FX.comboBox().model(selection)       // same model, same items
));
```

Edits made through the editor -- `employees.add("Diana")`, `employees.remove(0)` --
are reflected in both controls. The list is copy-on-write, so each new domain
shares its items with the previous one, and a control's item list refers to the
domain's own list rather than a copy. A model's domain can also be replaced at
runtime: `setDomain(...)` binds it to a fixed domain, while `setSource(...)` binds
it to any `DomainSource`, such as an editor or a shared
`DomainSources.mutable(...)` handle. That makes it straightforward to show, hide
or swap a section of the UI as other state changes.

> This example can be found in the fx-samples module as ListDomainSampleApplication.java.

## The `Observe` class

Provides ways to observe multiple values in a more declarative way:

* Combine multiple observable values.
* Derive booleans such as `allTrue()` or `anyFalse()`.
* Map values with lambdas.

Example for `Observe.booleans(...)`:

```java
BooleanProperty subscribed = new SimpleBooleanProperty();
BooleanProperty acceptedTerms = new SimpleBooleanProperty();
BooleanProperty emailVerified = new SimpleBooleanProperty();

// Combine multiple booleans to determine if the "Register" button should be enabled
ObservableValue<Boolean> canRegister = Observe.booleans(
    subscribed,
    acceptedTerms,
    emailVerified
).allTrue();

// React automatically, enabling the button, when the combined state changes
FX.button().text("Register now!").enable(canRegister);
```

Key characteristics:

* Combines multiple boolean observable values into a single derived value.
* Supports common boolean operations (`allTrue()`, `anyFalse()`).
* Declarative and concise: avoids manual listener management.
* Updates automatically whenever any of the source values change.
* Safe for chaining derived observables: no weak listener pitfalls like `Bindings.createBooleanBinding`.
  - For example, `Observe.booleans(nameValid.map(v -> !v), ageValid)` works reliably without keeping explicit references.
* Lazy evaluation: listeners are only registered when the derived value is observed.
* Returns a standard `ObservableValue`, so it integrates seamlessly with existing JavaFX APIs.

Equivalent JavaFX approach using `Bindings`:

```java
// JavaFX version combining multiple booleans (weak listener pitfalls if using mapped observables)
Bindings.createBooleanBinding(
    () -> nameValid.get() && ageValid.get() && emailValid.get(),
    nameValid, ageValid, emailValid
);
```

Example for `Observe.values(...)`:

```java
StringProperty firstName = new SimpleStringProperty("Alice");
StringProperty lastName = new SimpleStringProperty("Smith");

// Combine values using Observe.values and map; map is skipped if any value is null
ObservableValue<String> fullName = Observe.values(firstName, lastName)
    .map((fn, ln) -> fn + " " + ln)
    .orElse("Unknown");

// React automatically when either first or last name changes
fullName.subscribe(name -> System.out.println("Full name: " + name));
```

Key characteristics:

* Combines multiple observable values of any type into a single derived value.
* `map` transforms the combined values, but is skipped if any input is `null`.
* The result of `map` is a standard `ObservableValue`; `orElse` is used to provide a default when map is skipped.
* `compute` is a `map` alternative that always invokes the lambda, even if some inputs are `null`.
* Changes in any source observable automatically propagate.

## The `Trigger` class

`Trigger` is a lightweight utility for decoupling event sources from actions. You can fire a `Trigger` from one node and react to it from one or more others. This allows you to declaratively wire controls together without assigning them to local variables.

Example:

```java
// Create a trigger accepting an ActionEvent:
Trigger<ActionEvent> spinUpTrigger = Trigger.of();

// Bind a Button to the trigger:
FX.button().text("Spin Multiple!").onAction(spinUpTrigger::fire);

// When the trigger fires, do something to a Spinner:
FX.spinner().apply(sp -> spinUpTrigger.onFire(sp::increment));
```

Here:
- Clicking the button fires the `Trigger`.
- The spinner subscribes to the `Trigger` and increments when fired.
- You could have multiple subscribers, all reacting to the same trigger.
- The trigger payload can be anything, but can also be ignored if irrelevant.

This is especially useful for declarative UIs where multiple nodes share a common action, or when you want to avoid creating temporary variables just to wire events.

## The `NodeEventHandler` interface

`NodeEventHandler` is like a standard `EventHandler`, but it passes the control (`Node`) itself as a parameter. This is very handy in declarative UI construction, because you often don't have a variable for the node yet.

Example:

```java
FX.button().text("Click me")
    .onAction((btn, event) -> {
        // btn is the Button itself
        new Alert(AlertType.INFORMATION, "You clicked " + btn.getText()).showAndWait();
        btn.setDisable(true); // directly manipulate the node
    });
```

You can use it with any event type, just like a normal `EventHandler`, but the extra node reference allows fluent code without local variables.

## Inline Style Sheets

The `StyleSheets` class provides a simple way to embed CSS directly in your application
without using external files. The `inline` method converts a CSS string into a data URL that JavaFX can consume.

Example:

```java
Scene scene = new Scene(root);

scene.getStylesheets().add(StyleSheets.inline(
    """
    .root {
        -fx-padding: 1em;
    }
    
    Button {
        -fx-background-color: lightblue;
    }
    """
));
```

Key characteristics:

* Supports embedding CSS directly in code for self-contained custom controls, examples or small apps.
* Uses UTF-8 base64 encoding so JavaFX can load the stylesheet as a URL.
* Can be added anywhere a standard stylesheet URL is accepted (e.g. `Scene#getStylesheets()`).

This is particularly useful for small, control-specific stylesheets provided via 
`getUserAgentStylesheet()`, where shipping a separate resource file would be cumbersome.

## Combined Example: Models, Domains, and Fluent UI

The following example demonstrates how to combine value models with builders and 
observable utilities to construct a fully reactive JavaFX UI. It uses the FXFlow 
builders for fluent layout and control creation, `Observe` for combining observable 
values, and Models to enforce domains and validation.

> This example can be found in the fx-samples module as ModelSampleApplication.java.

```java
/*
 * Set-up models. These differ from regular FX properties in that they know
 * when they are valid. They can also provide additional information to set
 * up controls:
 */

BooleanModel smsAlerts = BooleanModel.of();
StringModel name = StringModel.regex(".{5,}");
StringModel phoneNumber = StringModel.nullable();
IntegerModel luggageWeight = IntegerModel.nullableRange(0, 100);
ChoiceModel<String> tripType = ChoiceModel.of("Return", "One Way");
ObjectModel<LocalDate> departureDate = ObjectModel.of(Domain.where(dd -> dd.getDayOfWeek().getValue() < 6));
ObjectModel<LocalDate> returnDate = ObjectModel.nullable();

/*
 * Create a boolean which is true when it is a return trip that is being booked:
 */

ObservableValue<Boolean> returnTrip = tripType.map("Return"::equals);

/*
 * The possible domain of the return date changes depending on the trip type and
 * then chosen departure date. Let's update it when any of those change:
 */

Observe.values(returnTrip, departureDate).compute((isReturnTrip, dd) ->
    !isReturnTrip ? Domain.<LocalDate>inapplicable()  // Return Date is not applicable
        : dd == null ? Domain.<LocalDate>any()  // Return Date can be anything as no departure date was chosen
        : Domain.bounded(dd.plusDays(1), LocalDate.MAX)  // Return Date must be after departure date
).subscribe(returnDate::setDomain);

/*
 * When SMS alerts are wanted, set the phone number domain to require
 * a valid phone number:
 */

smsAlerts.map(v -> v ? Domain.regex("[0-9]{5,}") : Domain.<String>any())
    .subscribe(phoneNumber::setDomain);

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

Scene scene = Scenes.create(FX.vbox("root").nodes(
    FX.grid("form")
        .row(
            "Trip Type",
            FX.comboBox().model(tripType)
        )
        .row(
            "Departure Date",
            FX.datePicker().promptText("Departure Date").model(departureDate)
        )
        .row(
            FX.label().text("_Return Date").visible(returnDate.applicable()),
            FX.datePicker().promptText("Return Date").model(returnDate)
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
            FX.hbox().nodes(
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
```

Key characteristics:
* Models (`BooleanModel`, `StringModel`, etc.) encapsulate valid value domains and validation rules.
* `Observe` allows combining multiple observables with `values()`, `booleans()`, and `compute()` to derive reactive values.
* Fluent builders (`FX`, or the grouped `CONTROLS`/`PANES`/`SHAPES` instances) allow concise creation of controls and layouts.
* Reactive UI: all dependent controls and button enablement update automatically when model values change.
* Supports conditional visibility, dynamic domains, and cross-field validation.

This snippet demonstrates the power of FXFlow for building reactive, validated forms with minimal boilerplate.

## Status

The project is under active development and experimentation. APIs may change between releases, especially in newer or more exploratory areas. Feedback and discussion are welcome.
