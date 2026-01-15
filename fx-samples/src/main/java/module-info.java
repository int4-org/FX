/**
 * Module that contains example/sample JavaFX applications demonstrating
 * the use of the org.int4.fx builder APIs and related utilities.
 *
 * <p>These samples are intended for documentation and manual testing. They
 * illustrate common usage patterns such as declarative UI construction,
 * binding to observable values and models, inline styling, and validation.
 */
module org.int4.fx.samples {
  requires transitive org.int4.fx.builders;

  exports org.int4.fx.samples;
}

