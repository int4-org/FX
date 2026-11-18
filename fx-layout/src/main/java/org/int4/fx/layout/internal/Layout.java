package org.int4.fx.layout.internal;

import java.util.List;

// TODO do we really need to define what a Layout must support?  --> used for LayoutAssertions to get at children
public interface Layout extends Layoutable {
  List<Layoutable> getChildrenUnmodifiable();
}
