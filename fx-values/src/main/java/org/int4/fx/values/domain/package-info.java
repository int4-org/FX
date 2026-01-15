/**
 * Domain abstractions defining valid value sets and optional UI-oriented views.
 * <p>
 * This package provides the {@link org.int4.fx.values.domain.Domain} API, which
 * models sets of permitted values independently of any concrete UI control or
 * storage mechanism. Domains are primarily used to constrain and validate
 * model values, but may also expose optional views that describe how values
 * can be enumerated, stepped, normalized, or mapped for user interface
 * construction.
 * <p>
 * Supported domain types include unrestricted, empty, enumerated, bounded,
 * stepped, continuous, and predicate-based domains. Not all domains are
 * suitable for all UI controls; view availability indicates which interaction
 * patterns a domain supports.
 * <p>
 * Domains are immutable and reusable, allowing them to be safely shared and
 * dynamically swapped on models as business rules change.
 */
package org.int4.fx.values.domain;
