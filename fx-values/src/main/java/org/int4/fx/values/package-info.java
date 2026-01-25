/**
 * Core abstractions for observable, domain-driven values in JavaFX applications.
 * <p>
 * This module provides building blocks for modeling application state with
 * strong validity constraints, reactive observability, and optional UI-oriented
 * views. It is organized around three main concerns:
 * <ul>
 *   <li><strong>Domains</strong> – read-only views and constraints for values,
 *       supporting transformations, normalization, stepping, and indexing.
 *       Key types include {@link org.int4.fx.values.domain.Domain} and
 *       {@link org.int4.fx.values.domain.View}.</li>
 *   <li><strong>Models</strong> – mutable, observable holders of values that
 *       expose validity, applicability, and domain semantics. Typical models
 *       include {@link org.int4.fx.values.model.BooleanModel},
 *       {@link org.int4.fx.values.model.IntegerModel},
 *       {@link org.int4.fx.values.model.StringModel} and
 *       {@link org.int4.fx.values.model.ChoiceModel}.</li>
 *   <li><strong>Utilities</strong> – helpers for composing observables,
 *       triggers, and derived values across models and domains.
 *       Notable classes are {@link org.int4.fx.values.util.Observe} and
 *       {@link org.int4.fx.values.util.Trigger}.</li>
 * </ul>
 * <p>
 * These abstractions are designed to be compatible with JavaFX's observable
 * APIs while remaining decoupled from specific controls, making it easy to
 * use models as sources of truth in both UI and business logic.
 * <p>
 * Usage is generally intended on the JavaFX application thread. Updates from
 * background threads should be marshalled appropriately (e.g., via
 * {@code Platform.runLater}) to avoid threading issues.
 *
 * @see org.int4.fx.values.model
 * @see org.int4.fx.values.domain
 * @see org.int4.fx.values.util
 */
package org.int4.fx.values;
