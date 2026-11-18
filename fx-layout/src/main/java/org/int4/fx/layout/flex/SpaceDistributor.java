package org.int4.fx.layout.flex;

import java.util.Objects;

/**
 * Algorithm for distributing space given min and max size restrictions.
 */
// TODO perhaps instantiate this class so render scale can be a field
class SpaceDistributor {
    private static final double[] EMPTY = new double[0];

    /**
     * Adjusts the given sizes toward the given target sizes to fit within a specified amount of space,
     * quantizing them according to the given render scale. If the sizes cannot be adjusted, returns a
     * quantized copy of either the given sizes or the target sizes, otherwise returns the quantized
     * sizes that fit.
     * <p>
     * If {@code renderScale} is a positive value, all input values and the returned sizes
     * are rounded up to the nearest pixel boundary (quantized). A value of zero means
     * infinite resolution, so rounding is skipped.
     * <p>
     * If the direction of change (shrinking or growing, depending on the given space) is
     * incompatible with the given target sizes, the method returns a quantized copy of {@code sizes}
     * because the values cannot be adjusted in the correct direction. More formally,
     * a quantized copy of {@code sizes} is returned if:
     * <ul>
     * <li>{@code space < sum(sizes) && sum(targetSizes) > sum(sizes)}</li>
     * <li>{@code space > sum(sizes) && sum(targetSizes) < sum(sizes)}</li>
     * </ul>
     * <p>
     * If the given space is more constrained than the sum of the target sizes, the
     * method returns the quantized target sizes as the closest possible solution. More formally,
     * a quantized copy of {@code targetSizes} is returned if:
     * <ul>
     * <li>{@code sum(targetSizes) < sum(sizes) && sum(targetSizes) > space}</li>
     * <li>{@code sum(targetSizes) > sum(sizes) && sum(targetSizes) < space}</li>
     * </ul>
     * <p>
     * <b>Note:</b> Due to floating-point precision limitations, the sum of the
     * returned sizes is not guaranteed to exactly equal {@code space}. Small
     * rounding errors may occur.
     *
     * TODO document that each component of sizes and target sizes must be equal or smaller/larger depending on resize direction
     *
     * @param space the size of the space in which to distribute the given sizes, cannot be negative
     * @param renderScale a render scale, cannot be negative
     * @param sizes an array containing the initial sizes, cannot be {@code null} and will not be modified
     * @param targetSizes an array containing the target sizes, cannot be {@code null} and will not be modified
     * @return a new array with the adjusted sizes, never {@code null}
     * @throws NullPointerException when any argument is {@code null}
     * @throws IllegalArgumentException when the two arrays are not the same size, if a size
     *   exceeds its corresponding target size, if {@code space} is negative, or if {@code renderScale} is negative
     */
    public static double[] distribute(double space, double renderScale, double[] sizes, double[] targetSizes) {
        if (Objects.requireNonNull(sizes, "sizes").length != Objects.requireNonNull(targetSizes, "targetSizes").length) {
            throw new IllegalArgumentException("sizes and targetSizes arrays must be of the same length");
        }

        if (space < 0) {
            throw new IllegalArgumentException("space cannot be negative: " + space);
        }

        if (renderScale < 0) {
            throw new IllegalArgumentException("renderScale cannot be negative: " + renderScale);
        }

        if (sizes.length == 0) {
            return EMPTY;
        }

        double[] quantizedSizes = ceil(sizes, renderScale);
        double spaceRequired = round(sum(quantizedSizes), renderScale);
        double quantizedSpace = ceil(space, renderScale);
        double available = round(quantizedSpace - spaceRequired, renderScale);

        if (available == 0) {
            return quantizedSizes;
        }

        boolean grow = available > 0;
        double[] quantizedTargetSizes = ceil(targetSizes, renderScale);
        double targetSpaceRequired = round(sum(quantizedTargetSizes), renderScale);

        // if the target sizes can't be reached when growing/shrinking (it's the wrong way), then return current sizes:
        if (grow == (targetSpaceRequired < spaceRequired)) {
            return quantizedSizes;
        }

        // if the target sizes are smaller/larger than the intended space, then return the target sizes:
        if (grow == (targetSpaceRequired < quantizedSpace)) {
            return quantizedTargetSizes;
        }

        balanceAvailableSpacePreScaled(available, renderScale, quantizedSizes, quantizedTargetSizes);

        return quantizedSizes;
    }

