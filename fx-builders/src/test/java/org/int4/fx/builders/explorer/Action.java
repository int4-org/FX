package org.int4.fx.builders.explorer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.params.provider.ValueSource;

/**
 * Marks a method as an action that can be applied to an {@link Explorable} model.
 * <p>
 * Actions are executed during exhaustive exploration of the model's state space.
 * They may modify the state and are combined with assertions to verify correct behavior.
 * <p>
 * Methods annotated with {@code @Action} may optionally take parameters if used
 * with a {@link ValueSource} or other parameterized provider.
 * <p>
 * An action may be conditionally skipped using assumptions (e.g.,
 * {@link org.junit.jupiter.api.Assumptions#assumeTrue(boolean)}). If an assumption fails,
 * the action is aborted and the path continues without treating it as a failure.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {}