package org.int4.fx.builders.control;

import java.lang.ref.WeakReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ReferenceAsserts {

  @SuppressWarnings("all")  // to suppress parameter assignment warning
  public static <T> T testIfStronglyReferenced(T obj, Runnable clearRefs) {
    WeakReference<T> ref = new WeakReference<>(obj);

    clearRefs.run();
    obj = null;

    testIfStronglyReferenced(ref);

    return ref.get();
  }

  @SuppressWarnings("all")  // to suppress parameter assignment warning
  public static void testIfNotStronglyReferenced(Object obj, Runnable clearRefs) {
    WeakReference<Object> ref = new WeakReference<>(obj);

    clearRefs.run();
    obj = null;

    testIfStronglyReferenced(ref);
  }

  public static <T> T testIfStronglyReferenced(WeakReference<T> ref) {
    System.gc();

    assertThat(ref.get()).isNotNull();

    return ref.get();
  }

  public static void testIfNotStronglyReferenced(WeakReference<?> ref) {
    await().untilAsserted(() -> {
      System.gc();

      assertThat(ref.get()).isNull();
    });
  }
}
