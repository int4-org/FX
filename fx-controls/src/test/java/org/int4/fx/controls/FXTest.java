package org.int4.fx.controls;

import javafx.application.Platform;

import org.junit.jupiter.api.BeforeAll;

public class FXTest {

  @BeforeAll
  static void beforeAll() {
    try {
      Platform.startup(() -> {});
    }
    catch(IllegalStateException e) {
      // ignore
    }
  }
}
