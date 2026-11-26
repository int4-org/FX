/**
 * The {@code Flex} layout container and the layout engine it is built on.
 * <p>
 * {@code Flex} is a row/column container whose orientation can be changed
 * at any time, that handles snapping, content bias, and child grow constraints.
 * Instances can be created with the {@link org.int4.fx.layout.flex.Flex#hflex(String...)} and
 * {@link org.int4.fx.layout.flex.Flex#vflex(String...)} builder methods, through the {@link org.int4.fx.layout.flex.Flexes}
 * builder group, or directly via the constructors. A builder can also be
 * obtained with {@link org.int4.fx.layout.flex.Flex#of(javafx.geometry.Orientation, String...)}.
 * <p>
 * When a {@code Flex} is {@link org.int4.fx.layout.flex.Flex#alignedProperty() aligned}, it
 * coordinates the layout of its nested {@code Flex} descendants so their
 * cells line up across siblings, behaving like a grid.
 */
package org.int4.fx.layout.flex;
