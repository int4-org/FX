package org.int4.fx.builders.internal;

import java.util.Arrays;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import org.int4.fx.builders.common.NodeBuilder;

/**
 * Internal utility methods for converting arbitrary builder inputs into
 * JavaFX {@link Node} instances.
 * <p>
 * This class supports the flexible child semantics used by the builder API,
 * where builders may accept heterogeneous objects and normalize them into
 * nodes at build time.
 */
public class Builders {

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
  public static Node toNode(Object node) {
    return switch(node) {
      case Node n -> n;
      case NodeBuilder b -> b.build();
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
  public static Node[] toNodes(Object... nodes) {
    Objects.requireNonNull(nodes, "nodes");

    return Arrays.stream(nodes).filter(Objects::nonNull).map(Builders::toNode).toArray(Node[]::new);
  }
}
