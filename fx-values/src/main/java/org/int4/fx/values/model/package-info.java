/**
 * Domain-constrained, reactive value models for JavaFX applications.
 * <p>
 * This package defines a hierarchical set of model interfaces that extend
 * JavaFX's {@link javafx.beans.value.ObservableValue ObservableValue}
 * abstraction with explicit domain-based validation, applicability, and
 * reactive state management.
 * <p>
 * The model hierarchy consists of three primary tiers:
 * <ul>
 *   <li>{@link org.int4.fx.values.model.ObservableModel ObservableModel} –
 *       A read-only semantic projection of a model's state.</li>
 *   <li>{@link org.int4.fx.values.model.WritableModel WritableModel} –
 *       Extends {@code ObservableModel} with validation state, mutation
 *       operations, and a read-only view of its domain.</li>
 *   <li>{@link org.int4.fx.values.model.Model Model} –
 *       A model where both value and domain are writable.</li>
 * </ul>
 * <p>
 * Each model is associated with a {@link org.int4.fx.values.domain.Domain}
 * describing the set of permitted values. Models distinguish between values 
 * that are temporarily irrelevant (non-applicable) and values that may be 
 * transformed while preserving applicability semantics.
 * <p>
 * Models expose multiple access patterns:
 * <ul>
 *   <li>Reactive state access via {@code rawValue()}, providing a
 *       {@link org.int4.fx.core.util.Value Value}
 *       that encapsulates data and state atomically.</li>
 *   <li>Semantic access via {@code getValue()}, where applicability
 *       determines whether a value is exposed.</li>
 *   <li>Strict access via {@code get()} for business logic, with explicit
 *       exceptions for invalid or non-applicable state.</li>
 * </ul>
 * <p>
 * The package includes specialized models for primitive and reference types
 * such as boolean, numeric, string, object, and choice-based values.
 */
package org.int4.fx.values.model;
