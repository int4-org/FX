package org.int4.fx.layout.flex;

import java.util.Objects;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import org.int4.fx.builders.common.AbstractRegionBuilder;
import org.int4.fx.builders.context.BuildContext;

/**
 * Builder for {@link Flex} instances.
 */
public final class FlexBuilder extends AbstractRegionBuilder<Flex, FlexBuilder> {

  private final Orientation orientation;

  /**
   * Creates a new builder with the given orientation and optional style classes.
   *
   * @param orientation the initial orientation, can be {@code null} to use
   *   {@link Orientation#HORIZONTAL}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public FlexBuilder(Orientation orientation, String... styleClasses) {
    super(styleClasses);

    this.orientation = orientation;
  }

  /**
   * Enables aligned mode.
   *
   * @return the fluent builder, never {@code null}
   * @see Flex#setAligned(boolean)
   */
  public FlexBuilder aligned() {
    return aligned(true);
  }

  /**
   * Sets aligned mode.
   *
   * @param aligned whether aligned mode should be enabled
   * @return the fluent builder, never {@code null}
   * @see Flex#setAligned(boolean)
   */
  public FlexBuilder aligned(boolean aligned) {
    return apply(flex -> flex.setAligned(aligned));
  }

  /**
   * Sets whether children should fill the cross axis.
   *
   * @param fill whether children should fill the cross axis
   * @return the fluent builder, never {@code null}
   * @see Flex#setFill(boolean)
   */
  public FlexBuilder fill(boolean fill) {
    return apply(flex -> flex.setFill(fill));
  }

  /**
   * Sets the spacing between children along the orientation.
   *
   * @param spacing the spacing
   * @return the fluent builder, never {@code null}
   * @see Flex#setSpacing(double)
   */
  public FlexBuilder spacing(double spacing) {
    return apply(flex -> flex.setSpacing(spacing));
  }

  /**
   * Sets the alignment of the content.
   *
   * @param alignment the alignment, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code alignment} is {@code null}
   * @see Flex#setAlignment(Pos)
   */
  public FlexBuilder alignment(Pos alignment) {
    Objects.requireNonNull(alignment, "alignment");

    return apply(flex -> flex.setAlignment(alignment));
  }

  /**
   * Sets the orientation.
   *
   * @param orientation the orientation, can be {@code null} to use
   *   {@link Orientation#HORIZONTAL}
   * @return the fluent builder, never {@code null}
   * @see Flex#setOrientation(Orientation)
   */
  public FlexBuilder orientation(Orientation orientation) {
    return apply(flex -> flex.setOrientation(orientation));
  }

  /**
   * Configures the Flex with the given children.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see Flex#getChildren()
   */
  public FlexBuilder nodes(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return applyChildrenStrategy(nodes, (flex, children) -> flex.getChildren().setAll(children));
  }

  /**
   * Creates the Flex with the given children.
   *
   * @param nodes the nodes or other supported objects to add, with {@code null} elements
   *   skipped; the array cannot be {@code null}
   * @return the created node, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   * @see Flex#getChildren()
   */
  public Flex with(Object... nodes) {
    nodes(nodes);

    return build();
  }

  @Override
  public Flex build(BuildContext context) {
    return initialize(context, new Flex(orientation));
  }
}
