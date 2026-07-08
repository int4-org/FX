package org.int4.fx.builders.control;

import java.lang.ref.WeakReference;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Window;

import org.mockito.Mockito;

import org.int4.fx.builders.Scenes;
import org.int4.fx.builders.common.SceneBuilder;
import org.int4.fx.builders.event.WindowEvent;
import org.int4.fx.scene.event.Broadcasts;
import org.int4.fx.values.model.BooleanModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelLinkerGCTest extends FXTest {

  /**
   * Verifies that when a linked Node is removed from a plain Scene
   * that the node is garbage collected.
   */
  @Test
  void shouldGcWhenRemovedFromPlainScene() {
    BooleanModel model = BooleanModel.of(true);
    CheckBox node = new CheckBoxBuilder()
      .model(model)
      .build();

    Scene scene = new Scene(node);

    // Subscriptions are active (plain Scene activates immediately)
    assertThat(node.isSelected()).isTrue();

    model.set(false);
    assertThat(node.isSelected()).isFalse();

    WeakReference<CheckBox> ref = new WeakReference<>(node);

    node = null;

    // Verify it is still alive while in the scene graph
    ReferenceAsserts.testIfStronglyReferenced(ref);

    // Remove from scene graph
    scene.setRoot(new Label());

    // Model is still referenced, so verify the node is GC'd despite that
    ReferenceAsserts.testIfNotStronglyReferenced(ref);
  }

  /**
   * Verifies that when a linked Node is removed from a Scene that has
   * the SHOW_STATE_MANAGING_SCENE flag that the node is garbage collected.
   */
  @Test
  void shouldGcWhenRemovedFromShowStateManagingScene() {
    BooleanModel model = BooleanModel.of(true);
    CheckBox node = new CheckBoxBuilder()
      .model(model)
      .build();

    Scene scene = new SceneBuilder(node).build();
    WeakReference<CheckBox> ref = new WeakReference<>(node);

    node = null;

    // Verify it is still alive while in the scene graph
    ReferenceAsserts.testIfStronglyReferenced(ref);

    // Remove from scene graph
    scene.setRoot(new Label());

    // Model is still referenced, so verify the node is GC'd despite that
    ReferenceAsserts.testIfNotStronglyReferenced(ref);
  }

  /**
   * Verifies that when a HIDDEN broadcast is sent to a node in a
   * show-state-managing scene, subscriptions are terminated and the
   * node is garbage collected.
   */
  @Test
  void shouldGcWhenWindowHiddenBroadcastReceived() {
    BooleanModel model = BooleanModel.of(true);
    CheckBox node = new CheckBoxBuilder()
      .model(model)
      .build();

    new SceneBuilder(node).build();

    Window mockWindow = Mockito.mock(Window.class);

    // Mimic WINDOW_SHOWING: activate subscriptions
    Broadcasts.broadcast(node, WindowEvent.showing(mockWindow));

    // Subscriptions are now active
    assertThat(node.isSelected()).isTrue();

    WeakReference<CheckBox> ref = new WeakReference<>(node);

    node = null;

    // Still alive while showing
    ReferenceAsserts.testIfStronglyReferenced(ref);

    // Mimic WINDOW_HIDDEN: terminate subscriptions
    Broadcasts.broadcast(ref.get(), WindowEvent.hidden(mockWindow));

    // Model is still referenced, verify the node is GC'd
    ReferenceAsserts.testIfNotStronglyReferenced(ref);
  }
}
