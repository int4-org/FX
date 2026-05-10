package org.int4.fx.values.domain;

abstract class AbstractIndexedView<T> implements IndexedView<T> {
  private static final int MAX_ITEMS_TO_DISPLAY = 10;  // must be even

  @Override
  public String toString() {
    StringBuilder items = new StringBuilder();
    long max = size() <= MAX_ITEMS_TO_DISPLAY ? size() : Math.min(MAX_ITEMS_TO_DISPLAY / 2, size());

    for(long i = 0; i < max; i++) {
      if(i > 0) {
        items.append(", ");
      }

      items.append(get(i));
    }

    if(size() > MAX_ITEMS_TO_DISPLAY) {
      items.append(" ... ");

      for(long i = size() - MAX_ITEMS_TO_DISPLAY / 2; i < size(); i++) {
        if(i > size() - MAX_ITEMS_TO_DISPLAY / 2) {
          items.append(", ");
        }

        items.append(get(i));
      }
    }

    return "IndexedView[" + items + "]";
  }
}
