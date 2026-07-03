package org.int4.fx.builders.control;

import java.text.NumberFormat;
import java.text.ParsePosition;

final class Parsers {

  /*
   * All parse functions:
   * - Must consume all input, and throw an exception if they can't
   * - Should return null when input is blank or null
   */

  static Integer parseInt(NumberFormat formatter, String input) {
    if(input == null || input.isBlank()) {
      return null;
    }

    ParsePosition pp = new ParsePosition(0);
    Number n = formatter.parse(input, pp);

    if(n != null && pp.getIndex() == input.length()) {
      return n.intValue();
    }

    throw new NumberFormatException("Unable to parse: " + input);
  }

  static Long parseLong(NumberFormat formatter, String input) {
    if(input == null || input.isBlank()) {
      return null;
    }

    ParsePosition pp = new ParsePosition(0);
    Number n = formatter.parse(input, pp);

    if(n != null && pp.getIndex() == input.length()) {
      return n.longValue();
    }

    throw new NumberFormatException("Unable to parse: " + input);
  }

  static Double parseDouble(NumberFormat formatter, String input) {
    if(isBlank(input)) {
      return null;
    }

    ParsePosition position = new ParsePosition(0);
    Number number = formatter.parse(input, position);

    if(number != null && position.getIndex() == input.length()) {
      return number.doubleValue();
    }

    throw new NumberFormatException("Unable to parse: " + input);
  }

  private static boolean isBlank(String input) {
    return input == null || input.isBlank();
  }

  private Parsers() {}
}