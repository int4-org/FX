package org.int4.fx.builders.common;

import java.util.Arrays;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import org.int4.fx.builders.FX;

/**
 * Builder capable of creating a {@link Node}.
 * <p>
 * This interface marks builders that can produce a {@link Node} and allows
 * them to be accepted transparently in places where builders support arbitrary
 * child objects.
 * <p>
 * In such contexts, callers do not need to invoke {@link #build()} explicitly;
 * the builder will be completed automatically when a {@link Node} is required.
 * This enables fluent composition of builders without manual materialization.
 *
 * @param <N> the type of node being build
 */
public interface NodeBuilder<N extends Node> {

  /**
   * Converts the given object into a {@link Node}.
   * <p>
   * Supported conversions:
   * <ul>
   *   <li>{@link Node}: returned as-is</li>
   *   <li>{@link NodeBuilder}: {@link NodeBuilder#build()} is invoked</li>
   *   <li>{@link String}: converted to a {@link Label} with the string as text</li>
   * </ul>
   * <p>
   * Any unsupported object type results in a placeholder {@link Button}
   * indicating an invalid child element.
   *
   * @param node the object to convert, must not be {@code null}
   * @return a {@link Node} representing the given object, never {@code null}
   * @throws IllegalArgumentException if {@code node} is {@code null}
   */
  static Node toNode(Object node) {
    return switch(node) {
      case Node n -> n;
      case NodeBuilder<?> b -> b.build();
      case String s -> new Label(s);
      case null -> throw new IllegalArgumentException("node cannot be null");
      // TODO could also allow Observables to be translated to bound labels :)
      default -> {
        Button b = new Button("Unknown Element");

        b.setTooltip(new Tooltip("Unsupported child element:\n" + node));

        yield b;
      }
    };
  }

  /**
   * Converts the given objects into an array of {@link Node}s.
   * <p>
   * Any {@code null} elements are ignored.
   *
   * @param nodes objects to convert with {@code null} elements skipped; the array cannot be {@code null}
   * @return an array of converted nodes, never {@code null}
   * @throws NullPointerException if the nodes array is {@code null}
   */
  static Node[] toNodes(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return Arrays.stream(nodes).filter(Objects::nonNull).map(NodeBuilder::toNode).toArray(Node[]::new);
  }

  /**
   * Completes the builder and creates a new {@link Node}.
   *
   * @return a new {@link Node}, never {@code null}
   */
  N build();
}
