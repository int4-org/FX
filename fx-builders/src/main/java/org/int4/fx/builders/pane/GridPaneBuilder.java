package org.int4.fx.builders.pane;

import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.internal.Builders;
import org.int4.fx.builders.internal.VisibilityProxy;

/**
 * Builder for {@link GridPane} instances.
 */
public final class GridPaneBuilder extends AbstractRegionBuilder<GridPane, GridPaneBuilder> {
  private int rowIndex;

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public GridPaneBuilder(String... styleClasses) {
    super(styleClasses);
  }

  /**
   * Makes the grid lines visible.
   *
   * @return the fluent builder, never {@code null}
   * @see GridPane#gridLinesVisibleProperty()
   */
  public GridPaneBuilder gridLinesVisible() {
    return apply(gp -> gp.setGridLinesVisible(true));
  }

  /**
   * Adds a row of nodes to the grid.
   *
   * @param nodes the nodes or other supported objects to add in the row, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see GridPane#addRow(int, javafx.scene.Node...)
   */
  public GridPaneBuilder row(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return apply(gp -> gp.addRow(rowIndex++, Builders.toNodes(nodes)));
  }

  /**
   * Adds a row of nodes to the grid with conditional visibility.
   *
   * @param displayed an observable controlling visibility of the row, cannot be {@code null}
   * @param nodes the nodes or builders to add in the row, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code displayed} or the {@code nodes} array is {@code null}
   */
  public GridPaneBuilder row(ObservableValue<Boolean> displayed, Object... nodes) {
    Objects.requireNonNull(displayed, "displayed");
    Objects.requireNonNull(nodes, "nodes");

    return apply(gp -> {
      Node[] finalNodes = Builders.toNodes(nodes);

      for(int i = 0; i < finalNodes.length; i++) {
        // TODO a Group wrapper is not fully transparent to say CSS descendant selector, and perhaps min/max sizes
        finalNodes[i] = new VisibilityProxy(finalNodes[i]);  // wrapper to get a guaranteed visible/managed property that is not used by anything
      }

      gp.addRow(rowIndex++, finalNodes);

      displayed.subscribe(v -> {
        for(Node node : finalNodes) {
          VisibilityProxy proxy = (VisibilityProxy) node;

          proxy.setWrapperVisible(v);
          proxy.setWrapperManaged(v);
        }
      });
    });
  }

  @Override
  public GridPane build() {
    return initialize(new GridPane());
  }
}
