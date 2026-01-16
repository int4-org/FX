/**
 * Fluent builders for JavaFX controls.
 * <p>
 * This package provides builder types for JavaFX {@link javafx.scene.control.Control} subclasses,
 * enabling declarative construction, configuration, and optional model binding.
 * <p>
 * Controls created through this package automatically expose validation and
 * interaction state via CSS pseudo-classes when bound to a
 * {@link org.int4.fx.values.model.ValueModel}.
 * <p>
 * The following pseudo-classes are applied by the builders:
 * <ul>
 *   <li><strong>{@code :invalid}</strong> – set when the bound model is currently
 *       invalid according to its domain. This pseudo-class is cleared when the
 *       model becomes valid again.</li>
 *   <li><strong>{@code :touched}</strong> – set when the user has interacted with
 *       the control (for example by modifying its value). This flag is not
 *       automatically cleared and can be used to distinguish pristine controls
 *       from those that have received user input.</li>
 *   <li><strong>{@code :dirty}</strong> – set while the control contains user
 *       modifications that have not yet been committed back to the model.
 *       The flag is cleared when the value is committed, typically on focus loss
 *       or an explicit commit event.</li>
 * </ul>
 * <p>
 * These pseudo-classes allow styling of validation feedback, interaction state,
 * and commit behavior purely via CSS, without requiring control-specific logic.
 * <p>
 * Many builders have abstract base types that mirror the JavaFX control hierarchy,
 * allowing consistent fluent APIs across related controls.
 */
package org.int4.fx.builders.control;
