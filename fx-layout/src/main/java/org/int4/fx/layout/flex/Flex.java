package org.int4.fx.layout.flex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.int4.fx.layout.internal.Layoutable;

/**
 * Flex lays out its children in a single horizontal row or vertical column.
 * Unlike {@code HBox} and {@code VBox}, whose orientation is fixed, a Flex's
 * {@link #setOrientation(Orientation) orientation} can be changed at any time,
 * and is reflected in the {@code :horizontal} and {@code :vertical}
 * pseudo-classes, so a single Flex can be used in place of either. The
 * {@link #hflex(String...)} and {@link #vflex(String...)} builder factories
 * create a Flex with a given orientation:
 * <pre>{@code
 *     Flex row = Flex.hflex().with(new Label("Name:"), new TextField());
 *     row.setSpacing(8);
 * }</pre>
 * If the Flex has a border and/or padding set, then the contents will be laid
 * out within those insets.
 * <p>
 * Flex will resize children (if resizable) to their preferred sizes along its
 * own orientation, and uses its fill property to determine whether to resize
 * them along the other orientation to fill its own extent in that orientation,
 * or keep them at their preferred sizes (fill defaults to true).
 * The alignment of the content is controlled by the alignment property,
 * which defaults to Pos.TOP_LEFT.
 * <p>
 * If a Flex is resized larger than its preferred size along its orientation,
 * by default it keeps children at their preferred size, leaving the extra
 * space unused. An application that wants one or more children to be allocated
 * that extra space may set a {@link Priority#ALWAYS grow} constraint on the
 * child -- see "Optional Layout Constraints" below.
 * <p>
 * Flex lays out each managed child regardless of the child's visible property
 * value; unmanaged children are ignored.
 *
 * <h2>Resizable Range</h2>
 * <p>
 * A Flex's parent will resize the Flex within the Flex's resizable range
 * during layout. By default the Flex computes this range based on its content
 * as outlined in the table below.
 * <table border="1">
 * <caption>Flex Resize Table</caption>
 * <tr><td></td><th scope="col">along orientation</th><th scope="col">across orientation</th></tr>
 * <tr><th scope="row">minimum</th>
 * <td>leading/trailing insets plus the sum of each child's min size along that axis, plus spacing between children</td>
 * <td>leading/trailing insets plus the largest of the children's min sizes across that axis</td></tr>
 * <tr><th scope="row">preferred</th>
 * <td>leading/trailing insets plus the sum of each child's pref size along that axis, plus spacing between children</td>
 * <td>leading/trailing insets plus the largest of the children's pref sizes across that axis</td></tr>
 * <tr><th scope="row">maximum</th>
 * <td>unbounded by default</td>
 * <td>unbounded by default</td></tr>
 * </table>
 * <p>
 * "Along orientation" means width when {@link Orientation#HORIZONTAL}, height
 * when {@link Orientation#VERTICAL}; "across orientation" is the other one.
 * A Flex's maximum size is unbounded by default. Set
 * {@link Region#setMaxWidth(double) maxWidth} or
 * {@link Region#setMaxHeight(double) maxHeight} to constrain it, or use the
 * CSS {@code -fx-max-width} and {@code -fx-max-height} properties.
 *
 * <h2>Optional Layout Constraints</h2>
 * <p>
 * An application may set constraints on individual children to customize a
 * Flex's layout, using the static methods below.
 * <table border="1">
 * <caption>Flex Constraint Table</caption>
 * <tr><th scope="col">Constraint</th><th scope="col">Type</th><th scope="col">Description</th></tr>
 * <tr><th scope="row">grow</th><td>{@link Priority}</td>
 * <td>Whether the child should grow along this Flex's orientation when extra space is available; only {@link Priority#ALWAYS} has any effect.</td></tr>
 * </table>
 *
 * <h2>Aligned Mode</h2>
 * <p>
 * Setting the {@link #alignedProperty() aligned} property makes a Flex
 * coordinate the layout of its nested Flex descendants so their cells line up
 * across siblings: an aligned, vertical Flex containing horizontal Flex
 * children lines up each row's columns with every other row's columns (or,
 * mirrored, an aligned horizontal Flex with vertical children lines up each
 * column's rows). In the notation below, {@code v[...]} is a vertical Flex and
 * {@code h[...]} a horizontal one, each containing the listed children:
 * <pre>{@code
 *     v[h[A, B], h[C, D]]     // unaligned: two independent rows -- B does not line up with D
 *     av[h[A, B], h[C, D]]    // aligned: B lines up with D
 * }</pre>
 * Only the first crossing from the aligned root's own orientation into the
 * opposite orientation is treated as forming aligned rows/columns; anything
 * nested inside one of those cells -- regardless of its own orientation -- is
 * an opaque leaf and manages its own layout independently, exactly as it
 * would outside an aligned tree.
 * <p>
 * A nested Flex with the SAME orientation as the aligned root is instead
 * flattened transparently, wherever it appears below the root before the
 * first orientation crossing -- its children are treated as if they were
 * direct children of the root, without forming a row/column of their own --
 * letting an application group several rows/columns together (e.g. to give
 * them a shared border, background, or visibility toggle) without disturbing
 * the alignment they contribute to:
 * <pre>{@code
 *     av[v[h[A, B], h[C, D]], h[E, F]]   // v[...] flattens: still 2 rows of 2 columns each, all aligned
 * }</pre>
 */
