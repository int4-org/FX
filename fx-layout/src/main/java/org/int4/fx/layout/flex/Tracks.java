package org.int4.fx.layout.flex;

/**
 * Resolved per-track information along a single axis, each array alternating
 * {@code [track, gap, track, gap, ..., track]} exactly as returned by
 * {@link FlexLayout#computeExtents(double)} and
 * {@link FlexLayout#computeSpans(double)}.
 * <p>
 * {@code grow} entries are {@code 1.0} if the track should absorb extra space
 * when growing along the main axis of its immediate container (mirroring
 * {@code Priority.ALWAYS} on {@code HBox}/{@code VBox} children), or {@code 0.0}
 * otherwise; gap positions are always {@code 0.0}. Cross-axis tracks are never
 * growable -- only alignment and fill apply there -- so {@code grow} is only
 * meaningful on the array returned by {@code computeSpans}.
 */
record Tracks(double[] mins, double[] prefs, double[] maxs, double[] grow) {}
