package org.int4.fx.builders.pane;

import javafx.scene.layout.HBox;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link HBox} instances.
 */
public final class HBoxBuilder extends AbstractRegionBuilder<HBox, HBoxBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public HBoxBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Creates the hbox with the given items.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created {@link HBox}, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see HBox#getChildren()
   */
  public HBox nodes(Object... nodes) {
    return initialize(new HBox(NodeBuilder.toNodes(nodes)));
  }

  @Override
  public HBox build() {
    return initialize(new HBox());
  }
}
