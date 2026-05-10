package org.int4.fx.builders.explorer;

/**
 * Marks a model that exposes actions and assertions, making it explorable
 * for exhaustive state-based testing.
 */
public interface Explorable {

  /**
   * Returns an immutable object representing the current state of this explorable model.
   * The returned object must implement {@code equals} and {@code hashCode} to allow
   * correct comparison of states during exploration.
   *
   * @return a snapshot of the current state, never {@code null}
   */
  Object snapshot();
}