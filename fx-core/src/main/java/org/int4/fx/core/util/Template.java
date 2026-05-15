package org.int4.fx.core.util;

import java.util.Map;
import java.util.regex.Pattern;

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
 */
public interface Template {

  /**
   * A regular expression string for a single part of a template key.
   */
  String PART_PATTERN = "[a-z_]([a-zA-Z0-9_]|-(?=[a-zA-Z0-9_]))*";

  /**
   * A regular expression pattern that matches valid template keys according
   * to the contract defined by this interface.
   */
  Pattern KEY_PATTERN = Pattern.compile("^" + PART_PATTERN + "(\\." + PART_PATTERN + ")*$");

  /**
   * Returns the key for this template.
   *
   * @return a non-null translation key
   */
  String key();

  /**
   * Returns the named arguments for this template.
   * <p>
   * The arguments can be used to populate placeholders in a message
   * string. The returned map is guaranteed to have a fixed iteration
   * order and to contain only non-null keys.
   *
   * @return a non-null map of arguments with fixed iteration order
   */
  Map<String, Object> args();
}
