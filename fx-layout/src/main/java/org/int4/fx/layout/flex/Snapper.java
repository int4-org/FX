package org.int4.fx.layout.flex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

interface Snapper {

  /**
   * A default snapper for 1.0 scaling.
   */
  static final Snapper DEFAULT = new Snapper() {
    private static double EPSILON = 1e-6;

    @Override
    public double snapPositionX(double value) {
      return Math.rint(value);
    }

    @Override
    public double snapPositionY(double value) {
      return Math.rint(value);
    }

    @Override
    public double snapSpaceX(double value) {
      return Math.rint(value);
    }

    @Override
    public double snapSpaceY(double value) {
      return Math.rint(value);
    }

    @Override
    public double snapSizeX(double value) {
      return Math.ceil(value - EPSILON);
    }

    @Override
    public double snapSizeY(double value) {
      return Math.ceil(value - EPSILON);
    }
  };

  static final Snapper NO_SNAPPING = new Snapper() {
    @Override
    public double snapPositionX(double v) {
      return v;
    }

    @Override
    public double snapPositionY(double v) {
      return v;
    }

    @Override
    public double snapSizeX(double v) {
      return v;
    }

    @Override
    public double snapSizeY(double v) {
      return v;
    }

    @Override
    public double snapSpaceX(double v) {
      return v;
    }

    @Override
    public double snapSpaceY(double v) {
      return v;
    }
  };

  class Cache {
    private static final Map<RenderScaleContext, Snapper> INSTANCES = new ConcurrentHashMap<>();

    {
      INSTANCES.put(RenderScaleContext.DEFAULT, DEFAULT);
    }
  }

  static Snapper createSnapper(RenderScaleContext context) {
    return Cache.INSTANCES.computeIfAbsent(context, _ -> new Snapper() {
      private static double EPSILON = 1e-6;

      final double ssx = context.snapScaleX();
      final double ssy = context.snapScaleY();
      final double rssx = 1.0 / ssx;
      final double rssy = 1.0 / ssy;

      @Override
      public double snapPositionX(double value) {
        return Math.rint(value * ssx) * rssx;
      }

      @Override
      public double snapPositionY(double value) {
        return Math.rint(value * ssy) * rssy;
      }

      @Override
      public double snapSpaceX(double value) {
        return Math.rint(value * ssx) * rssx;
      }

      @Override
      public double snapSpaceY(double value) {
        return Math.rint(value * ssy) * rssy;
      }

      @Override
      public double snapSizeX(double value) {
        double d = value * ssx;

        if(Double.isInfinite(d)) { // Avoids returning NaN for high magnitude inputs
          return value;
        }

        return Math.ceil(d - EPSILON) * rssx;
      }

      @Override
      public double snapSizeY(double value) {
        double d = value * ssy;

        if(Double.isInfinite(d)) { // Avoids returning NaN for high magnitude inputs
          return value;
        }

        return Math.ceil(d - EPSILON) * rssy;
      }
    });
  }

  double snapPositionX(double value);
  double snapPositionY(double value);
  double snapSizeX(double v);
  double snapSizeY(double v);
  double snapSpaceX(double v);
  double snapSpaceY(double v);
}