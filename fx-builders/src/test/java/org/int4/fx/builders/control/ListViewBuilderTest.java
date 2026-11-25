package org.int4.fx.builders.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Window;

import org.int4.fx.builders.common.SceneBuilder;
import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSources;
import org.int4.fx.values.domain.IndexedEditor;
import org.int4.fx.values.model.ChoiceModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListViewBuilderTest extends FXTest {

  @Test
  void shouldBuildWithFixedItems() {
    ListView<String> listView = new ListViewBuilder.Raw()
      .items(List.of("a", "b"))
      .build();

    assertThat(listView.getItems()).containsExactly("a", "b");
  }

  @Test
  void shouldUseObservableItemsDirectly() {
    ObservableList<String> items = FXCollections.observableArrayList("a");
    ListView<String> listView = new ListViewBuilder.Raw()
      .items(items)
      .build();

    assertThat(listView.getItems()).isSameAs(items);

    items.add("b");

    assertThat(listView.getItems()).containsExactly("a", "b");
  }

  @Test
  void shouldSortFixedItemsWhenComparatorIsSet() {
    ListView<String> listView = new ListViewBuilder.Raw()
      .comparator(Comparator.<String>reverseOrder())
      .items(List.of("a", "c", "b"))
      .build();

    assertThat(listView.getItems()).containsExactly("c", "b", "a");
  }

  @Test
  void shouldSortObservableItemsWhenComparatorIsSet() {
    ObservableList<String> items = FXCollections.observableArrayList("b", "a");
    ListView<String> listView = new ListViewBuilder.Raw()
      .items(items)
      .comparator(Comparator.<String>naturalOrder())
      .build();

    assertThat(listView.getItems()).containsExactly("a", "b");
    assertThat(items).containsExactly("b", "a");

    items.add("c");

    assertThat(listView.getItems()).containsExactly("a", "b", "c");
  }

  @Test
  void shouldApplyCellFactory() {
    ListView<String> listView = new ListViewBuilder.Raw()
      .items(List.of("a"))
      .cellFactory(() -> new ListCell<String>() {})
      .build();

    assertThat(listView.getCellFactory().call(listView)).isNotNull();
  }

  @Test
  void shouldApplyUntypedSettings() {
    Label placeholder = new Label("empty");
    ListView<String> listView = new ListViewBuilder.Raw()
      .orientation(Orientation.HORIZONTAL)
      .placeholder(placeholder)
      .fixedCellSize(20)
      .editable(true)
      .items(List.of("a"))
      .build();

    assertThat(listView.getOrientation()).isEqualTo(Orientation.HORIZONTAL);
    assertThat(listView.getPlaceholder()).isSameAs(placeholder);
    assertThat(listView.getFixedCellSize()).isEqualTo(20);
    assertThat(listView.isEditable()).isTrue();
  }

  @Test
  void shouldRejectNullItemsAndCellFactory() {
    assertThatThrownBy(() -> new ListViewBuilder.Raw().items((List<String>)null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new ListViewBuilder.Raw().items((ObservableList<String>)null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new ListViewBuilder.Raw().items(List.of("a")).cellFactory(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldPopulateItemsFromModel() {
    ChoiceModel<String> model = ChoiceModel.of("a", "b");
    ListView<String> listView = new ListViewBuilder.Raw()
      .model(model)
      .build();

    assertThat(listView.getItems()).containsExactly("a", "b");
    assertThatThrownBy(() -> listView.getItems().add("c")).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldUpdateItemsWhenModelDomainChanges() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("a", "b"));
    ChoiceModel<String> model = ChoiceModel.of(source);
    ListView<String> listView = new ListViewBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(listView);

    source.set(Domain.of("a", "c"));

    assertThat(listView.getItems()).containsExactly("a", "c");
  }

  @Test
  void shouldBindSelectionToModel() {
    ChoiceModel<String> model = ChoiceModel.of("a", "b");
    ListView<String> listView = new ListViewBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(listView);

    model.set("b");

    assertThat(listView.getSelectionModel().getSelectedItem()).isEqualTo("b");

    listView.getSelectionModel().select("a");

    assertThat(model.get()).isEqualTo("a");
  }

  @Test
  void shouldKeepSelectionWhenItemsChange() {
    IndexedEditor<String> editor = Domain.from(List.of("a", "b", "c")).requireEditor(IndexedEditor.class);
    ChoiceModel<String> model = ChoiceModel.of(editor);
    ListView<String> listView = new ListViewBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(listView);

    listView.getSelectionModel().select("b");

    editor.remove(0);  // removes "a", before the selected item

    assertThat(listView.getItems()).containsExactly("b", "c");
    assertThat(listView.getSelectionModel().getSelectedItem()).isEqualTo("b");
    assertThat(model.get()).isEqualTo("b");
  }

  @Test
  void shouldApplyIncrementalEditsFromEditorBackedModel() {
    IndexedEditor<String> editor = Domain.from(List.of("a", "b")).requireEditor(IndexedEditor.class);
    ChoiceModel<String> model = ChoiceModel.of(editor);
    ListView<String> listView = new ListViewBuilder.Raw()
      .model(model)
      .build();

    @SuppressWarnings("unused")
    Scene scene = new Scene(listView);

    List<String> events = new ArrayList<>();

    listView.getItems().addListener((ListChangeListener<String>)change -> {
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

    assertThat(listView.getItems()).containsExactly("a", "b", "c");
    assertThat(events).containsExactly("add 2");
  }

  @Test
  void shouldSortModelItemsWhenComparatorIsSet() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("b", "a"));
    ChoiceModel<String> model = ChoiceModel.of(source);
    ListView<String> listView = new ListViewBuilder.Raw()
      .comparator(Comparator.<String>naturalOrder())
      .model(model)
      .build();

    assertThat(listView.getItems()).containsExactly("a", "b");

    @SuppressWarnings("unused")
    Scene scene = new Scene(listView);

    source.set(Domain.of("d", "c", "a"));

    assertThat(listView.getItems()).containsExactly("a", "c", "d");
  }

  @Test
  void shouldResyncItemsAfterReconnect() {
    DomainSources.Mutable<String> source = DomainSources.mutable(Domain.of("a", "b"));
    ChoiceModel<String> model = ChoiceModel.of(source);
    ListView<String> listView = new ListViewBuilder.Raw()
      .model(model)
      .build();

    new SceneBuilder(listView).build();  // show state managing scene

    Window window = Mockito.mock(Window.class);

    Broadcasts.broadcast(listView, WindowEvent.showing(window));

    assertThat(listView.getItems()).containsExactly("a", "b");

    Broadcasts.broadcast(listView, WindowEvent.hidden(window));

    source.set(Domain.of("x", "y", "z"));

    // Nothing is synchronized while hidden:
    assertThat(listView.getItems()).containsExactly("a", "b");

    Broadcasts.broadcast(listView, WindowEvent.showing(window));

    assertThat(listView.getItems()).containsExactly("x", "y", "z");
  }
}
