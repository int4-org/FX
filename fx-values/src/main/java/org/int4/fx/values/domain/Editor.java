package org.int4.fx.values.domain;

/**
 * An editor for a domain.
 * <p>
 * An editor edits the domain that a {@link DomainSource} provides: each edit
 * advances the domain and publishes the change as a transition. Type-specific
 * interfaces such as {@link IndexedEditor} define the actual edit operations.
 * Editors are obtained through {@link Domain#requireEditor(Class)}.
 *
 * @param <T> the value type governed by the edited domain
 */
public interface Editor<T> {
}
