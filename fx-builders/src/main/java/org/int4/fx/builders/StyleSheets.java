package org.int4.fx.builders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Utility class for working with CSS stylesheets in JavaFX.
 */
public class StyleSheets {

  /**
   * Encodes a CSS stylesheet as a Base64 data URL, suitable for use with
   * {@link javafx.scene.Scene#getStylesheets()}.
   *
   * @param styleSheet the CSS stylesheet content, cannot be {@code null}
   * @return a Base64-encoded data URL representing the stylesheet, never {@code null}
   * @throws NullPointerException if {@code styleSheet} is {@code null}
   */
  public static String inline(String styleSheet) {
    byte[] bytes = Objects.requireNonNull(styleSheet, "styleSheet").getBytes(StandardCharsets.UTF_8);
    StringBuilder builder = new StringBuilder();

    builder.append("data:text/css;charset=UTF-8;base64,");
    builder.append(Base64.getEncoder().encodeToString(bytes));

    return builder.toString();
  }

  private StyleSheets() {}
}
