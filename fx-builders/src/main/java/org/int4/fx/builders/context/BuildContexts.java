package org.int4.fx.builders.context;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provides access to and management of build contexts with well-defined scoping.
 * <p>
 * It maintains:
 * <ul>
 *   <li>a process-wide root context,</li>
 *   <li>a thread-local active context derived from the root, and</li>
 *   <li>support for temporarily overriding the active context for a specific scope.</li>
 * </ul>
 * <p>
 * This allows build-time behavior to be configured globally, per thread,
 * or for a specific execution block.
 */
public class BuildContexts {
  private static final ThreadLocal<BuildContext> ACTIVE_CONTEXT = ThreadLocal.withInitial(() -> BuildContexts.root());

  private static volatile BuildContext root = BuildContext.EMPTY;

  /**
   * Returns the process-wide root build context.
   * <p>
   * The root context acts as the baseline from which thread-local active
   * contexts are initialized. Changing the root context does not affect
   * already-initialized thread-local contexts.
   *
   * @return the current root build context, never {@code null}
   */
  public static BuildContext root() {
    return root;
  }

  /**
   * Sets the process-wide root build context.
   * <p>
   * The provided context becomes the baseline for newly initialized
   * thread-local active contexts.
   * <p>
   * This method is typically called during application initialization,
   * before any builders are used, if the root context is intended to act
   * as a global default.
   *
   * @param root the new root build context, cannot be {@code null}
   * @throws NullPointerException if {@code newDefault} is {@code null}
   */
  public static void root(BuildContext root) {
    BuildContexts.root = Objects.requireNonNull(root, "root");
  }

  /**
   * Returns the currently active build context for the calling thread.
   * <p>
   * If no context has been explicitly set for the thread, the root context
   * is returned.
   *
   * @return the active build context for the current thread, never {@code null}
   */
  public static BuildContext activeContext() {
    return ACTIVE_CONTEXT.get();
  }

  /**
   * Executes the given supplier with the specified build context temporarily
   * set as the active context for the current thread.
   * <p>
   * The previous active context is restored after the supplier completes,
   * regardless of whether it completes normally or throws an exception.
   *
   * @param <T> the return type of the supplier
   * @param context the build context to activate for the duration of the call,
   *   cannot be {@code null}
   * @param supplier the code to execute with the given active context,
   *   cannot be {@code null}
   * @return the value returned by the supplier
   * @throws NullPointerException if {@code context} or {@code supplier} is {@code null}
   */
  public static <T> T with(BuildContext context, Supplier<T> supplier) {
    Objects.requireNonNull(context, "context");
    Objects.requireNonNull(supplier, "supplier");

    BuildContext previousContext = ACTIVE_CONTEXT.get();

    try {
      ACTIVE_CONTEXT.set(context);

      return supplier.get();
    }
    finally {
      ACTIVE_CONTEXT.set(previousContext);
    }
  }
}
