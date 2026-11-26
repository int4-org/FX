package org.int4.fx.builders.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;

import org.int4.fx.builders.common.SceneBuilder;
import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSources;
import org.int4.fx.values.domain.ListEditor;
import org.int4.fx.values.model.ChoiceModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComboBoxBuilderTest extends FXTest {

  @Test
  void shouldBuildWithFixedItems() {
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .items(List.of("a", "b"))
      .build();

    assertThat(control.getItems()).containsExactly("a", "b");
  }

  @Test
  void shouldSortFixedItemsWhenComparatorIsSet() {
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .comparator(Comparator.<String>reverseOrder())
      .items(List.of("a", "c", "b"))
      .build();

    assertThat(control.getItems()).containsExactly("c", "b", "a");
  }

  @Test
  void shouldSortObservableItemsWhenComparatorIsSet() {
    ObservableList<String> items = FXCollections.observableArrayList("b", "a");
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .items(items)
      .comparator(Comparator.<String>naturalOrder())
      .build();

    assertThat(control.getItems()).containsExactly("a", "b");
    assertThat(items).containsExactly("b", "a");

    items.add("c");

    assertThat(control.getItems()).containsExactly("a", "b", "c");
  }

  @Test
  void shouldPopulateItemsFromModel() {
    ChoiceModel<String> model = ChoiceModel.of("a", "b");
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .model(model)
      .build();

    assertThat(control.getItems()).containsExactly("a", "b");
    assertThatThrownBy(() -> control.getItems().add("c")).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldUpdateItemsWhenModelDomainChanges() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("a", "b"));
    ChoiceModel<String> model = ChoiceModel.of(source);
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(control);

    source.set(Domain.of("a", "c"));

    assertThat(control.getItems()).containsExactly("a", "c");
  }

  @Test
  void shouldApplyIncrementalEditsFromEditorBackedModel() {
    ListEditor<String> editor = Domain.of("a", "b").editor();
    ChoiceModel<String> model = ChoiceModel.of(editor);
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(control);

    List<String> events = new ArrayList<>();

    control.getItems().addListener((ListChangeListener<String>)change -> {
      while(change.next()) {
        if(change.wasReplaced()) {
          events.add("replace " + change.getFrom());
        }
        else if(change.wasAdded()) {
          events.add("add " + change.getFrom());
        }
        else if(change.wasRemoved()) {
          events.add("remove " + change.getFrom());
        }
      }
    });

    editor.add("c");

    assertThat(control.getItems()).containsExactly("a", "b", "c");
    assertThat(events).containsExactly("add 2");
  }

  @Test
  void shouldKeepSelectionWhenItemsChange() {
    ListEditor<String> editor = Domain.of("a", "b", "c").editor();
    ChoiceModel<String> model = ChoiceModel.of(editor);
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(control);

    control.getSelectionModel().select("b");

    editor.remove(0);  // removes "a", before the selected item

    assertThat(control.getItems()).containsExactly("b", "c");
    assertThat(control.getValue()).isEqualTo("b");
    assertThat(control.getSelectionModel().getSelectedIndex()).isZero();
  }

  @Test
  void shouldSortModelItemsWhenComparatorIsSet() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("b", "a"));
    ChoiceModel<String> model = ChoiceModel.of(source);
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .comparator(Comparator.<String>naturalOrder())
      .model(model)
      .build();

    assertThat(control.getItems()).containsExactly("a", "b");

    @SuppressWarnings("unused")
    Scene scene = new Scene(control);

    source.set(Domain.of("d", "c", "a"));

    assertThat(control.getItems()).containsExactly("a", "c", "d");
  }

  @Test
  void shouldResyncItemsAfterReconnect() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("a", "b"));
    ChoiceModel<String> model = ChoiceModel.of(source);
    ComboBox<String> control = new ComboBoxBuilder.Raw()
      .model(model)
      .build();

    new SceneBuilder(control).build();  // show state managing scene

    Window window = Mockito.mock(Window.class);

    Broadcasts.broadcast(control, WindowEvent.showing(window));

    assertThat(control.getItems()).containsExactly("a", "b");

    Broadcasts.broadcast(control, WindowEvent.hidden(window));

    source.set(Domain.of("x", "y", "z"));

    // Nothing is synchronized while hidden:
    assertThat(control.getItems()).containsExactly("a", "b");

    Broadcasts.broadcast(control, WindowEvent.showing(window));

    assertThat(control.getItems()).containsExactly("x", "y", "z");
  }
}