public class Flex extends Region {

  /**
   * Creates a builder for a Flex with the given orientation.
   *
   * @param orientation the initial orientation, can be {@code null} to use
   *   {@link Orientation#HORIZONTAL}
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public static FlexBuilder of(Orientation orientation, String... styleClasses) {
    return new FlexBuilder(orientation, styleClasses);
  }

  /**
   * Creates a builder for a horizontal Flex.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public static FlexBuilder hflex(String... styleClasses) {
    return of(Orientation.HORIZONTAL, styleClasses);
  }

  /**
   * Creates a builder for a vertical Flex.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   * @return a fluent builder, never {@code null}
   * @throws NullPointerException if {@code styleClasses} is {@code null}
   */
  public static FlexBuilder vflex(String... styleClasses) {
    return of(Orientation.VERTICAL, styleClasses);
  }

  /**
   * Returns the grow constraint of the given child.
   *
   * @param child the child to query, cannot be {@code null}
   * @return the child's grow constraint, never {@code null}
   * @throws NullPointerException if {@code child} is {@code null}
   */
  public static Priority getGrow(Node child) {
    return FlexLayout.getGrow(Objects.requireNonNull(child, "child"));
  }

  /**
   * Sets the grow constraint of the given child.
   * <p>
   * Only {@link Priority#ALWAYS} has an effect; any other value behaves like
   * {@link Priority#NEVER}.
   *
   * @param child the child to configure, cannot be {@code null}
   * @param priority the grow constraint, cannot be {@code null}
   * @throws NullPointerException if {@code child} or {@code priority} is {@code null}
   */
  public static void setGrow(Node child, Priority priority) {
    FlexLayout.setGrow(
      Objects.requireNonNull(child, "child"),
      Objects.requireNonNull(priority, "priority")
    );

    if(child.getParent() != null) {
      child.getParent().requestLayout();
    }
  }

