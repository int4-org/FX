package org.int4.fx.builders.control;

import java.util.Objects;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import org.int4.fx.builders.common.AbstractControlBuilder;
import org.int4.fx.builders.common.NodeBuilder;

/**
 * Builder for {@link TabPane} instances.
 */
public final class TabPaneBuilder extends AbstractControlBuilder<TabPane, TabPaneBuilder> {

  /**
   * Creates a new builder with optional style classes.
   *
   * @param styleClasses the style classes, cannot be {@code null} but can be empty
   */
  public TabPaneBuilder(String... styleClasses) {
    super(TabPane::new, styleClasses);
  }

  /**
   * Sets the side on which the tabs are displayed.
   *
   * @param side the side to place the tabs on
   * @return the fluent builder, never {@code null}
   * @see TabPane#setSide(Side)
   */
  public TabPaneBuilder side(Side side) {
    return apply(c -> c.setSide(side));
  }

  /**
   * Adds a new tab with the given title and content.
   *
   * @param title the tab title
   * @param content the tab content or other supported object, cannot be {@code null}
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code content} is {@code null}
   * @see TabPane#getTabs()
   * @see Tab#Tab(String, javafx.scene.Node)
   */
  public TabPaneBuilder add(String title, Object content) {
    Objects.requireNonNull(content, "content");

    return apply(c -> c.getTabs().add(new Tab(title, NodeBuilder.toNode(content))));
  }

  /**
   * Configures the builder with the given tabs.
   *
   * @param tabs the tabs to add, cannot be {@code null} but may be empty
   * @return the fluent builder, never {@code null}
   * @throws NullPointerException if {@code tabs} is {@code null}
   * @see TabPane#getTabs()
   */
  public TabPaneBuilder tabs(Tab... tabs) {
    Objects.requireNonNull(tabs, "tabs");

    return apply(node -> node.getTabs().addAll(Objects.requireNonNull(tabs, "tabs")));
  }
}
