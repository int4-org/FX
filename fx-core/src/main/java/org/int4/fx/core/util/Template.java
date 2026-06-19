package org.int4.fx.core.util;

import java.util.Objects;
import java.util.SequencedMap;
import java.util.regex.Pattern;

import org.int4.common.collection.Immutable;

/**
 * A template for generating messages.
 * <p>
 * A template represents a logical message descriptor consisting of a stable
 * key and a set of named arguments. It acts as an intermediate representation
 * that can be consumed by translation layers to produce end-user strings.
 * <p>
 * Keys follow a strict dot-separated format consisting of one or more parts.
 * Each part must start with a lowercase letter or an underscore. Subsequent
 * characters in a part can contain mixed case letters, numbers, underscores,
 * and dashes. Numbers are not allowed immediately following a dot. Dashes
 * cannot be doubled or appear at the beginning or end of a part. No spaces
 * are allowed.
 *
 * @param key a key, never {@code null}
 * @param args the orderded map of named arguments for this template, never {@code null} and
 *   does not contain {@code null} keys; if there are no arguments, the map returned is empty
 */
public record Template(String key, SequencedMap<String, Object> args) {

  /**
   * A regular expression string for a single part of a template key.
   */
  private static final String PART_PATTERN = "[a-z_]([a-zA-Z0-9_]|-(?=[a-zA-Z0-9_]))*";

  /**
   * A regular expression pattern that matches valid template keys according
   * to the contract defined by this interface.
   */
  private static final Pattern KEY_PATTERN = Pattern.compile("^" + PART_PATTERN + "(\\." + PART_PATTERN + ")*$");

  /**
   * Creates a template with the given key and no arguments.
   * <p>
   * The key should follow the format described in the class-level documentation.
   *
   * @param key the template key, not {@code null}
   * @return a template with the given key and no arguments, never {@code null}
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public static Template of(String key) {
    return of(key, Immutable.sequencedMap());
  }

  /**
   * Creates a template with the given key and arguments.
   * <p>
   * The key should follow the format described in the class-level documentation.
   *
   * @param key the template key, not {@code null}
   * @param args the arguments for this template, cannot be {@code null}
   * @return a template with the given key and arguments, never {@code null}
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public static Template of(String key, SequencedMap<String, Object> args) {
    return new Template(key, args);
  }

  /**
   * Constructs a new instance.
   */
  public Template {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(args, "args");

    if(!KEY_PATTERN.matcher(key).matches()) {
      throw new IllegalArgumentException("invalid key format: " + key);
    }
  }
}
