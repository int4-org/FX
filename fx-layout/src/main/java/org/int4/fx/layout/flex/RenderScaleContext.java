package org.int4.fx.layout.flex;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * Provides the horizontal and vertical pixel scale factors for a rendering
 * surface.
 *
 * @param snapScaleX a positive horizontal scale factor, expressed as the
 *        ratio of device pixels to logical pixels on the X axis
 * @param snapScaleY a positive vertical scale factor, expressed as the
 *        ratio of device pixels to logical pixels on the Y axis
 */
record RenderScaleContext(double snapScaleX, double snapScaleY) {

    /**
     * A render scale context that returns 1.0 for both axes, useful for testing.
     */
    public static final RenderScaleContext DEFAULT = new RenderScaleContext(1.0, 1.0);

    public static RenderScaleContext of(Node node) {
      Window window = findWindow(node);

      return window == null
        ? RenderScaleContext.DEFAULT
        : new RenderScaleContext(window.getRenderScaleX(), window.getRenderScaleY());

    }

    private static Window findWindow(Node node) {
      Scene scene = node.getScene();

      return scene == null ? null : scene.getWindow();
    }
}

