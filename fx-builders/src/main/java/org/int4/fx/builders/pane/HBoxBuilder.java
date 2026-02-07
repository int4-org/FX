package org.int4.fx.builders.pane;

import java.util.Objects;

import javafx.scene.layout.HBox;

import org.int4.fx.builders.common.AbstractPaneBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Builder for {@link HBox} instances.
 */
public final class HBoxBuilder extends AbstractPaneBuilder<HBox, HBoxBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public HBoxBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Configures the hbox with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see HBox#getChildren()
   */
  public HBoxBuilder nodes(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return applyChildrenStrategy(nodes, (n, v) -> n.getChildren().setAll(v));
  }

  /**
   * Creates the hbox with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created node, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see HBox#getChildren()
   */
  public HBox with(Object... nodes) {
    nodes(nodes);

    return build();
  }

  @Override
  public HBox build(BuildContext context) {
    return initialize(context, new HBox());
  }
}
