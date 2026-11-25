package org.int4.fx.samples;

import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import org.int4.fx.builders.Scenes;
import org.int4.fx.core.util.StyleSheets;
import org.int4.fx.core.util.Trigger;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource.DomainTransition;
import org.int4.fx.values.domain.IndexedEditor;
import org.int4.fx.values.model.ChoiceModel;
import org.int4.fx.values.model.StringModel;

import static org.int4.fx.builders.CoreFX.FX;

/**
 * Sample application for a list-backed domain and its indexed editor.
 * <p>
 * A {@link ListView} and a {@link ComboBox} are both model driven: they show the
 * items of a {@link ChoiceModel} backed by the editor's domain source. Edits
 * performed through the {@link IndexedEditor} are reflected in both controls.
 */
public class ListDomainSampleApplication extends Application {

  /**
   * Main method.
   *
   * @param args arguments from the commandline, if any
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

  /**
   * Constructs a new instance.
   */
  public ListDomainSampleApplication() {
  }

  @Override
  public void start(Stage primaryStage) {
    IndexedEditor<String> editor = Domain.from(List.of("Alice", "Bob", "Charlie", "Diana"))
      .requireEditor(IndexedEditor.class);
    ChoiceModel<String> model = ChoiceModel.of(editor);
    StringModel newEmployee = StringModel.nullable();
    StringModel status = StringModel.of("No transitions yet");
    Trigger<ActionEvent> removeTrigger = Trigger.of();

    editor.transitions(t -> status.set(describe(t)));

    Scene scene = Scenes.create(FX.vbox("root").nodes(
      FX.label().text("ListDomain editor").style("title"),
      FX.hbox("controls").nodes(
        FX.textField().promptText("Employee name").model(newEmployee),
        FX.button().text("Add").onAction(e -> {
          String name = newEmployee.getValue();

          if(name != null && !name.trim().isEmpty()) {
            editor.add(name.trim());
            newEmployee.set(null);
          }
        }),
        FX.button().text("Remove selected").onAction(removeTrigger::fire),
        FX.button().text("Clear").onAction(e -> editor.clear()),
        FX.button().text("Batch add").onAction(e -> editor.batch(() -> {
          editor.add("Batch A");
          editor.add("Batch B");
          editor.add("Batch C");
        }))
      ),
      FX.hbox("lists").nodes(
        FX.listView().model(model).apply(employees -> removeTrigger.onFire(() -> {
          int index = employees.getSelectionModel().getSelectedIndex();

          if(index >= 0) {
            editor.remove(index);
          }
        })),
        FX.comboBox().model(model).promptText("Select an employee")
      ),
      FX.label().text(status).wrapText()
    ));

    scene.getStylesheets().add(StyleSheets.inline(
      """
      .root {
        -fx-padding: 1em;
        -fx-spacing: 0.75em;
      }

      .title {
        -fx-font-size: 1.3em;
        -fx-font-weight: bold;
      }

      .controls {
        -fx-spacing: 0.5em;
        -fx-alignment: center-left;
      }

      .lists {
        -fx-spacing: 1em;
      }

      ListView, ComboBox {
        -fx-pref-width: 16em;
      }
      """
    ));

    primaryStage.setTitle("ListDomain Sample");
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }

  private static String describe(DomainTransition<?> transition) {
    return "Edit: " + transition.edit();
  }
}