    /*
     * Balances the available space amongst any resizable spaces, taking into account their
     * target sizes. When the available space is negative, spaces will be shrunk and their
     * target size represents a minimum value. When it is positive, spaces will be grown
     * and their target size represents a maximum value.
     *
     * The input targetSizes array is not modified.
     *
     * Note: when renderScale is not zero, all input values must be on whole pixel boundaries.
     * Furthermore, all newly calculated values in this method are also rounded to pixel boundaries.
     */
    private static void balanceAvailableSpacePreScaled(double initialAvailableSpace, double renderScale, double[] sizes, double[] targetSizes) {
        int count = sizes.length;
        int resizables = 0;  // Tracks the number of spaces that can still be resized
        boolean grow = initialAvailableSpace >= 0;

        // Tracks the amount of space not yet assigned to or removed from a resizable space. Note
        // that this can be negative, which means too much space is being used and must be reclaimed.
        double availableSpace = initialAvailableSpace;

        /*
         * Determine the amount of spaces that can be resized, while
         * checking if the input values make sense:
         */

        for (int i = 0; i < count; i++) {
            if (grow && sizes[i] > targetSizes[i]) {
                throw new IllegalArgumentException("growing: sizes[" + i + "] exceeded targetSizes[" + i + "]: " + sizes[i] + " > " + targetSizes[i]);
            }
            else if(!grow && targetSizes[i] > sizes[i]) {
                throw new IllegalArgumentException("shrinking: targetSizes[" + i + "] exceeded sizes[" + i + "]: " + targetSizes[i] + " > " + sizes[i]);
            }

            if (sizes[i] != targetSizes[i]) {
                resizables++;
            }
        }

        assert resizables > 0;

        /*
         * Balances the available space evenly among resizable spaces. Negative available
         * space will result in spaces being shrunk, while positive available space will
         * result in them being grown. When a space reaches its target, it is excluded
         * and any remaining available space is balanced amongst the remaining resizable
         * spaces. This process continues until the available space reaches zero (or is
         * too small to balance evenly), or all resizable spaces have reached their target.
         */

        for (;;) {
            double idealChange = down(availableSpace / resizables, renderScale);

            if (idealChange == 0) {

                /*
                 * There was insufficient space (positive or negative) to balance amongst all
                 * the resizable spaces equally. When quantized it means there was insufficient
                 * space to adjust all resizable spaces by at least one pixel.
                 */

                break;
            }

            int initialResizables = resizables;

            for (int i = 0; i < count; i++) {
                double size = sizes[i];
                double targetSize = targetSizes[i];

                if ((grow && size < targetSize) || (!grow && size > targetSize)) {
                    double x = round(size + idealChange, renderScale);
                    double newSize = grow ? Math.min(targetSize, x) : Math.max(targetSize, x);

                    availableSpace = round(availableSpace - newSize + size, renderScale);
                    sizes[i] = newSize;

                    if (newSize == targetSize) {  // resizable reached target size?
                        --resizables;  // then exclude resizable

                        if (resizables == 0) {  // exit when all resizables reached their target values
                            return;
                        }
                    }
                }
            }

            if (initialResizables == resizables) {

                /*
                 * When values are not quantized, it is possible that after balancing
                 * a tiny bit of available space will be left over due to floating
                 * point rounding errors and magnitude differences of the values involved.
                 * To prevent an infinite loop trying to balance some nano scale amount
                 * of space, the loop is exited when none of the spaces involved reached
                 * their target size and thus all space must have been balanced
                 * successfully (note: due to floating point magnitude differences, the
                 * sum of the final sizes may still not exactly equal the requested
                 * amount of space).
                 */

                break;
            }
        }

        if (renderScale == 0) {  // exit after balancing completed when not dealing with pixels
            return;
        }

        /*
         * When dealing with pixel sized chunks, a final pass is needed to balance left over
         * space that could not be balanced evenly to all spaces that did not reach their
         * target, meaning some spaces will get or lose one extra pixel.
         */

        double pixelSize = 1 / renderScale;

        //assert Math.abs(available) <= round(pixelSize * resizables, renderScale) : "available exceeded threshold: " + available + "; resizables: = " + resizables;

        for (int i = 0; i < count; i++) {
            if (Math.abs(availableSpace) < 0.5 * pixelSize) {  // exit if less than half a pixel is available
                break;
            }

            if (availableSpace < 0) {
                double newSize = round(sizes[i] - pixelSize, renderScale);

                if (newSize >= targetSizes[i]) {
                    sizes[i] = newSize;
                    availableSpace += pixelSize;
                }
            }
            else {
                double newSize = round(sizes[i] + pixelSize, renderScale);

                if (newSize <= targetSizes[i]) {
                    sizes[i] = newSize;
                    availableSpace -= pixelSize;
                }
            }
        }
    }

    // array functions:

    private static double[] ceil(double[] values, double s) {
        double[] ceiledValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            ceiledValues[i] = ceil(values[i], s);
        }

        return ceiledValues;
    }

    private static double sum(double[] values) {
        double sum = 0;

        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }

        return sum;
    }

    // rounding functions:

    private static double down(double x, double scale) {
        return x < 0 ? ceil(x, scale) : floor(x, scale);
    }

    private static double ceil(double x, double scale) {
        if (scale == 0) {
            return x;
        }

        double d = x * scale;

        if (Double.isInfinite(d)) {
            return x;
        }

        return Math.ceil(Math.nextDown(d)) / scale;
    }

    private static double floor(double x, double scale) {
        if (scale == 0) {
            return x;
        }

        double d = x * scale;

        if (Double.isInfinite(d)) {
            return x;
        }

        return Math.floor(Math.nextUp(d)) / scale;
    }

    private static double round(double x, double scale) {
        if (scale == 0) {
            return x;
        }

        double d = x * scale;

        if (Double.isInfinite(d)) {
            return x;
        }

        return Math.rint(d) / scale;
    }
}
