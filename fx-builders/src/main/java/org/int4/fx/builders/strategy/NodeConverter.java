package org.int4.fx.builders.strategy;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import org.int4.fx.builders.context.BuildContexts;

class NodeConverter {
  static Node toNode(Object content) {
    return switch(content) {
      case Node n -> n;
      case String s -> {
        Label label = new Label();

        BuildContexts.activeContext().resolve(TextStrategy::base).apply(label, s, label::setText);

        yield label;
      }
      default -> {
        Button b = new Button("Unknown Element");

        b.setTooltip(new Tooltip("Unsupported child element:\n" + content));

        yield b;
      }
    };
  }
}
