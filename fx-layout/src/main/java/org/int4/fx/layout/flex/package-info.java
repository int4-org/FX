/**
 * The {@code Flex} layout container and the layout engine it is built on.
 * <p>
 * {@code Flex} is a row/column container whose orientation can be changed
 * at any time, that handles snapping, content bias, and child grow constraints.
 * Instances can be created directly with {@link Flex#hflex(javafx.scene.Node...)},
 * {@link Flex#vflex(javafx.scene.Node...)}, or through the {@link Flexes}
 * builder group. A builder can also be obtained with
 * {@link Flex#of(String...)}.
 * <p>
 * When a {@code Flex} is {@link Flex#alignedProperty() aligned}, it
 * coordinates the layout of its nested {@code Flex} descendants so their
 * cells line up across siblings, behaving like a grid.
 */
package org.int4.fx.layout.flex;
