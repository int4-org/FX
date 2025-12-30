package org.int4.fx.builders.pane;

import javafx.scene.layout.VBox;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.internal.Builders;

/**
 * Builder for {@link VBox} instances.
 */
public final class VBoxBuilder extends AbstractRegionBuilder<VBox, VBoxBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public VBoxBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Creates the vbox with the given items.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created {@link VBox}, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see VBox#getChildren()
   */
  public VBox nodes(Object... nodes) {
    return initialize(new VBox(Builders.toNodes(nodes)));
  }
}
