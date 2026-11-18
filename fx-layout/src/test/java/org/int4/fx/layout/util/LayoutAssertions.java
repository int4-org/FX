package org.int4.fx.layout.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

import org.int4.fx.layout.internal.Layout;
import org.int4.fx.layout.internal.Layoutable;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utility methods for verifying JavaFX layout bounds using
 * an ASCII-art representation.
 * <p>
 * The ASCII representation describes expected node bounds using
 * boxed regions. Named boxes correspond to 1-based indices of
 * nodes passed to the assertion methods.
 */
public final class LayoutAssertions {
  private static final Comparator<Map.Entry<String, AsciiArtParser.Bounds>> COMPARATOR = Comparator.comparing(e -> e.getKey());

  private LayoutAssertions() {
    // utility class
  }

  /**
   * Asserts that the given nodes match the bounds defined in the ASCII
   * representation.
   * <p>
   * Any non-whitespace character other than letters, digits, {@code '.'}
   * or {@code ':'} is treated as a boundary. In practice this means
   * you can use characters like {@code -}, {@code |} and {@code +} to construct
   * the boundaries.
   * <p>
   * Each boxed region must contain a size ({@code width x height}),
   * optionally prefixed by a 1-based index followed by {@code ':'}.
   * Unnamed boxes are treated as spacers.
   * <p>
   * Margins can be indicated by a box which surrounds another box. A margin must
   * have 4 numbers corresponding to the top, left, right and bottom inset size.
   * <p>
   * An example visual representation:
   * <pre>
   * +-------------+-----+--------------------------+
   * |  1: 40x20   |     |  2: 100x20               |
   * +-------------+     +--------------------------+------------------+
   * |             |     |                      5                      |
   * |             |     |     +----------------------+----------+     |
   * |  3: 40x28   | 4x0 |  2  |   4: 100x20          | 5: 50x20 |  4  |
   * |             |     |     +----------------------+----------+     |
   * |             |     |                      3                      |
   * +-------------+     +--------------------------+------------------+
   * |  6: 40x20   |     |  7: 100x20               |
   * +-------------+-----+--------------------------+
   * </pre>
   * In the above representation, there are 7 named boxes representing node positions and
   * their sizes. There is one spacer of 4 pixels wide right of node 1. There is a margin
   * around nodes 4 and 5. Next to nodes 2 and 7 there is some empty space.
   *
   * @param expected ASCII-art representation of expected layout, cannot be {@code null}
   * @param nodes nodes referenced by index in the ASCII representation, cannot be {@code null}
   * @return the {@link AsciiArtParser} used for parsing
   * @throws NullPointerException if any argument is {@code null}
   */
  public static AsciiArtParser assertChildBounds(String expected, Node... nodes) {
    return assertChildBounds(Rotation.NONE, expected, nodes);
  }

  private static AsciiArtParser.Bounds rotateBounds(Rotation rotation, AsciiArtParser.Bounds bounds) {
    return new AsciiArtParser.Bounds(
      switch(rotation) {
        case NONE -> bounds.x();
        case R90_FLIP -> bounds.y();
      },
      switch(rotation) {
        case NONE -> bounds.y();
        case R90_FLIP -> bounds.x();
      },
      bounds.width(),
      bounds.height()
    );
  }

  private static String summarize(Rotation rotation, String expected, AsciiArtParser parser) {
    return "    Rotation: " + rotation + "\n"
      + expected.indent(4)
      + "\nParsed bounds of all areas:\n"
      + summarizeBounds(rotation, parser.getAllBounds());
  }

