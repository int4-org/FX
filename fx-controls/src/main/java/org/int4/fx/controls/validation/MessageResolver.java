package org.int4.fx.controls.validation;

/**
 * A strategy for resolving validation issues into human-readable messages.
 */
@FunctionalInterface
public interface MessageResolver {

  /**
   * Resolves the given validation issue into a localized message.
   *
   * @param issue the {@link ValidationIssue} to resolve, cannot be {@code null}
   * @return a human-readable message, or {@code null} if no message could be resolved
   */
  String resolve(ValidationIssue<?> issue);
}
