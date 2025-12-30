package org.int4.fx.builders.internal;

public interface ShowingStateListener {
  static final Object KEY = new Object();
  static final Object SHOW_STATE_MANAGING_SCENE = new Object();

  void showStatusChanged(boolean showing);

}
