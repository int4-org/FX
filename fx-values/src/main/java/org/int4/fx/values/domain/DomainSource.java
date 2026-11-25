package org.int4.fx.values.domain;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.util.Subscription;

/**
 * A source of {@link Domain} instances that publishes changes to its domain.
 * <p>
 * A {@code Domain} is an immutable snapshot of a set of allowed values; a
 * {@code DomainSource} is the handle that produces such snapshots and announces
 * when a new one becomes available. The current snapshot is returned by
 * {@link #get()}, and every change is published as a {@link DomainTransition}
 * to subscribers registered with {@link #transitions(Consumer)}.
 * <p>
 * A transition both describes the new domain and how it was produced, through
 * its {@link DomainTransition#edit() edit}:
 * <ul>
 *   <li>{@link Edit.Indexed} carries the individual list changes, allowing
 *       consumers that mirror the items to apply them incrementally instead of
 *       rebuilding their state.</li>
 *   <li>{@link Edit.Replaced} indicates that no incremental information is
 *       available, so consumers should use the successor domain directly.</li>
 * </ul>
 * Implementations choose the edit they publish; an editable source typically
 * publishes indexed edits, while a source whose domain is replaced wholesale
 * publishes {@link Edit.Replaced}.
 * <p>
 * <b>Invariants</b>
 * <ul>
 *   <li>{@link #get()} never returns {@code null}.</li>
 *   <li>A transition always represents a change: the successor is never the same
 *       instance as the previous domain, and implementations do not publish
 *       transitions for changes that leave the domain instance unchanged.</li>
 *   <li>{@link #transitions(Consumer)} does not replay the current domain; it
 *       only delivers transitions caused by later changes. Consumers that need
 *       the current state should read {@link #get()} as well.</li>
 *   <li>Transitions are delivered synchronously, before the operation that
 *       caused them returns.</li>
 *   <li>Subscribing or unsubscribing during a notification does not affect the
 *       notification already in progress.</li>
 * </ul>
 * <p>
 * Sources are meant for single-threaded use, typically the JavaFX application
 * thread. Sharing a source between threads requires external synchronization.
 * <p>
 * See {@link DomainSources} for fixed and replaceable implementations, and
 * {@link IndexedEditor} for a source that edits its items.
 *
 * @param <T> the value type governed by the published domains
 */
public interface DomainSource<T> {

  /**
   * Returns the current domain.
   *
   * @return the current domain, never {@code null}
   */
  Domain<T> get();

  /**
   * Subscribes to changes of this source's domain.
   * <p>
   * The consumer is not called for the current domain; it receives only the
   * transitions caused by changes made after subscribing.
   *
   * @param consumer a consumer for transitions, cannot be {@code null}
   * @return a subscription that can be used to cancel the subscription, never {@code null}
   * @throws NullPointerException if {@code consumer} is {@code null}
   */
  Subscription transitions(Consumer<DomainTransition<T>> consumer);

  /**
   * Describes a change of a domain source.
   * <p>
   * A transition always represents a change: {@code previous} is either
   * {@code null} (an initial transition) or a different instance than
   * {@code successor}. An initial transition always has an
   * {@link Edit.Replaced} edit, as there is no prior domain state that indexed
   * changes could refer to. For other transitions, the edit describes how the
   * successor was produced; its indices, if any, are relative to the domain
   * state immediately before the edit.
   *
   * @param <T> the value type
   * @param previous the previous domain, can be {@code null} for an initial transition
   * @param successor the successor domain, cannot be {@code null} and must differ from {@code previous}
   * @param edit the edit that produced the successor, cannot be {@code null} and must be
   *   {@link Edit.Replaced} when {@code previous} is {@code null}
   */
  record DomainTransition<T>(
    Domain<T> previous,
    Domain<T> successor,
    Edit<T> edit
  ) {

    /**
     * Constructs a new instance.
     *
     * @throws NullPointerException if {@code successor} or {@code edit} is {@code null}
     * @throws IllegalArgumentException if {@code previous} and {@code successor} are the same
     *   instance, or if {@code previous} is {@code null} and the edit is not
     *   {@link Edit.Replaced}
     */
    public DomainTransition {
      Objects.requireNonNull(successor, "successor");
      Objects.requireNonNull(edit, "edit");

      if(previous == successor) {
        throw new IllegalArgumentException("previous and successor must differ");
      }
      if(previous == null && !(edit instanceof Edit.Replaced<T>)) {
        throw new IllegalArgumentException("an initial transition must have a replaced edit");
      }
    }
  }
}