  private static final CssMetaData<Flex, Boolean> ALIGNED = new CssMetaData<>("-fx-aligned", StyleConverter.getBooleanConverter(), false) {
    @Override
    public boolean isSettable(Flex node) {
      return node.aligned == null || !node.aligned.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<Boolean> getStyleableProperty(Flex node) {
      return (StyleableProperty<Boolean>)node.alignedProperty();
    }
  };

  private static final CssMetaData<Flex, Boolean> FILL = new CssMetaData<>("-fx-fill", StyleConverter.getBooleanConverter(), true) {
    @Override
    public boolean isSettable(Flex node) {
      return node.fill == null || !node.fill.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<Boolean> getStyleableProperty(Flex node) {
      return (StyleableProperty<Boolean>)node.fillProperty();
    }
  };

  private static final CssMetaData<Flex, Pos> ALIGNMENT = new CssMetaData<>("-fx-alignment", new EnumConverter<>(Pos.class), Pos.TOP_LEFT) {
    @Override
    public boolean isSettable(Flex node) {
      return node.alignment == null || !node.alignment.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<Pos> getStyleableProperty(Flex node) {
      return (StyleableProperty<Pos>)node.alignmentProperty();
    }
  };

  private static final CssMetaData<Flex, Number> SPACING = new CssMetaData<>("-fx-spacing", SizeConverter.getInstance(), 0.0) {
    @Override
    public boolean isSettable(Flex node) {
      return node.spacing == null || !node.spacing.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<Number> getStyleableProperty(Flex node) {
      return (StyleableProperty<Number>)node.spacingProperty();  // TODO consider just returning styleableproperty in API as well..
    }
  };

  private final FlexLayout layout;

  private Orientation orientation = Orientation.HORIZONTAL;
  private Coordination coordination;
  private boolean layoutSynced;

  /**
   * Creates an empty horizontal Flex.
   */
  public Flex() {
    this(Orientation.HORIZONTAL);
  }

  /**
   * Creates a Flex with the given orientation and children.
   *
   * @param orientation the orientation, can be {@code null} to use
   *   {@link Orientation#HORIZONTAL}
   * @param nodes the children, cannot be {@code null} and cannot contain {@code null}
   * @throws NullPointerException if {@code nodes} is {@code null} or contains {@code null}
   */
  public Flex(Orientation orientation, Node... nodes) {
    this.layout = new FlexLayout();

    Objects.requireNonNull(nodes, "nodes");

    for(Node node : nodes) {
      Objects.requireNonNull(node, "node");
    }

    setOrientation(orientation);
    getChildren().addAll(Arrays.asList(nodes));
  }

  @Override
  public final ObservableList<Node> getChildren() {
    return super.getChildren();
  }

  @Override
  protected double computeMinWidth(double dependentHeight) {
    syncLayout();

    return layout.minWidth(dependentHeight);
  }

  @Override
  protected double computePrefWidth(double dependentHeight) {
    syncLayout();

    return layout.prefWidth(dependentHeight);
  }

  @Override
  protected double computeMinHeight(double dependentWidth) {
    syncLayout();

    return layout.minHeight(dependentWidth);
  }

  @Override
  protected double computePrefHeight(double dependentWidth) {
    syncLayout();

    return layout.prefHeight(dependentWidth);
  }

  @Override
  protected double computeMaxWidth(double dependentHeight) {
    return Double.MAX_VALUE;
  }

  @Override
  protected double computeMaxHeight(double dependentWidth) {
    return Double.MAX_VALUE;
  }

  /**
   * Returns the current orientation.
   *
   * @return the current orientation, never {@code null}
   */
  public final Orientation getOrientation() {
    return orientation;
  }

  /**
   * Sets the orientation.
   * <p>
   * A {@code null} orientation is treated as {@link Orientation#HORIZONTAL}.
   * The orientation is reflected in the {@code :horizontal} and
   * {@code :vertical} pseudo-classes.
   *
   * @param orientation the new orientation, can be {@code null}
   */
  public final void setOrientation(Orientation orientation) {  // final because called in constructor
    this.orientation = orientation == null ? Orientation.HORIZONTAL : orientation;

    pseudoClassStateChanged(PseudoClass.getPseudoClass("horizontal"), this.orientation == Orientation.HORIZONTAL);
    pseudoClassStateChanged(PseudoClass.getPseudoClass("vertical"), this.orientation == Orientation.VERTICAL);

    requestLayout();
  }

  @Override
  public double getBaselineOffset() {
    syncLayout();

    return layout.getBaselineOffset();
  }

  @Override
  protected void layoutChildren() {
    syncLayout();

    if(coordination != null) {
      layout.layoutCoordinated(coordination, 0, 0, getWidth(), getHeight());
    }
    else {
      layout.resizeRelocate(0, 0, getWidth(), getHeight());
    }
  }

  /*
   * Called by an aligned ancestor in place of a normal resize: stores the
   * given tracks and defers to them at layoutChildren()'s own (0, 0) reset --
   * there is a temporal gap between this call and JavaFX actually invoking
   * layoutChildren() (it happens independently, on JavaFX's own schedule),
   * so the tracks must be held onto rather than used immediately, unlike a
   * bare FlexLayout's own layoutCoordinated().
   *
   * The offsets in the given coordination are relative to (x, y) -- e.g. if
   * this Flex is itself nested inside another real Flex (a coordinated chain
   * more than one real Flex deep), they are relative to OUR OWN position,
   * not to whatever real Node sits at the root of the whole chain. Since
   * layoutChildren() always resets our own inner FlexLayout's origin to
   * (0, 0) and relies on JavaFX's own scene graph transform composition to
   * re-add our real position afterward, the offsets must be rebased to be
   * relative to that (0, 0) origin now, before that reset happens -- or our
   * own real position would end up added twice (once by us, once by JavaFX).
   */
  void layoutCoordinated(Coordination coordination, double x, double y, double w, double h) {
    double mainDelta = getOrientation() == Orientation.HORIZONTAL ? x : y;
    double otherDelta = getOrientation() == Orientation.HORIZONTAL ? y : x;

    this.coordination = new Coordination(
      coordination.crossedAxis(),
      coordination.mainSizes(), rebase(coordination.mainOffsets(), mainDelta), coordination.mainCursor(),
      coordination.otherSizes(), rebase(coordination.otherOffsets(), otherDelta)
    );

    resizeRelocate(x, y, w, h);
  }

  private static double[] rebase(double[] offsets, double delta) {
    double[] rebased = new double[offsets.length];

    for(int i = 0; i < offsets.length; i++) {
      rebased[i] = offsets[i] - delta;
    }

    return rebased;
  }

  FlexLayout asFlexLayout() {
    syncLayout();

    return layout;
  }

  private void syncLayout() {
    if(!layoutSynced) {
      layout.setSpacing(getSpacing());
      layout.aligned(isAligned());
      layout.setAlignment(getAlignment());
      layout.setOrientation(getOrientation());
      layout.setFill(isFill());
      layout.setInsets(getInsets());
      layout.setSnapToPixel(isSnapToPixel());
      layout.setRenderScaleContext(RenderScaleContext.of(this));
      layout.setChildren(getManagedChildren().stream().map(Layoutable::of).toList());

      layoutSynced = true;
    }
  }

  @Override
  public Orientation getContentBias() {
    return layout.getContentBias();
  }

  @Override
  public void requestLayout() {
    layout.invalidate();

    coordination = null;
    layoutSynced = false;

    super.requestLayout();
  }

  private StyleableBooleanProperty aligned;

  /**
   * Returns whether aligned mode is enabled.
   *
   * @return whether aligned mode is enabled
   */
  public final boolean isAligned() { return aligned == null ? false : aligned.get(); }

  /**
   * Enables or disables aligned mode.
   *
   * @param value whether aligned mode should be enabled
   */
  public final void setAligned(boolean value) { alignedProperty().set(value); }

  /**
   * The aligned mode of this Flex.
   * <p>
   * When enabled, nested Flex descendants coordinate their layout so their
   * cells line up across siblings.
   *
   * @return the aligned property, never {@code null}
   */
  public final BooleanProperty alignedProperty() {
    if(aligned == null) {
      aligned = new StyleableBooleanProperty() {
        @Override
        public void invalidated() {
          requestLayout();
        }

        @Override
        public CssMetaData<Flex, Boolean> getCssMetaData() {
          return ALIGNED;
        }

        @Override
        public Object getBean() {
          return Flex.this;
        }

        @Override
        public String getName() {
          return "aligned";
        }
      };
    }
    return aligned;
  }

  private StyleableBooleanProperty fill;

  /**
   * Returns whether children are resized across the orientation to fill this
   * Flex.
   *
   * @return whether children fill the cross axis
   */
  public final boolean isFill() { return fill == null ? true : fill.get(); }

  /**
   * Sets whether children should fill this Flex across its orientation.
   *
   * @param value whether children should fill the cross axis
   */
  public final void setFill(boolean value) { fillProperty().set(value); }

  /**
   * The fill mode of this Flex.
   *
   * @return the fill property, never {@code null}
   */
  public final BooleanProperty fillProperty() {
    if(fill == null) {
      fill = new StyleableBooleanProperty(true) {  // default value must be set if it deviates from the property default, otherwise initial invalidation fails if set to the property default
        @Override
        public void invalidated() {
          requestLayout();
        }

        @Override
        public CssMetaData<Flex, Boolean> getCssMetaData() {
          return FILL;
        }

        @Override
        public Object getBean() {
          return Flex.this;
        }

        @Override
        public String getName() {
          return "fill";
        }
      };
    }

    return fill;
  }

  private StyleableDoubleProperty spacing;

  /**
   * Returns the space between children along this Flex's orientation.
   *
   * @return the spacing
   */
  public final double getSpacing() { return spacing == null ? 0 : spacing.get(); }

  /**
   * Sets the space between children along this Flex's orientation.
   *
   * @param value the spacing
   */
  public final void setSpacing(double value) { spacingProperty().set(value); }

  /**
   * The amount of space between each child, along this Flex's orientation.
   *
   * @return the amount of space between each child, along this Flex's orientation
   */
  public final DoubleProperty spacingProperty() {
    if(spacing == null) {
      spacing = new StyleableDoubleProperty() {
        @Override
        public void invalidated() {
          requestLayout();
        }

        @Override
        public CssMetaData<Flex, Number> getCssMetaData() {
          return SPACING;
        }

        @Override
        public Object getBean() {
          return Flex.this;
        }

        @Override
        public String getName() {
          return "spacing";
        }
      };
    }

    return spacing;
  }

  private StyleableObjectProperty<Pos> alignment;

  /**
   * Returns the overall alignment of children within this Flex.
   *
   * @return the alignment, never {@code null}
   */
  public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }

  /**
   * Sets the overall alignment of children within this Flex.
   *
   * @param value the alignment, cannot be {@code null}
   * @throws NullPointerException if {@code value} is {@code null}
   */
  public final void setAlignment(Pos value) { alignmentProperty().set(Objects.requireNonNull(value, "value")); }

  /**
   * The overall alignment of children within the Flex's width and height.
   *
   * @return the overall alignment of children within the Flex's width and height
   */
  public final ObjectProperty<Pos> alignmentProperty() {
    if(alignment == null) {
      alignment = new StyleableObjectProperty<>(Pos.TOP_LEFT) {
        @Override
        public void invalidated() {
          requestLayout();
        }

        @Override
        public CssMetaData<Flex, Pos> getCssMetaData() {
          return ALIGNMENT;
        }

        @Override
        public Object getBean() {
          return Flex.this;
        }

        @Override
        public String getName() {
          return "alignment";
        }
      };
    }

    return alignment;
  }

  private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

  static {
    final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Region.getClassCssMetaData());

    styleables.add(ALIGNED);
    styleables.add(FILL);
    styleables.add(SPACING);
    styleables.add(ALIGNMENT);

    STYLEABLES = Collections.unmodifiableList(styleables);
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return STYLEABLES;
  }
}
