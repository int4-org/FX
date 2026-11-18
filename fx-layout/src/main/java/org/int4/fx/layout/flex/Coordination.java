package org.int4.fx.layout.flex;

/**
 * Carries the resolved grid track data an aligned ancestor hands to a
 * coordinated descendant via
 * {@link Flex#layoutCoordinated(Coordination, double, double, double, double)},
 * so the
 * descendant can place its own direct children using tracks the ancestor
 * already resolved instead of computing its own.
 * <p>
 * {@code mainSizes}/{@code mainOffsets} are the receiving node's own
 * main-axis tracks (shared with any siblings walking the same tracks via the
 * common {@code mainCursor}); {@code otherSizes}/{@code otherOffsets} are the
 * other axis' tracks, handed further down should the receiving node itself
 * flatten transparently or cross into an opposite-orientation child.
 * {@code mainOffsets}/{@code otherOffsets} are absolute (not relative to the
 * receiving node) -- the same shared arrays are read by every node in the
 * coordinated subtree via the advancing cursor, so there is nothing to
 * rebase; only this node's own cross-axis position/thickness (used to place
 * its direct children) is node-specific, and that comes from this node's own
 * {@code x}/{@code y}/{@code w}/{@code h} once set, exactly like the
 * non-coordinated layout path already does.
 *
 * @param crossedAxis {@code true} once an opposite-orientation child has
 *   already been crossed into on the path from the aligned root to here
 * @param mainSizes resolved sizes of this node's own main-axis tracks
 * @param mainOffsets absolute start offset of each entry in {@code mainSizes}
 * @param mainCursor shared, mutable index into {@code mainSizes}/{@code mainOffsets}
 * @param otherSizes resolved sizes of the other axis' tracks
 * @param otherOffsets absolute start offset of each entry in {@code otherSizes}
 */
record Coordination(
  boolean crossedAxis,
  double[] mainSizes, double[] mainOffsets, int[] mainCursor,
  double[] otherSizes, double[] otherOffsets
) {}
