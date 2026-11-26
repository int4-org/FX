package org.int4.fx.values.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.util.Subscription;

import org.int4.fx.values.domain.Edit.IndexedChange;
import org.int4.fx.values.domain.DomainSource.DomainTransition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListDomainTest {

  @Test
  void fromShouldCopyTheInputList() {
    List<String> input = new ArrayList<>(List.of("a", "b"));
    ListDomain<String> domain = ListDomain.from(input);

    input.add("c");

    assertThat(domain.contains("a")).isTrue();
    assertThat(domain.contains("b")).isTrue();
    assertThat(domain.contains("c")).isFalse();

    IndexedView<String> view = domain.requireView(IndexedView.class);

    assertThat(view.asList()).containsExactly("a", "b");
  }

  @Test
  void fromShouldRejectNullInput() {
    assertThatThrownBy(() -> ListDomain.from(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofShouldAcceptVarargs() {
    ListDomain<String> domain = ListDomain.of("a", "b");

    assertThat(domain.contains("a")).isTrue();
    assertThat(domain.contains("c")).isFalse();
    assertThat(asList(domain.editor())).containsExactly("a", "b");
  }

  @Test
  void shouldEvaluateMembership() {
    ListDomain<String> domain = ListDomain.of("a", "b");

    assertThat(domain.contains("a")).isTrue();
    assertThat(domain.contains("c")).isFalse();
    assertThat(domain.contains(null)).isFalse();
    assertThat(domain.evaluate("a")).isEqualTo(Membership.MEMBER);
    assertThat(domain.evaluate("c")).isEqualTo(
      new Membership.Excluded(DomainTemplates.notContained(List.of("a", "b")))
    );
    assertThat(domain.evaluate(null)).isEqualTo(
      new Membership.Excluded(DomainTemplates.MISSING)
    );
  }

  @Test
  void nullableShouldAllowNullWithoutChangingItems() {
    ListDomain<String> domain = ListDomain.of("a", "b");
    Domain<String> nullable = domain.nullable();

    assertThat(nullable.contains(null)).isTrue();
    assertThat(nullable.evaluate(null)).isEqualTo(Membership.MEMBER);

    IndexedView<String> view = nullable.requireView(IndexedView.class);

    assertThat(view.asList()).containsExactly("a", "b");
    assertThat(domain.contains(null)).isFalse();
  }

  @Test
  void indexedViewShouldExposeItemsAndSupportDuplicatesAndNulls() {
    ListDomain<String> domain = ListDomain.of("a", null, "a", "b");
    IndexedView<String> view = domain.requireView(IndexedView.class);

    assertThat(view.size()).isEqualTo(4);
    assertThat(view.asList()).containsExactly("a", null, "a", "b");
    assertThat(view.get(0)).isEqualTo("a");
    assertThat(view.get(1)).isNull();
    assertThat(view.indexOf("a")).isZero();
    assertThat(view.indexOf("b")).isEqualTo(3);
    assertThat(view.indexOf("c")).isEqualTo(-1);
    assertThat(view.indexOf(null)).isEqualTo(1);
  }

  @Test
  void indexedViewShouldRejectOutOfBoundsAccess() {
    IndexedView<String> view = ListDomain.of("a", "b").requireView(IndexedView.class);

    assertThatThrownBy(() -> view.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> view.get(2)).isInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void indexedViewAsListShouldBeStableAndUnmodifiable() {
    IndexedView<String> view = ListDomain.of("a", "b").requireView(IndexedView.class);

    assertThat(view.asList()).isSameAs(view.asList());
    assertThatThrownBy(() -> view.asList().add("c"))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldExposeIndexedViewThroughViewAndRequireView() {
    ListDomain<String> domain = ListDomain.of("a", "b");

    assertThat(domain.view(IndexedView.class)).isInstanceOf(IndexedView.class);
    assertThat(domain.view(View.class)).isInstanceOf(IndexedView.class);

    IndexedView<String> view = domain.requireView(IndexedView.class);

    assertThat(view.asList()).containsExactly("a", "b");
  }

  @Test
  void shouldReturnNoopViewForUnsupportedViews() {
    ListDomain<String> domain = ListDomain.of("a", "b");

    assertThat(domain.view(ContinuousView.class)).isNotInstanceOf(IndexedView.class);
    assertThatThrownBy(() -> domain.requireView(ContinuousView.class))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldProvideAnListEditor() {
    ListDomain<String> domain = ListDomain.of("a", "b");

    ListEditor<String> editor = domain.editor();

    assertThat(editor.size()).isEqualTo(2);
    assertThat(editor.get(0)).isEqualTo("a");
    assertThat(editor.get(1)).isEqualTo("b");
  }

  @Test
  void shouldProvideEditorThroughDomainFactory() {
    ListEditor<String> editor = Domain.of("a", "b").editor();

    assertThat(editor.size()).isEqualTo(2);

    editor.add("c");

    assertThat(asList(editor)).containsExactly("a", "b", "c");
  }

  @Test
  void shouldRejectUnsupportedEditors() {
    ListDomain<String> domain = ListDomain.of("a", "b");

    assertThatThrownBy(() -> domain.requireEditor(Editor.class))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void editorShouldSupportAddAtIndexAndDefaults() {
    ListEditor<String> editor = ListDomain.of("a").editor();

    editor.add(0, "first");
    editor.add(2, "last");
    editor.add("appended");
    editor.addFirst("very-first");
    editor.addLast("very-last");

    assertThat(editor.size()).isEqualTo(6);
    assertThat(asList(editor)).containsExactly(
      "very-first", "first", "a", "last", "appended", "very-last"
    );
  }

  @Test
  void editorShouldSupportRemoveAtIndexAndDefaults() {
    ListEditor<String> editor = ListDomain.of("a", "b", "c", "d").editor();

    assertThat(editor.remove(1)).isEqualTo("b");
    assertThat(editor.removeFirst()).isEqualTo("a");
    assertThat(editor.removeLast()).isEqualTo("d");

    assertThat(asList(editor)).containsExactly("c");
  }

  @Test
  void editorShouldSupportNullElements() {
    ListDomain<String> domain = ListDomain.of("a", null, "b").nullable();
    ListEditor<String> editor = domain.editor();

    assertThat(editor.get(1)).isNull();

    editor.add(1, null);
    editor.add(null);

    assertThat(asList(editor)).containsExactly("a", null, null, "b", null);

    assertThat(editor.remove(1)).isNull();
    assertThat(asList(editor)).containsExactly("a", null, "b", null);
  }

  @Test
  void editorShouldClearAndRemainUsable() {
    ListEditor<String> editor = ListDomain.of("a", "b").editor();

    editor.clear();

    assertThat(editor.size()).isZero();

    editor.add("c");

    assertThat(asList(editor)).containsExactly("c");
  }

  @Test
  void editorShouldPublishTransitionsForEdits() {
    ListEditor<String> editor = ListDomain.of("a", "b").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    assertThat(transitions).isEmpty();

    editor.add("c");

    assertThat(transitions).hasSize(1);
    assertThat(items(transitions.get(0).previous())).containsExactly("a", "b");
    assertThat(items(transitions.get(0).successor())).containsExactly("a", "b", "c");
    assertThat(transitions.get(0).edit()).isEqualTo(new Edit.Indexed<>(List.of(
      new IndexedChange.Insert<>(2, List.of("c"))
    )));

    editor.remove(0);

    assertThat(transitions).hasSize(2);
    assertThat(items(transitions.get(1).previous())).containsExactly("a", "b", "c");
    assertThat(items(transitions.get(1).successor())).containsExactly("b", "c");
    assertThat(transitions.get(1).edit()).isEqualTo(new Edit.Indexed<>(List.of(
      new IndexedChange.Remove<>(0, List.of("a"))
    )));

    editor.clear();

    assertThat(transitions).hasSize(3);
    assertThat(items(transitions.get(2).previous())).containsExactly("b", "c");
    assertThat(items(transitions.get(2).successor())).isEmpty();
    assertThat(transitions.get(2).edit()).isEqualTo(new Edit.Indexed<>(List.of(
      new IndexedChange.Remove<>(0, List.of("b", "c"))
    )));
  }

  @Test
  void indexedDomainEditShouldContainMultipleChanges() {
    Edit.Indexed<String> edit = new Edit.Indexed<>(List.of(
      new IndexedChange.Insert<>(0, List.of("a")),
      new IndexedChange.Set<>(0, "a", "b"),
      new IndexedChange.Remove<>(0, List.of("b"))
    ));

    assertThat(edit.changes()).containsExactly(
      new IndexedChange.Insert<>(0, List.of("a")),
      new IndexedChange.Set<>(0, "a", "b"),
      new IndexedChange.Remove<>(0, List.of("b"))
    );
  }

  @Test
  void indexedInsertShouldPreserveNullItems() {
    Edit.Indexed<String> edit = new Edit.Indexed<>(List.of(
      new IndexedChange.Insert<>(0, Arrays.asList("a", null, "b"))
    ));

    assertThat(edit.changes()).containsExactly(
      new IndexedChange.Insert<>(0, Arrays.asList("a", null, "b"))
    );
  }

  @Test
  void batchShouldPublishOneTransitionWithCombinedChanges() {
    ListEditor<String> editor = ListDomain.of("a", "b", "c").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.batch(() -> {
      editor.add("d");
      editor.remove(0);
      editor.add(1, "x");
    });

    assertThat(transitions).hasSize(1);
    assertThat(items(transitions.get(0).previous())).containsExactly("a", "b", "c");
    assertThat(items(transitions.get(0).successor())).containsExactly("b", "x", "c", "d");
    assertThat(transitions.get(0).edit()).isEqualTo(new Edit.Indexed<>(List.of(
      new IndexedChange.Insert<>(3, List.of("d")),
      new IndexedChange.Remove<>(0, List.of("a")),
      new IndexedChange.Insert<>(1, List.of("x"))
    )));
    assertThat(asList(editor)).containsExactly("b", "x", "c", "d");
  }

  @Test
  void batchShouldExposeTheWorkingStateAndDelayTheTransition() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.batch(() -> {
      editor.add("b");

      assertThat(editor.size()).isEqualTo(2);
      assertThat(editor.get(1)).isEqualTo("b");
      assertThat(transitions).isEmpty();

      editor.remove(0);

      assertThat(editor.size()).isEqualTo(1);
      assertThat(editor.get(0)).isEqualTo("b");
      assertThat(transitions).isEmpty();
    });

    assertThat(transitions).hasSize(1);
    assertThat(asList(editor)).containsExactly("b");
  }

  @Test
  void batchShouldRollbackWhenTheActionThrows() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    assertThatThrownBy(() -> editor.batch(() -> {
      editor.add("b");
      throw new IllegalStateException("boom");
    })).isInstanceOf(IllegalStateException.class);

    assertThat(asList(editor)).containsExactly("a");
    assertThat(transitions).isEmpty();

    editor.add("c");

    assertThat(asList(editor)).containsExactly("a", "c");
    assertThat(transitions).hasSize(1);
  }

  @Test
  void nestedBatchShouldPublishOneTransition() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.batch(() -> {
      editor.add("b");
      editor.batch(() -> editor.add("c"));
      editor.add("d");
    });

    assertThat(transitions).hasSize(1);
    assertThat(items(transitions.get(0).successor())).containsExactly("a", "b", "c", "d");
    assertThat(transitions.get(0).edit()).isEqualTo(new Edit.Indexed<>(List.of(
      new IndexedChange.Insert<>(1, List.of("b")),
      new IndexedChange.Insert<>(2, List.of("c")),
      new IndexedChange.Insert<>(3, List.of("d"))
    )));
  }

  @Test
  void emptyBatchShouldNotPublishATransition() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.batch(() -> {});

    assertThat(transitions).isEmpty();
    assertThat(asList(editor)).containsExactly("a");
  }

  @Test
  void nullableBatchShouldPreserveNullability() {
    ListEditor<String> editor = ListDomain.of("a").nullable().editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.batch(() -> {
      editor.add(null);
      editor.add("b");
    });

    assertThat(transitions).hasSize(1);
    assertThat(transitions.get(0).successor().allowsNull()).isTrue();
    assertThat(items(transitions.get(0).successor())).containsExactly("a", null, "b");
  }

  @Test
  void clearingAnEmptyEditorShouldNotPublishATransition() {
    ListEditor<String> editor = ListDomain.<String>of().editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);
    editor.clear();

    assertThat(transitions).isEmpty();
  }

  @Test
  void editorTransitionsShouldChain() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.add("b");
    editor.add("c");

    assertThat(transitions).hasSize(2);
    assertThat(transitions.get(1).previous()).isSameAs(transitions.get(0).successor());
  }

  @Test
  void editorShouldRejectNullTransitionConsumer() {
    ListEditor<String> editor = ListDomain.of("a").editor();

    assertThatThrownBy(() -> editor.transitions(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void transitionSubscriberShouldObserveUpdatedEditorState() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<Long> observedSizes = new ArrayList<>();

    editor.transitions(transition -> observedSizes.add(editor.size()));

    editor.add("b");

    assertThat(observedSizes).containsExactly(2L);
  }

  @Test
  void editorTransitionsShouldSupportMultipleSubscribersAndUnsubscribe() {
    ListEditor<String> editor = ListDomain.of("a").editor();
    List<DomainTransition<String>> firstEvents = new ArrayList<>();
    List<DomainTransition<String>> secondEvents = new ArrayList<>();

    Subscription firstSubscription = editor.transitions(firstEvents::add);

    editor.transitions(secondEvents::add);

    editor.add("b");

    assertThat(firstEvents).hasSize(1);
    assertThat(secondEvents).hasSize(1);

    firstSubscription.unsubscribe();
    editor.add("c");

    assertThat(firstEvents).hasSize(1);
    assertThat(secondEvents).hasSize(2);
  }

  @Test
  void nullableEditorShouldPublishNullableSuccessors() {
    ListEditor<String> editor = ListDomain.of("a").nullable().editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);
    editor.add(null);

    assertThat(transitions).hasSize(1);
    assertThat(transitions.get(0).previous().allowsNull()).isTrue();
    assertThat(transitions.get(0).successor().allowsNull()).isTrue();
    assertThat(items(transitions.get(0).successor())).containsExactly("a", null);
  }

  @Test
  void editorShouldRejectOutOfBoundsOperations() {
    ListEditor<String> editor = ListDomain.of("a").editor();

    assertThatThrownBy(() -> editor.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> editor.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> editor.add(-1, "x")).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> editor.add(2, "x")).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> editor.remove(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    assertThatThrownBy(() -> editor.remove(1)).isInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void emittedSuccessorsShouldStayImmutableAndIndependent() {
    ListEditor<String> editor = ListDomain.of("a", "b").editor();
    List<DomainTransition<String>> transitions = new ArrayList<>();

    editor.transitions(transitions::add);

    editor.add("c");

    Domain<String> first = transitions.get(0).successor();
    IndexedView<String> firstView = first.requireView(IndexedView.class);
    List<String> firstList = firstView.asList();

    editor.add("d");
    editor.remove(0);
    editor.clear();

    assertThat(firstList).containsExactly("a", "b", "c");
    assertThat(items(first)).containsExactly("a", "b", "c");
    assertThat(firstView.asList()).containsExactly("a", "b", "c");
    assertThat(first.contains("a")).isTrue();
    assertThatThrownBy(() -> firstList.add("x")).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void editorShouldReturnIndependentEditors() {
    ListDomain<String> domain = ListDomain.of("a", "b");
    ListEditor<String> first = domain.editor();
    ListEditor<String> second = domain.editor();

    assertThat(first).isNotSameAs(second);

    first.add("c");

    assertThat(asList(first)).containsExactly("a", "b", "c");
    assertThat(asList(second)).containsExactly("a", "b");
  }

  @Test
  void fromShouldReturnAListDomain() {
    ListDomain<String> domain = Domain.from(List.of("a", "b"));
    IndexedView<String> view = domain.requireView(IndexedView.class);

    assertThat(view.asList()).containsExactly("a", "b");
  }

  @Test
  void editorsShouldBeIndependentAndLeaveTheDomainUnchanged() {
    ListDomain<String> domain = ListDomain.of("a", "b");
    IndexedView<String> originalView = domain.requireView(IndexedView.class);
    ListEditor<String> first = domain.editor();
    ListEditor<String> second = domain.editor();

    first.add("c");
    first.addFirst("first");

    assertThat(asList(first)).containsExactly("first", "a", "b", "c");
    assertThat(asList(second)).containsExactly("a", "b");
    assertThat(originalView.asList()).containsExactly("a", "b");
    assertThat(domain.contains("c")).isFalse();

    IndexedView<String> view = domain.requireView(IndexedView.class);

    assertThat(view.asList()).containsExactly("a", "b");
  }

  @Test
  void editorShouldNotAffectPreviouslyCreatedViews() {
    ListDomain<String> domain = ListDomain.of("a", "b");
    IndexedView<String> view = domain.requireView(IndexedView.class);
    ListEditor<String> editor = domain.editor();

    editor.clear();
    editor.add("x");
    editor.add("y");

    assertThat(view.asList()).containsExactly("a", "b");
    assertThat(view.size()).isEqualTo(2);
    assertThat(asList(editor)).containsExactly("x", "y");
  }

  @Test
  void nullableEditorShouldRemainUsableAfterAChange() {
    ListDomain<String> nullable = ListDomain.of("a").nullable();
    ListEditor<String> editor = nullable.editor();

    editor.add(null);
    editor.add("b");

    assertThat(asList(editor)).containsExactly("a", null, "b");
  }

  @Test
  void toStringShouldDescribeTheDomain() {
    assertThat(ListDomain.of("a", "b").toString())
      .isEqualTo("ListDomain[includesNull=false; size=2]");
    assertThat(ListDomain.of("a", "b").nullable().toString())
      .isEqualTo("ListDomain[includesNull=true; size=2]");
  }

  private static <T> List<T> asList(ListEditor<T> editor) {
    List<T> result = new ArrayList<>();

    for(long i = 0; i < editor.size(); i++) {
      result.add(editor.get(i));
    }

    return result;
  }

  private static <T> List<T> items(Domain<T> domain) {
    IndexedView<T> view = domain.requireView(IndexedView.class);

    return view.asList();
  }
}