  private static String summarizeBounds(Rotation rotation, Map<String, AsciiArtParser.Bounds> allBounds) {
    return allBounds.entrySet().stream()
      .sorted(COMPARATOR)
      .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), rotateBounds(rotation, e.getValue())))
      .map(Object::toString)
      .collect(Collectors.joining("\n"));
  }

  public static AsciiArtParser assertChildBounds(Rotation rotation, String expected, Node... nodes) {
    Objects.requireNonNull(expected, "expected");
    Objects.requireNonNull(nodes, "nodes");

    AsciiArtParser parser = new AsciiArtParser(expected.lines().toList(), rotation);
    List<String> failures = new ArrayList<>();

    for (int i = 0; i < nodes.length; i++) {
      Node node = Objects.requireNonNull(nodes[i], "nodes[" + i + "]");
      int index = i + 1;

      AsciiArtParser.Bounds bounds = parser.getBounds(String.valueOf(index));

      if(bounds == null) {
        failures.add("No bounds were given for child " + index);
      }
      else {
        verify("child " + index, bounds, toSceneBounds(node), failures);
      }
    }

    if(!failures.isEmpty()) {
      fail(failures.stream().collect(Collectors.joining("\n- ", "\n- ", " in:\n\n" + summarize(rotation, expected, parser))));
    }

    return parser;
  }

  /**
   * Asserts that a container and its leaf descendants match the ASCII
   * representation, recursing through any nested real {@code Node} tree
   * (e.g. a {@code Flex}, {@code HBox} or {@code VBox}) down to the true
   * leaves -- only leaves need a label in the ASCII art.
   * <p>
   * Every intermediate container encountered along the way (anything with
   * its own managed children) is verified too, automatically: its expected
   * bounds are derived as the union of its children's (already resolved)
   * bounds, and checked against its actual bounds -- without needing a label
   * of its own. If the container has its own padding, AsciiArtParser names
   * the margin drawn around its children (e.g. {@code "M[1,2]"}, derived
   * from whatever is directly nested inside it) and that is looked up and
   * used instead, so a padded container is verified correctly rather than
   * reported as a mismatch.
   * <p>
   * Any non-whitespace character other than letters, digits, {@code '.'}
   * or {@code ':'} is treated as a boundary. In practice this means
   * you can use characters like {@code -}, {@code |} and {@code +} to construct
   * the boundaries.
   * <p>
   * Each boxed region must contain a size ({@code width x height}),
   * optionally prefixed by a 1-based index followed by {@code ':'}.
   * Unnamed boxes are treated as spacers.
   * <p>
   * Margins can be indicated by a box which surrounds another box. A margin must
   * have 4 numbers corresponding to the top, left, right and bottom inset size.
   * <p>
   * An example visual representation:
   * <pre>
   * +-------------+-----+--------------------------+
   * |  1: 40x20   |     |  2: 100x20               |
   * +-------------+     +--------------------------+------------------+
   * |             |     |                      5                      |
   * |             |     |     +----------------------+----------+     |
   * |  3: 40x28   | 4x0 |  2  |   4: 100x20          | 5: 50x20 |  4  |
   * |             |     |     +----------------------+----------+     |
   * |             |     |                      3                      |
   * +-------------+     +--------------------------+------------------+
   * |  6: 40x20   |     |  7: 100x20               |
   * +-------------+-----+--------------------------+
   * </pre>
   * In the above representation, there are 7 named boxes representing node positions and
   * their sizes. There is one spacer of 4 pixels wide right of node 1. There is a margin
   * around nodes 4 and 5. Next to nodes 2 and 7 there is some empty space.
   *
   * @param expected  ASCII-art representation of expected layout, must not be {@code null}
   * @param container container whose leaf descendants (and every intermediate container) will be verified, must not be {@code null}
   * @return the {@link AsciiArtParser} used for parsing
   * @throws NullPointerException if any argument is {@code null}
   */
  public static AsciiArtParser assertBounds(String expected, Region container) {
    return assertBounds(Rotation.NONE, expected, container);
  }

  public static AsciiArtParser assertBounds(Rotation rotation, String expected, Region container) {
    Objects.requireNonNull(container, "container");

    AsciiArtParser parser = new AsciiArtParser(expected.lines().toList(), rotation);
    List<String> failures = new ArrayList<>();
    int[] leafIndex = {0};

    for(Node child : childrenOf(container)) {
      resolveAndVerify(child, parser, leafIndex, failures, LayoutAssertions::childrenOf, LayoutAssertions::toSceneBounds);
    }

    double expectedWidth = switch(Rotation.NONE) {
      case NONE -> parser.getBounds().width();
      case R90_FLIP -> parser.getBounds().height();
    };

    double expectedHeight = switch(Rotation.NONE) {
      case NONE -> parser.getBounds().height();
      case R90_FLIP -> parser.getBounds().width();
    };

    if(expectedWidth != container.getWidth() || expectedHeight != container.getHeight()) {
      failures.add(
        "Expected container size ("
        + expectedWidth + "x" + expectedHeight
        + ") but was: ("
        + container.getWidth() + "x" + container.getHeight()
        + ")"
      );
    }

    if(!failures.isEmpty()) {
      fail(failures.stream().collect(Collectors.joining("\n- ", "\n- ", " in:\n\n" + summarize(rotation, expected, parser))));
    }

    return parser;
  }

  /**
   * Asserts that a container and its leaf descendants match the ASCII
   * representation, recursing through any nested {@link Layout} (e.g. a
   * wrapper or crossed {@code FlexLayout}) or nested real {@code Node} tree
   * (e.g. a {@code Flex}) down to the true leaves -- only leaves need a
   * label in the ASCII art.
   * <p>
   * Every intermediate container encountered along the way (anything with
   * its own children) is verified too, automatically: its expected bounds
   * are derived as the union of its children's (already resolved) bounds,
   * and checked against its actual bounds -- without needing a label of its
   * own. This assumes the container has no padding/insets of its own beyond
   * its children; a padded container would correctly be reported as a
   * mismatch rather than silently passing.
   * <p>
   * Any non-whitespace character other than letters, digits, {@code '.'}
   * or {@code ':'} is treated as a boundary. In practice this means
   * you can use characters like {@code -}, {@code |} and {@code +} to construct
   * the boundaries.
   * <p>
   * Each boxed region must contain a size ({@code width x height}),
   * optionally prefixed by a 1-based index followed by {@code ':'}.
   * Unnamed boxes are treated as spacers.
   * <p>
   * An example visual representation:
   * <pre>
   * +------------+--------------+
   * | 1: 40x60   | 2: 100x20    |
   * |            +--------------+
   * |            | 3: 100x16    |
   * +------------+--------------+
   * </pre>
   * In the above representation, there are 3 named leaf boxes. If node 2's
   * and node 3's common parent has no padding of its own, its bounds
   * (100x36, spanning both) are verified automatically as the union of
   * nodes 2 and 3 -- there is no separate label for it.
   *
   * @param expected  ASCII-art representation of expected layout, must not be {@code null}
   * @param container container whose leaf descendants (and every intermediate container) will be verified, must not be {@code null}
   * @return the {@link AsciiArtParser} used for parsing
   * @throws NullPointerException if any argument is {@code null}
   */
  public static AsciiArtParser assertBounds(String expected, Layout container) {
    Objects.requireNonNull(container, "container");

    AsciiArtParser parser = new AsciiArtParser(expected.lines().toList());
    List<String> failures = new ArrayList<>();
    int[] leafIndex = {0};

    for(Layoutable child : container.getChildrenUnmodifiable()) {
      resolveAndVerify(child, parser, leafIndex, failures, LayoutAssertions::childrenOf, LayoutAssertions::actualBoundsOf);
    }

    double expectedWidth = parser.getBounds().width();
    double expectedHeight = parser.getBounds().height();

    if(expectedWidth != container.getLayoutBounds().getWidth() || expectedHeight != container.getLayoutBounds().getHeight()) {
      failures.add(
        "Expected container size ("
        + expectedWidth + "x" + expectedHeight
        + ") but was: ("
        + container.getLayoutBounds().getWidth() + "x" + container.getLayoutBounds().getHeight()
        + ")"
      );
    }

    if(!failures.isEmpty()) {
      fail(failures.stream().collect(Collectors.joining("\n- ", "\n- ", " in:\n\n" + summarize(Rotation.NONE, expected, parser))));
    }

    return parser;
  }

  public static AsciiArtParser assertChildBounds(String expected, Layoutable... nodes) {
    Objects.requireNonNull(expected, "expected");
    Objects.requireNonNull(nodes, "nodes");

    AsciiArtParser parser = new AsciiArtParser(expected.lines().toList());
    List<String> failures = new ArrayList<>();

    for (int i = 0; i < nodes.length; i++) {
      Layoutable node = Objects.requireNonNull(nodes[i], "nodes[" + i + "]");
      int index = i + 1;

      AsciiArtParser.Bounds expectedBounds = parser.getBounds(String.valueOf(index));

      if(expectedBounds == null) {
        failures.add("No bounds were given for child " + index);
      }
      else {
        verify("child " + index, expectedBounds, actualBoundsOf(node), failures);
      }
    }

    if(!failures.isEmpty()) {
      fail(failures.stream().collect(Collectors.joining("\n- ", "\n- ", " in:\n\n" + summarize(Rotation.NONE, expected, parser))));
    }

    return parser;
  }

  /*
   * Finds a Layoutable's own children -- descending into Layout.
   * getManagedChildren() (a nested FlexLayout isn't a Node, so this is the
   * only way to descend into it), or, once a boundary bottoms out at a real
   * Node (directly, or wrapped in a NodeLayoutable), into a real Parent's
   * (e.g. a Flex's) own scene-graph children instead. Returns an empty list
   * for an actual leaf.
   */
  private static List<Layoutable> childrenOf(Layoutable layoutable) {
    if(layoutable instanceof Layout layout && !layout.getChildrenUnmodifiable().isEmpty()) {
      return layout.getChildrenUnmodifiable();
    }

    Node node = layoutable.unwrap(Node.class);

    if(node instanceof Parent parent && !parent.getChildrenUnmodifiable().isEmpty()) {
      return parent.getChildrenUnmodifiable().stream()
        .filter(Node::isManaged)
        .map(Layoutable::of)
        .toList();
    }

    return List.of();
  }

  /*
   * As childrenOf(Layoutable), but for a real Node/Parent tree (e.g. Flex or
   * any other Region). Returns an empty list for an actual leaf.
   */
  private static List<Node> childrenOf(Node node) {
    if(node instanceof Parent parent && !parent.getChildrenUnmodifiable().isEmpty()) {
      return parent.getChildrenUnmodifiable().stream()
        .filter(Node::isManaged)
        .toList();
    }

    return List.of();
  }

  /*
   * A node's expected bounds, plus the ASCII art key(s) that refer to it --
   * a single key when the node corresponds to its own distinct entry in the
   * picture (a leaf's index, or a margin's auto-derived "M[...]" name), or
   * several keys spliced in when the node has no distinct entry of its own
   * (an unpadded container, invisible in the picture, whose children appear
   * directly nested in its parent's place).
   */
  private record Resolved(AsciiArtParser.Bounds bounds, List<String> keys) {}

  /*
   * Resolves a node's expected bounds and verifies them against its actual
   * bounds, recursively -- generic over T so the same walk serves both a
   * Layoutable/Layout tree and a real Node/Parent tree, differing only in
   * how to find a node's children and its actual bounds. A leaf gets its
   * expected bounds straight from the ASCII art (consuming the next 1-based
   * index); an intermediate container (anything childrenOf yields children
   * for) builds a key from its (recursively resolved) children's own keys
   * (e.g. "1,2") and checks the ASCII art for two things registered under
   * that key: an explicit margin (drawn as "M[1,2]", AsciiArtParser derives
   * that name from whatever is directly nested inside it) taking priority
   * as the container's whole bounds if present, otherwise an explicitly
   * owned dead-space area (drawn as "[1,2]: H50" or similar -- a plain named
   * box whose name happens to be this exact bracketed key) absorbed into
   * whichever single side of the plain union it touches. Absent both (the
   * common case), its expected bounds are just the plain union of its
   * children -- so container Y's own placement is verified automatically
   * from whatever's already known about the children it contains, whether
   * padded, extended by owned dead space, or neither.
   */
  private static <T> Resolved resolveAndVerify(T node, AsciiArtParser parser, int[] leafIndex, List<String> failures, Function<T, List<T>> childrenOf, Function<T, Bounds> actualBoundsOf) {
    List<T> children = childrenOf.apply(node);

    if(!children.isEmpty()) {
      List<Resolved> resolvedChildren = new ArrayList<>(children.size());

      for(T child : children) {
        resolvedChildren.add(resolveAndVerify(child, parser, leafIndex, failures, childrenOf, actualBoundsOf));
      }

      List<String> childKeys = resolvedChildren.stream().flatMap(r -> r.keys().stream()).toList();
      String marginKey = "M[" + String.join(",", childKeys) + "]";
      AsciiArtParser.Bounds explicit = parser.getBounds(marginKey);

      if(explicit != null) {
        verify("container " + marginKey, explicit, actualBoundsOf.apply(node), failures);

        return new Resolved(explicit, List.of(marginKey));
      }

      AsciiArtParser.Bounds plainUnion = union(resolvedChildren.stream().map(Resolved::bounds).toList());
      String ownerKey = "[" + String.join(",", childKeys) + "]";
      AsciiArtParser.Bounds deadSpace = parser.getBounds(ownerKey);
      AsciiArtParser.Bounds expected = deadSpace == null ? plainUnion : absorb(plainUnion, deadSpace);

      verify("container " + ownerKey, expected, actualBoundsOf.apply(node), failures);

      return new Resolved(expected, childKeys);
    }

    int index = ++leafIndex[0];
    String key = String.valueOf(index);
    AsciiArtParser.Bounds expected = parser.getBounds(key);

    if(expected == null) {
      failures.add("No bounds were given for child " + index);

      return new Resolved(new AsciiArtParser.Bounds(0, 0, 0, 0), List.of(key));
    }

    verify("child " + index, expected, actualBoundsOf.apply(node), failures);

    return new Resolved(expected, List.of(key));
  }

  private static AsciiArtParser.Bounds union(List<AsciiArtParser.Bounds> bounds) {
    double minX = bounds.stream().mapToDouble(AsciiArtParser.Bounds::x).min().orElseThrow();
    double minY = bounds.stream().mapToDouble(AsciiArtParser.Bounds::y).min().orElseThrow();
    double maxX = bounds.stream().mapToDouble(b -> b.x() + b.width()).max().orElseThrow();
    double maxY = bounds.stream().mapToDouble(b -> b.y() + b.height()).max().orElseThrow();

    return new AsciiArtParser.Bounds(minX, minY, maxX - minX, maxY - minY);
  }

  /*
   * Grows a container's plain union to absorb an explicitly-owned dead-space
   * area (see resolveAndVerify's "[1,2]"-style owner key lookup) on whichever
   * single side it touches. Ownership is stated by the ASCII art itself (via
   * the owner key), not inferred geometrically -- a nested container whose
   * edge coincidentally lines up with its parent's is never ambiguous here,
   * since only the container whose exact key was written as the dead-space's
   * name absorbs it.
   */
  private static AsciiArtParser.Bounds absorb(AsciiArtParser.Bounds union, AsciiArtParser.Bounds deadSpace) {
    double left = union.x();
    double top = union.y();
    double right = union.x() + union.width();
    double bottom = union.y() + union.height();

    if(deadSpace.x() == right) {
      right = deadSpace.x() + deadSpace.width();
    }
    else if(deadSpace.x() + deadSpace.width() == left) {
      left = deadSpace.x();
    }
    else if(deadSpace.y() == bottom) {
      bottom = deadSpace.y() + deadSpace.height();
    }
    else if(deadSpace.y() + deadSpace.height() == top) {
      top = deadSpace.y();
    }

    return new AsciiArtParser.Bounds(left, top, right - left, bottom - top);
  }

  /*
   * A FlexLayout's own getLayoutBounds() already reports its absolute
   * position (its x/y fields are set directly by resizeRelocate/
   * layoutCoordinated), so it can be used as-is. A real Node's
   * getLayoutBounds() is different -- it is local content bounds starting at
   * the node's own origin, never including the layoutX/layoutY translation
   * that actually places it within its parent -- so that translation has to
   * be added back in separately, using the real Node API directly (removed
   * from Layoutable itself, but still present on Node).
   */
  private static Bounds actualBoundsOf(Layoutable node) {
    Node n = node.unwrap(Node.class);

    if(n == null) {
      return node.getLayoutBounds();
    }

    Bounds localBounds = n.getLayoutBounds();

    return new BoundingBox(
      n.getLayoutX() + localBounds.getMinX(), n.getLayoutY() + localBounds.getMinY(), 0,
      localBounds.getWidth(), localBounds.getHeight(), 0
    );
  }

  private static void verify(String label, AsciiArtParser.Bounds expected, Bounds actual, List<String> failures) {
    if(expected.x() != actual.getMinX() || expected.y() != actual.getMinY()) {
      failures.add(
        "Expected " + label + " to have location ("
        + expected.x() + ", " + expected.y()
        + ") but was: ("
        + actual.getMinX() + ", " + actual.getMinY()
        + ")"
      );
    }
    else if(expected.width() != actual.getWidth() || expected.height() != actual.getHeight()) {
      failures.add(
        "Expected " + label + " to have size ("
        + expected.width() + ", " + expected.height()
        + ") but was: ("
        + actual.getWidth() + ", " + actual.getHeight()
        + ")"
      );
    }
  }

  /**
   * Converts a node's local bounds to scene (root) coordinates.
   */
  private static Bounds toSceneBounds(Node node) {
    Bounds bounds = node.getBoundsInLocal();
    Node current = node;

    while (current.getParent() != null) {
      bounds = current.localToParent(bounds);
      current = current.getParent();
    }

    return bounds;
  }
}