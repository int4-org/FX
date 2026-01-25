/**
 * Domain-constrained, observable value models for JavaFX applications.
 * <p>
 * This package defines modifiable value models that extend JavaFX's
 * {@link javafx.beans.value.ObservableValue ObservableValue} abstraction
 * with explicit domain-based validation, applicability, and null-handling
 * semantics. Models act as stable value sources for UI controls and business
 * logic.
 * <p>
 * Each model is associated with a {@link org.int4.fx.values.domain.Domain}
 * describing the set of permitted values. Domains may be exchanged at runtime
 * to reflect changing business rules; a model becomes non-applicable when its
 * domain is empty, allowing UI controls to automatically disable themselves
 * and suppress validation feedback.
 * <p>
 * Models expose multiple access patterns for their value:
 * <ul>
 *   <li>Unconditional access to the stored value via {@code getRawValue()}.</li>
 *   <li>Null-safe access via {@code getValue()} for UI binding.</li>
 *   <li>Strict access via {@code get()} for business logic, with explicit
 *       exceptions for invalid or non-applicable state.</li>
 * </ul>
 * <p>
 * The package includes models for primitive and reference types such as
 * boolean, numeric, string, object, and choice-based values, and is designed
 * to be extensible with additional model variants.
 */
package org.int4.fx.values.model;
