package org.int4.fx.builders.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ListChangeListener;
import javafx.util.Subscription;

import org.int4.fx.values.domain.DomainSource;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.Edit;
import org.int4.fx.values.domain.Edit.IndexedChange;
import org.int4.fx.values.domain.IndexedEditor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainListBridgeTest {

  @Test
  void shouldMirrorInitialEditorItems() {
    DomainListBridge<String> bridge = DomainListBridge.link(editorOf("A", "B"));

    assertThat(bridge).containsExactly("A", "B");
  }

  @Test
  void shouldRejectNullArguments() {
    assertThatThrownBy(() -> DomainListBridge.link((DomainSource<String>)null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainListBridge.of((Domain<String>)null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldApplyIndexedEditsIncrementally() {
    IndexedEditor<String> editor = editorOf("A", "B");
    DomainListBridge<String> bridge = DomainListBridge.link(editor);
    List<String> events = recordChanges(bridge);

    editor.add("C");

    assertThat(bridge).containsExactly("A", "B", "C");
    assertThat(events).containsExactly("add 2:1");

    editor.remove(0);

    assertThat(bridge).containsExactly("B", "C");
    assertThat(events).containsExactly("add 2:1", "remove 0:1");

    editor.clear();

    assertThat(bridge).isEmpty();
    assertThat(events).containsExactly("add 2:1", "remove 0:1", "remove 0:2");
  }

  @Test
  void shouldNotifyOnceForBatch() {
    IndexedEditor<String> editor = editorOf("A", "B");
    DomainListBridge<String> bridge = DomainListBridge.link(editor);
    List<List<String>> snapshots = new ArrayList<>();

    bridge.addListener((ListChangeListener<String>)change -> {
      while(change.next()) {
        // drain the change so the snapshot below reflects the final state
      }

      snapshots.add(new ArrayList<>(bridge));
    });

    editor.batch(() -> {
      editor.add("C");
      editor.add("D");
      editor.remove(0);
    });

    assertThat(snapshots).containsExactly(List.of("B", "C", "D"));
  }

  @Test
  void shouldReplayMultipleChangesExactly() {
    IndexedEditor<String> editor = editorOf("A", "B");
    DomainListBridge<String> bridge = DomainListBridge.link(editor);
    List<String> events = recordChanges(bridge);

    editor.batch(() -> {
      editor.add("C");
      editor.add("D");
      editor.remove(0);
    });

    assertThat(events).containsExactly("remove 0:1", "add 1:2");
  }

  @Test
  void shouldRebuildOnEditWithoutIncrementalInformation() {
    FakeEditor editor = new FakeEditor("A", "B");
    DomainListBridge<String> bridge = DomainListBridge.link(editor);
    List<List<String>> snapshots = new ArrayList<>();

    bridge.addListener((ListChangeListener<String>)change -> {
      while(change.next()) {
        // drain the change so the snapshot below reflects the final state
      }

      snapshots.add(new ArrayList<>(bridge));
    });

    editor.replace("X", "Y", "Z");

    assertThat(bridge).containsExactly("X", "Y", "Z");
    assertThat(snapshots).containsExactly(List.of("X", "Y", "Z"));
  }

  @Test
  void shouldApplySetChanges() {
    FakeEditor editor = new FakeEditor("A", "B");
    DomainListBridge<String> bridge = DomainListBridge.link(editor);
    List<String> events = recordChanges(bridge);

    editor.set(0, "Z");

    assertThat(bridge).containsExactly("Z", "B");
    assertThat(events).containsExactly("set 0");
  }

  @Test
  void shouldBeReadOnly() {
    DomainListBridge<String> bridge = DomainListBridge.link(editorOf("A", "B"));

    assertThatThrownBy(() -> bridge.add("C")).isInstanceOf(UnsupportedOperationException.class);
    assertThatThrownBy(() -> bridge.set(0, "C")).isInstanceOf(UnsupportedOperationException.class);
    assertThatThrownBy(() -> bridge.remove("A")).isInstanceOf(UnsupportedOperationException.class);
    assertThatThrownBy(bridge::clear).isInstanceOf(UnsupportedOperationException.class);
    assertThat(bridge).containsExactly("A", "B");
  }

  @Test
  void shouldStopFollowingAfterClose() {
    IndexedEditor<String> editor = editorOf("A", "B");
    DomainListBridge<String> bridge = DomainListBridge.link(editor);

    bridge.close();
    bridge.close();  // should be idempotent

    editor.add("C");

    assertThat(bridge).containsExactly("A", "B");
  }

  private static IndexedEditor<String> editorOf(String... items) {
    return Domain.from(List.of(items)).requireEditor(IndexedEditor.class);
  }

  private static List<String> recordChanges(DomainListBridge<String> bridge) {
    List<String> events = new ArrayList<>();

    bridge.addListener((ListChangeListener<String>)change -> {
      while(change.next()) {
        if(change.wasReplaced()) {
          events.add("set " + change.getFrom());
        }
        else if(change.wasAdded()) {
          events.add("add " + change.getFrom() + ":" + change.getAddedSize());
        }
        else if(change.wasRemoved()) {
          events.add("remove " + change.getFrom() + ":" + change.getRemovedSize());
        }
      }
    });

    return events;
  }

  /**
   * An editor that can produce edits the {@code ListDomain} editor does not
   * support yet.
   */
  private static final class FakeEditor implements IndexedEditor<String> {
    private final List<String> items = new ArrayList<>();
    private final List<Consumer<DomainTransition<String>>> subscribers = new ArrayList<>();

    private Domain<String> current;

    FakeEditor(String... items) {
      this.items.addAll(List.of(items));
      current = Domain.from(this.items);
    }

    void replace(String... values) {
      List<String> next = List.of(values);

      items.clear();
      items.addAll(next);

      publish(new Edit.Replaced<>());
    }

    void set(long index, String value) {
      String previous = items.set((int)index, value);

      publish(new Edit.Indexed<>(List.of(new IndexedChange.Set<>(index, previous, value))));
    }

    @Override
    public Domain<String> get() {
      return Domain.from(items);
    }

    @Override
    public long size() {
      return items.size();
    }

    @Override
    public String get(long index) {
      return items.get((int)index);
    }

    @Override
    public void add(long index, String element) {
      items.add((int)index, element);
    }

    @Override
    public String remove(long index) {
      return items.remove((int)index);
    }

    @Override
    public void clear() {
      items.clear();
    }

    @Override
    public Subscription transitions(Consumer<DomainTransition<String>> consumer) {
      subscribers.add(consumer);

      return () -> subscribers.remove(consumer);
    }

    private void publish(Edit<String> edit) {
      Domain<String> successor = Domain.from(items);

      DomainTransition<String> transition = new DomainTransition<>(current, successor, edit);

      current = successor;

      for(Consumer<DomainTransition<String>> subscriber : List.copyOf(subscribers)) {
        subscriber.accept(transition);
      }
    }
  }
}
