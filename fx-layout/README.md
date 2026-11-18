# Flex Layout

`Flex` is a JavaFX layout container from the `fx-layout` module. It arranges its children in a single row or column and, unlike `HBox` and `VBox`, its orientation can change at runtime. It also supports an aligned mode in which nested Flex rows and columns coordinate their cells like a grid.

## Dependency

```xml
<dependency>
  <groupId>org.int4.fx</groupId>
  <artifactId>fx-layout</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Module name: `org.int4.fx.layout`.

## Creating a Flex

```java
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.int4.fx.layout.flex.Flex;

Flex form = Flex.vflex(
    Flex.hflex(new Label("Name"), new TextField()),
    Flex.hflex(new Label("Email"), new TextField())
);
```

`Flex.hflex(...)` creates a horizontal Flex and `Flex.vflex(...)` creates a vertical one. The no-argument constructor defaults to horizontal; `new Flex(Orientation.VERTICAL, ...)` constructs a vertical Flex directly.

## Fluent builder

Flex builders integrate with the rest of the FXFlow builder API:

```java
import static org.int4.fx.layout.flex.Flexes.FLEXES;

Flex form = FLEXES.vflex("form")
    .aligned()
    .spacing(8)
    .fill(false)
    .nodes(
        FLEXES.hflex().nodes(new Label("Name"), new TextField()),
        FLEXES.hflex().nodes(new Label("Email"), new TextField())
    )
    .build();
```

`Flexes.FLEXES` exposes `flex(...)`, `hflex(...)`, and `vflex(...)`. A builder can also be obtained directly with `Flex.of(...)` or `new FlexBuilder(...)`. `FlexBuilder` extends the standard region builder, so styling, visibility, and size configuration work as with other builders.

## Changing orientation

```java
Flex row = Flex.hflex(new Label("Name"), new TextField());

row.setOrientation(Orientation.VERTICAL);
row.setOrientation(Orientation.HORIZONTAL);
```

A `null` orientation is treated as `Orientation.HORIZONTAL`. The current orientation is reflected in the `:horizontal` and `:vertical` pseudo-classes.

## Sizing, spacing, and alignment

```java
form.setSpacing(8);
form.setAlignment(Pos.CENTER_LEFT);
form.setFill(false);
```

- `spacing` is the gap between children along the orientation.
- `alignment` positions the content within the Flex.
- `fill` controls whether children are resized across the orientation to fill the Flex. It defaults to `true`.

A child can grow to absorb extra space along the orientation:

```java
Flex.setGrow(emailField, Priority.ALWAYS);
```

Only `Priority.ALWAYS` has an effect; other values behave like `Priority.NEVER`.

The minimum and preferred sizes along the orientation are the sum of the children's sizes plus spacing and insets. Across the orientation they are the largest child size plus insets. The maximum size is unbounded by default.

## Aligned mode

Setting `aligned` makes separate Flex rows share a common grid: labels line up with labels and inputs with inputs.

```java
Flex row1 = Flex.hflex(new Label("Name"), new TextField());
Flex row2 = Flex.hflex(new Label("Email address"), new TextField());

Flex form = Flex.vflex(row1, row2);

form.setAligned(true);
```

Without aligned mode, each row sizes its cells independently:

| | Without `aligned` | With `aligned` |
|---|---|---|
| Row 1 | `Name` is as wide as "Name" | `Name` uses the shared label column |
| Row 2 | `Email address` is as wide as "Email address" | `Email address` uses the same shared column |
| Inputs | Start at different offsets | Start at the same offset |

With aligned mode, the rows behave like a grid:

| | Label column | Input column |
|---|---|---|
| Row 1 | Name | Text field |
| Row 2 | Email address | Text field |

If a row needs a wrapper for a background or border, a nested Flex with the same orientation as the root is flattened into the surrounding rows, so it does not disturb the grid.

## CSS

`Flex` exposes the following styleable properties:

- `-fx-spacing`
- `-fx-alignment`
- `-fx-fill`
- `-fx-aligned`

The `:horizontal` and `:vertical` pseudo-classes reflect the current orientation:

```css
.form {
  -fx-spacing: 0.5em;
}

.form:horizontal {
  -fx-alignment: center-left;
}
```

## API overview

- `Flex.of(String...)`, `Flex.of(Orientation, String...)`
- `Flex.hflex(Node...)`, `Flex.vflex(Node...)`
- `Flex.getGrow(Node)`, `Flex.setGrow(Node, Priority)`
- `getOrientation()`, `setOrientation(Orientation)`
- `isAligned()`, `setAligned(boolean)`, `alignedProperty()`
- `isFill()`, `setFill(boolean)`, `fillProperty()`
- `getSpacing()`, `setSpacing(double)`, `spacingProperty()`
- `getAlignment()`, `setAlignment(Pos)`, `alignmentProperty()`
