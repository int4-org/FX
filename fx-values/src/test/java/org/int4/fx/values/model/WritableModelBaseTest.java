package org.int4.fx.values.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.int4.fx.core.util.Observe;
import org.int4.fx.core.util.RawValue;
import org.int4.fx.core.util.Template;
import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.domain.DomainSource;
import org.int4.fx.values.domain.DomainSource.DomainTransition;
import org.int4.fx.values.domain.DomainSources;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WritableModelBaseTest {

  private static final Template TEMPLATE = Template.of("test");

  static class ListModel extends ModelBase<List<String>> {
    ListModel(List<String> initialValue, Domain<List<String>> initialDomain) {
      this(initialValue, DomainSources.mutable(initialDomain));
    }

    ListModel(List<String> initialValue, DomainSource<List<String>> source) {
      super(initialValue, source);
    }

    @Override
    protected List<String> makeImmutable(List<String> input) {
      return Collections.unmodifiableList(new ArrayList<>(input));
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
  void domainChangesShouldBePublishedAtomically() {
    ListModel model = new ListModel(List.of("a"), Domain.any());
    List<RawValue<List<String>>> observed = new ArrayList<>();

    Observe.values(model.transitions(), model.rawValue()).subscribe((transition, rawValue) -> observed.add(rawValue));

    observed.clear();

    model.setDomain(Domain.of(List.of("b")));  // excludes the current value

    assertThat(observed).hasSize(1);  // one notification containing both new values
    assertThat(observed.get(0)).isInstanceOf(RawValue.Invalid.class);
    assertThat(model.isInvalid()).isTrue();
  }

  @Test
  void valueChangesShouldBePublishedAtomically() {
    ListModel model = new ListModel(List.of("a"), Domain.of(List.of("a"), List.of("b")));
    List<Domain<List<String>>> observed = new ArrayList<>();

    Observe.values(model.transitions(), model.rawValue()).subscribe((transition, rawValue) -> observed.add(transition.successor()));

    observed.clear();

    model.setValue(List.of("b"));

    assertThat(observed).hasSize(1);  // one notification containing both new values
    assertThat(observed.get(0)).isEqualTo(model.getDomain());
  }

  @Test
  void setDomainShouldReplaceTheDomain() {
    ListModel model = new ListModel(List.of("a"), Domain.of(List.of("a"), List.of("b")));
    Domain<List<String>> next = Domain.of(List.of("b"));

    model.setDomain(next);

    assertThat(model.getDomain()).isSameAs(next);
    assertThat(model.isInvalid()).isTrue();
  }

  @Test
  void setDomainShouldDetachFromThePreviousSource() {
    DomainSources.Mutable<List<String>> original = DomainSources.mutable(Domain.any());
    ListModel model = new ListModel(List.of("a"), original);
    Domain<List<String>> replacement = Domain.of(List.of("a"));

    model.setDomain(replacement);
    original.set(Domain.of(List.of("b")));  // no longer followed

    assertThat(model.getDomain()).isSameAs(replacement);
    assertThat(model.isValid()).isTrue();
  }

  @Test
  void setSourceShouldFollowTheNewSource() {
    Domain<List<String>> initial = Domain.of(List.of("a"));
    ListModel model = new ListModel(List.of("a"), initial);
    DomainSources.Mutable<List<String>> replacement = DomainSources.mutable(Domain.any());
    Domain<List<String>> next = Domain.of(List.of("b"));

    model.setSource(replacement);
    replacement.set(next);

    assertThat(model.getDomain()).isSameAs(next);
    assertThat(model.isInvalid()).isTrue();
  }

  @Test
  void setSourceShouldStopFollowingThePreviousSource() {
    DomainSources.Mutable<List<String>> original = DomainSources.mutable(Domain.any());
    ListModel model = new ListModel(List.of("a"), original);
    Domain<List<String>> replacement = Domain.of(List.of("a"));

    model.setSource(DomainSources.of(replacement));
    original.set(Domain.of(List.of("b")));  // no longer followed

    assertThat(model.getDomain()).isSameAs(replacement);
  }

  @Test
  void setSourceWithTheSameDomainShouldNotNotify() {
    Domain<List<String>> domain = Domain.any();
    ListModel model = new ListModel(List.of("a"), DomainSources.of(domain));
    List<DomainTransition<List<String>>> observed = new ArrayList<>();

    Observe.values(model.transitions(), model.rawValue()).subscribe((transition, rawValue) -> observed.add(transition));

    observed.clear();

    model.setSource(DomainSources.of(domain));

    assertThat(observed).isEmpty();
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
