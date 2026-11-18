package org.int4.fx.scene.flag;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Subscription;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeFlagsTest {
  private static final PseudoClass TEST = PseudoClass.getPseudoClass("test-flag");
  private static final NodeFlag<PseudoClass, Integer> FLAG = NodeFlag.of(TEST);
  private static final NodeFlag<String, Integer> RAW_FLAG = NodeFlag.of("test-raw");

  @BeforeAll
  static void beforeAll() {
    try {
      Platform.startup(() -> {});
    }
    catch(IllegalStateException e) {
      // already started
    }
  }

  @Test
  void shouldStoreAndRemoveValues() {
    Pane node = new Pane();

    assertThat(NodeFlags.get(node, FLAG)).isNull();

    NodeFlags.apply(node, FLAG, 3);

    assertThat(NodeFlags.get(node, FLAG)).isEqualTo(3);
    assertThat(node.getPseudoClassStates()).contains(TEST);

    NodeFlags.apply(node, FLAG, null);

    assertThat(NodeFlags.get(node, FLAG)).isNull();
    assertThat(node.getPseudoClassStates()).doesNotContain(TEST);
  }

  @Test
  void shouldStoreAndRemoveValuesForRawKeys() {
    Pane node = new Pane();

    NodeFlags.apply(node, RAW_FLAG, 7);

    assertThat(NodeFlags.get(node, RAW_FLAG)).isEqualTo(7);

    NodeFlags.apply(node, RAW_FLAG, null);

    assertThat(NodeFlags.get(node, RAW_FLAG)).isNull();
  }

  @Test
  void shouldFindNodesInSubtree() {
    Pane root = new Pane();
    Pane child = new Pane();
    Pane grandChild = new Pane();

    root.getChildren().add(child);
    child.getChildren().add(grandChild);

    NodeFlags.apply(child, FLAG, 1);
    NodeFlags.apply(grandChild, FLAG, 2);

    assertThat(NodeFlags.nodes(root, FLAG)).containsExactlyInAnyOrder(child, grandChild);
  }

  @Test
  void shouldReportValueChangesAndRemoval() {
    Pane root = new Pane();
    Pane child = new Pane();

    root.getChildren().add(child);

    @SuppressWarnings("unused")
    Scene scene = new Scene(root);

    NodeFlags.apply(child, FLAG, 1);

    List<String> events = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(
      root,
      FLAG,
      (node, value) -> events.add("present:" + value),
      node -> events.add("absent")
    );

    assertThat(events).containsExactly("present:1");

    NodeFlags.apply(child, FLAG, 2);

    assertThat(events).containsExactly("present:1", "present:2");

    NodeFlags.apply(child, FLAG, null);

    assertThat(events).containsExactly("present:1", "present:2", "absent");

    subscription.unsubscribe();
  }

  @Test
  void shouldReportNodeRemovedFromObservedSubtree() {
    Pane root = new Pane();
    Pane child = new Pane();

    root.getChildren().add(child);

    @SuppressWarnings("unused")
    Scene scene = new Scene(root);

    NodeFlags.apply(child, FLAG, 1);

    List<Node> absent = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(root, FLAG, (node, value) -> {}, absent::add);

    root.getChildren().remove(child);

    assertThat(absent).containsExactly(child);

    subscription.unsubscribe();
  }

  @Test
  void shouldReportNodeReattachedToObservedSubtree() {
    Pane root = new Pane();
    Pane child = new Pane();

    root.getChildren().add(child);

    @SuppressWarnings("unused")
    Scene scene = new Scene(root);

    NodeFlags.apply(child, FLAG, 1);

    List<Node> present = new ArrayList<>();
    List<Node> absent = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(root, FLAG, (node, value) -> present.add(node), absent::add);

    assertThat(present).containsExactly(child);

    root.getChildren().remove(child);

    assertThat(absent).containsExactly(child);

    root.getChildren().add(child);

    assertThat(present).containsExactly(child, child);
    assertThat(absent).containsExactly(child);

    subscription.unsubscribe();
  }

  @Test
  void shouldReportRootDetachAndReattach() {
    Pane root = new Pane();
    Pane child = new Pane();

    root.getChildren().add(child);

    Scene scene = new Scene(root);

    NodeFlags.apply(child, FLAG, 1);

    List<Node> present = new ArrayList<>();
    List<Node> absent = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(root, FLAG, (node, value) -> present.add(node), absent::add);

    assertThat(present).containsExactly(child);

    scene.setRoot(new Pane());

    assertThat(absent).containsExactly(child);

    scene.setRoot(root);

    assertThat(present).containsExactly(child, child);

    subscription.unsubscribe();
  }

  @Test
  void shouldReportAncestorBranchRemovedFromObservedSubtree() {
    Pane root = new Pane();
    Pane branch = new Pane();
    Pane child = new Pane();

    branch.getChildren().add(child);
    root.getChildren().add(branch);

    @SuppressWarnings("unused")
    Scene scene = new Scene(root);

    NodeFlags.apply(child, FLAG, 1);

    List<Node> absent = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(root, FLAG, (node, value) -> {}, absent::add);

    root.getChildren().remove(branch);

    assertThat(absent).containsExactly(child);

    subscription.unsubscribe();
  }

  @Test
  void shouldReportRemovalWhenObservingScene() {
    Pane root = new Pane();
    Pane child = new Pane();

    root.getChildren().add(child);

    Scene scene = new Scene(root);

    NodeFlags.apply(child, FLAG, 1);

    List<Node> present = new ArrayList<>();
    List<Node> absent = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(scene, FLAG, (node, value) -> present.add(node), absent::add);

    assertThat(present).containsExactly(child);

    root.getChildren().remove(child);

    assertThat(absent).containsExactly(child);

    subscription.unsubscribe();
  }

  @Test
  void shouldReplayStateWhenNodeIsAttachedToScene() {
    Pane root = new Pane();
    Scene scene = new Scene(root);
    Pane child = new Pane();

    NodeFlags.apply(child, FLAG, 5);

    List<String> events = new ArrayList<>();
    Subscription subscription = NodeFlags.observe(scene, FLAG, (node, value) -> events.add("present:" + value), node -> events.add("absent"));

    assertThat(events).isEmpty();

    root.getChildren().add(child);

    assertThat(events).containsExactly("present:5");

    subscription.unsubscribe();
  }
}
