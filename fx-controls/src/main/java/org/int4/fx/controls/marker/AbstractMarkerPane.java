package org.int4.fx.controls.marker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Subscription;

/**
 * A base class for creating marker overlays on top of arbitrary content.
 * <p>
 * This pane manages a dedicated overlay layer in which markers can be
 * attached to nodes. Markers automatically track their target nodes as they
 * move, resize, or are removed from the scene. Subclasses control marker
 * placement by implementing {@link #computeMarkerPosition(Node)}.
 * <p>
 * Usage typically involves:
 * <ol>
 *   <li>Calling {@link #setContent(Node)} to set the main content of the pane.</li>
 *   <li>Calling {@link #putMarker(Node, Node)} to associate marker nodes with targets.</li>
 * </ol>
 */
public abstract class AbstractMarkerPane extends Region {
  record Marker(Node node, Subscription subscription) {
    void delete() {
      if(node.getParent() instanceof Pane p) {
        p.getChildren().remove(node);
      }

      subscription.unsubscribe();
    }

    void reposition(Point2D overlayCoords) {
      Bounds bounds = node.getLayoutBounds();

      node.relocate(overlayCoords.getX() + bounds.getMinX(), overlayCoords.getY() + bounds.getMinY());
    }
  }

  private final Pane overlay = new Pane();
  private final Map<Node, Marker> markers = new HashMap<>();

  private Node content;

  /**
   * Creates a new marker overlay pane.
   */
  public AbstractMarkerPane() {
    overlay.getStyleClass().add("overlay");
    overlay.setMouseTransparent(false);
    overlay.setPickOnBounds(false);
    overlay.setManaged(false);
  }

  /**
   * Sets the main content of this overlay pane.
   * <p>
   * The content is displayed below the overlay layer, which hosts any markers
   * added via {@link #putMarker(Node, Node)}.
   *
   * @param content the content node to display beneath the overlay, can be {@code null}
   */
  public final void setContent(Node content) {
    this.content = content;

    if(content == null) {
      getChildren().clear();
    }
    else {
      getChildren().setAll(content, overlay);
    }
  }

  /**
   * Associates a marker node with the given target node.
   * <p>
   * If a marker is already associated with this node, it is first removed.
   * The marker's position will automatically update when the node moves, resizes,
   * or is removed from the scene. Cleanup is handled automatically.
   *
   * @param target the target {@link Node} to attach the marker to, cannot be {@code null}
   * @param marker the marker {@link Node} to display, cannot be {@code null}
   * @throws NullPointerException if {@code target} or {@code marker} is {@code null}
   */
  protected final void putMarker(Node target, Node marker) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(marker, "marker");

    removeMarker(target);

    /*
     * We must observe both bounds and local-to-scene transform changes:
     * a node can move in scene space (due to parent layout or transforms)
     * without its layoutBounds/boundsInLocal changing. Either case requires
     * repositioning the marker, so we convert both into a layout request.
     * Also, the observation must be done with change listeners, as the
     * localToSceneTransform may already be invalid.
     */

    Subscription subscription = Subscription.combine(
      target.localToSceneTransformProperty().subscribe((ov, nv) -> requestLayout()),
      target.boundsInLocalProperty().subscribe((ov, nv) -> requestLayout()),
      target.sceneProperty().subscribe(v -> {
        if(v == null) {
          removeMarker(target);
        }
      })
    );

    /*
     * Don't be tempted to move the marker to its intended position here,
     * as the marker will not have CSS applied yet at this stage. Instead, wait
     * for layoutChildren to be called, and position it there. CSS will then
     * have been correctly applied and the marker will have its intended size.
     */

    markers.put(target, new Marker(marker, subscription));
    overlay.getChildren().add(marker);
  }

  /**
   * Returns the marker node associated with the given target node, if any.
   *
   * @param target the target {@link Node} to look up, cannot be {@code null}
   * @return the associated marker {@link Node}, or {@code null} if no marker exists for the target
   * @throws NullPointerException if {@code target} is {@code null}
   */
  protected final Node getMarkerNode(Node target) {
    Marker marker = markers.get(Objects.requireNonNull(target, "target"));

    return marker == null ? null : marker.node();
  }

  /**
   * Removes the marker associated with the given target node, if any.
   * <p>
   * Automatically unsubscribes from property listeners and removes the marker from the overlay.
   *
   * @param target the target node whose marker should be removed, cannot be {@code null}
   * @throws NullPointerException if {@code target} is {@code null}
   */
  protected final void removeMarker(Node target) {
    Marker marker = markers.remove(Objects.requireNonNull(target, "target"));

    if(marker != null) {
      marker.delete();
    }
  }

  @Override
  protected final double computeMinWidth(double height) {
    return snapSizeX((content != null ? content.minWidth(height) : 0) + getInsets().getLeft() + getInsets().getRight());
  }

  @Override
  protected final double computeMinHeight(double width) {
    return snapSizeY((content != null ? content.minHeight(width) : 0) + getInsets().getTop() + getInsets().getBottom());
  }

  @Override
  protected final double computePrefWidth(double height) {
    return snapSizeX((content != null ? content.prefWidth(height) : 0) + getInsets().getLeft() + getInsets().getRight());
  }

  @Override
  protected final double computePrefHeight(double width) {
    return snapSizeY((content != null ? content.prefHeight(width) : 0) + getInsets().getTop() + getInsets().getBottom());
  }

  @Override
  protected final void layoutChildren() {
    if(content != null) {
      content.resizeRelocate(0, 0, getWidth(), getHeight());
      overlay.resizeRelocate(0, 0, getWidth(), getHeight());

      for(Map.Entry<Node, Marker> e : markers.entrySet()) {
        Node target = e.getKey();

        e.getValue().reposition(overlay.sceneToLocal(target.localToScene(computeMarkerPosition(target))));
      }
    }
  }

  /**
   * Return the desired marker position in the coordinate space of the given
   * node.
   *
   * @param node the {@link Node} on which a marker needs positioning, cannot be {@code null}
   * @return a {@link Point2D}, never {@code null}
   */
  protected abstract Point2D computeMarkerPosition(Node node);
}
