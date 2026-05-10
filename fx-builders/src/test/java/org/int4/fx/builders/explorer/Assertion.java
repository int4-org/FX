package org.int4.fx.builders.explorer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a method as an assertion for an {@link Explorable} model.
 * <p>
 * Assertion methods are invoked after actions are executed to verify that the
 * model's state matches expectations. They should throw an exception (e.g.,
 * {@link AssertionError}) if the verification fails.
 * <p>
 * Assertions are typically parameterless and observe the state exposed by
 * {@link Explorable#snapshot()}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Assertion {}
