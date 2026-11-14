package org.int4.fx.builders.pane;

import java.util.Objects;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.FlowPane;

import org.int4.fx.builders.common.AbstractPaneBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Builder for {@link FlowPane} instances.
 */
public final class FlowPaneBuilder extends AbstractPaneBuilder<FlowPane, FlowPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if any argument is {@code null}
   */
  public FlowPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Configures the flow pane with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see FlowPane#getChildren()
   */
  public FlowPaneBuilder nodes(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return applyChildrenStrategy(nodes, (n, v) -> n.getChildren().setAll(v));
  }

  /**
   * Creates the flow pane with the given nodes.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created node, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see FlowPane#getChildren()
   */
  public FlowPane with(Object... nodes) {
    nodes(nodes);

    return build();
  }

  /**
   * Sets the flow pane orientation.
   *
   * @param orientation the orientation, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code orientation} is {@code null}
   * @see FlowPane#setOrientation(Orientation)
   */
  public FlowPaneBuilder orientation(Orientation orientation) {
    Objects.requireNonNull(orientation, "orientation");

    return apply(fp -> fp.setOrientation(orientation));
  }

  /**
   * Sets the horizontal gap between nodes.
   *
   * @param gap the horizontal gap
   * @return the fluent builder, never {@code null}
   * @see FlowPane#setHgap(double)
   */
  public FlowPaneBuilder hgap(double gap) {
    return apply(fp -> fp.setHgap(gap));
  }

  /**
   * Sets the vertical gap between nodes.
   *
   * @param gap the vertical gap
   * @return the fluent builder, never {@code null}
   * @see FlowPane#setVgap(double)
   */
  public FlowPaneBuilder vgap(double gap) {
    return apply(fp -> fp.setVgap(gap));
  }

  /**
   * Sets both the horizontal and vertical gaps between nodes.
   *
   * @param gap the horizontal and vertical gap
   * @return the fluent builder, never {@code null}
   * @see FlowPane#setHgap(double)
   * @see FlowPane#setVgap(double)
   */
  public FlowPaneBuilder gap(double gap) {
    return hgap(gap).vgap(gap);
  }

  /**
   * Sets the overall content alignment.
   *
   * @param alignment the alignment, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code alignment} is {@code null}
   * @see FlowPane#setAlignment(Pos)
   */
  public FlowPaneBuilder alignment(Pos alignment) {
    Objects.requireNonNull(alignment, "alignment");

    return apply(fp -> fp.setAlignment(alignment));
  }

  /**
   * Sets the preferred wrap length.
   *
   * @param length the preferred wrap length
   * @return the fluent builder, never {@code null}
   * @see FlowPane#setPrefWrapLength(double)
   */
  public FlowPaneBuilder prefWrapLength(double length) {
    return apply(fp -> fp.setPrefWrapLength(length));
  }

  /**
   * Sets the horizontal alignment of nodes within each column.
   *
   * @param alignment the column alignment, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code alignment} is {@code null}
   * @see FlowPane#setColumnHalignment(HPos)
   */
  public FlowPaneBuilder columnHalignment(HPos alignment) {
    Objects.requireNonNull(alignment, "alignment");

    return apply(fp -> fp.setColumnHalignment(alignment));
  }

  /**
   * Sets the vertical alignment of nodes within each row.
   *
   * @param alignment the row alignment, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code alignment} is {@code null}
   * @see FlowPane#setRowValignment(VPos)
   */
  public FlowPaneBuilder rowValignment(VPos alignment) {
    Objects.requireNonNull(alignment, "alignment");

    return apply(fp -> fp.setRowValignment(alignment));
  }

  @Override
  public FlowPane build(BuildContext context) {
    return initialize(context, new FlowPane());
  }
}
