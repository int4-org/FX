package org.int4.fx.builders.control;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link SplitPane} instances.
 */
public final class SplitPaneBuilder extends AbstractControlBuilder<SplitPane, SplitPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public SplitPaneBuilder(String... styleClasses) {
    super(SplitPane::new, styleClasses);
  }

  /**
   * Sets the divider positions of the split pane.
   *
   * @param positions the divider positions
   * @return the fluent builder, never {@code null}
   * @see SplitPane#setDividerPositions(double...)
   */
  public SplitPaneBuilder dividerPositions(double... positions) {
    return apply(c -> c.setDividerPositions(positions));
  }

  /**
   * Configures the split pane to use a horizontal orientation.
   *
   * @return the fluent builder, never {@code null}
   * @see Orientation#HORIZONTAL
   * @see SplitPane#setOrientation(Orientation)
   */
  public SplitPaneBuilder horizontal() {
    return orientation(Orientation.HORIZONTAL);
  }

  /**
   * Configures the split pane to use a vertical orientation.
   *
   * @return the fluent builder, never {@code null}
   * @see Orientation#VERTICAL
   * @see SplitPane#setOrientation(Orientation)
   */
  public SplitPaneBuilder vertical() {
    return orientation(Orientation.VERTICAL);
  }

  /**
   * Sets the orientation of the split pane.
   *
   * @param orientation the orientation
   * @return the fluent builder, never {@code null}
   * @see SplitPane#orientationProperty()
   */
  public SplitPaneBuilder orientation(Orientation orientation) {
    return apply(c -> c.setOrientation(orientation));
  }

  /**
   * Creates the split pane with the given items.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created {@link SplitPane}, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see SplitPane#getItems()
   */
  public SplitPane nodes(Object... nodes) {
    SplitPane node = build();

    node.getItems().addAll(Builders.toNodes(nodes));

    return node;
  }
}
