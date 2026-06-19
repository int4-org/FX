package org.int4.fx.values.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WritableModelBaseTest {

  private static final Template TEMPLATE = Template.of("test");

  static class ListModel extends WritableModelBase<List<String>> {
    ListModel(List<String> initialValue, Domain<List<String>> initialDomain) {
      super(initialValue, initialDomain);
    }

    @Override
    protected List<String> makeImmutable(List<String> input) {
      return Collections.unmodifiableList(new ArrayList<>(input));
    }

    public void setDomain(Domain<List<String>> domain) {
      super.updateDomain(domain);
    }
  }

  @Test
  void shouldMakeValueImmutableInConstructor() {
    List<String> mutableList = new ArrayList<>(List.of("a", "b"));
    ListModel model = new ListModel(mutableList, Domain.any());

    List<String> storedValue = model.getRawValue().orElseThrow();

    assertThat(storedValue).isNotSameAs(mutableList);
    assertThat(storedValue).containsExactly("a", "b");

    mutableList.add("c");

    assertThat(storedValue).containsExactly("a", "b"); // Stored value should not be affected
  }

  @Test
  void shouldMakeValueImmutableInSetValue() {
    ListModel model = new ListModel(List.of(), Domain.any());
    List<String> mutableList = new ArrayList<>(List.of("a", "b"));

    model.setValue(mutableList);

    List<String> storedValue = model.getRawValue().orElseThrow();

    assertThat(storedValue).isNotSameAs(mutableList);
    assertThat(storedValue).containsExactly("a", "b");

    mutableList.add("c");

    assertThat(storedValue).containsExactly("a", "b");
  }

  @Test
  void shouldMakeValueImmutableInTrySet() {
    ListModel model = new ListModel(List.of(), Domain.any());
    List<String> mutableList = new ArrayList<>(List.of("a", "b"));

    model.trySet(mutableList, Function.identity(), TEMPLATE);

    List<String> storedValue = model.getRawValue().orElseThrow();

    assertThat(storedValue).isNotSameAs(mutableList);
    assertThat(storedValue).containsExactly("a", "b");

    mutableList.add("c");

    assertThat(storedValue).containsExactly("a", "b");
  }

  @Test
  void shouldMakeValueImmutableInConstructorWithInapplicableDomain() {
    List<String> mutableList = new ArrayList<>(List.of("a", "b"));
    ListModel model = new ListModel(mutableList, Domain.inapplicable());

    List<String> storedValue = model.getRawValue().orElseThrow();

    assertThat(storedValue).isNotSameAs(mutableList);
    assertThat(storedValue).containsExactly("a", "b");

    mutableList.add("c");

    assertThat(storedValue).containsExactly("a", "b");
  }

  @Test
  void shouldMakeValueImmutableInReevaluate() {
    List<String> mutableList = new ArrayList<>(List.of("a", "b"));
    ListModel model = new ListModel(mutableList, Domain.any());

    List<String> storedValue = model.getRawValue().orElseThrow();

    assertThat(storedValue).containsExactly("a", "b");

    // reevaluate is triggered by setDomain
    model.setDomain(Domain.of(List.of("a", "b")));

    List<String> storedValueAfterReevaluate = model.getRawValue().orElseThrow();

    assertThat(storedValueAfterReevaluate).containsExactly("a", "b");

    // Verify that the stored value is indeed a different (immutable) copy
    mutableList.add("c");

    assertThat(storedValueAfterReevaluate).containsExactly("a", "b");
  }
}
