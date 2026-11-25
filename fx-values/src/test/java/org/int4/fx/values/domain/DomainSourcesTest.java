package org.int4.fx.values.domain;

import java.util.ArrayList;
import java.util.List;

import org.int4.fx.values.domain.DomainSource.DomainTransition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainSourcesTest {

  @Test
  void fixedShouldReturnTheSameDomainAndNeverTransition() {
    Domain<String> domain = Domain.of("a", "b");
    DomainSource<String> source = DomainSources.of(domain);
    List<DomainTransition<String>> transitions = new ArrayList<>();

    source.transitions(transitions::add);

    assertThat(source.get()).isSameAs(domain);
    assertThat(transitions).isEmpty();
  }

  @Test
  void inapplicableShouldProvideTheInapplicableDomain() {
    DomainSource<String> source = DomainSources.inapplicable();

    assertThat(source.get()).isSameAs(Domain.inapplicable());
  }

  @Test
  void mutableShouldPublishTransitionsForChanges() {
    Domain<String> first = Domain.of("a");
    Domain<String> second = Domain.of("a", "b");
    DomainSources.Mutable<String> source = DomainSources.mutable(first);
    List<DomainTransition<String>> transitions = new ArrayList<>();

    source.transitions(transitions::add);
    source.set(second);

    assertThat(source.get()).isSameAs(second);
    assertThat(transitions).hasSize(1);
    assertThat(transitions.get(0).previous()).isSameAs(first);
    assertThat(transitions.get(0).successor()).isSameAs(second);
    assertThat(transitions.get(0).edit()).isInstanceOf(Edit.Replaced.class);
  }

  @Test
  void mutableShouldIgnoreSettingTheSameDomain() {
    Domain<String> domain = Domain.of("a");
    DomainSources.Mutable<String> source = DomainSources.mutable(domain);
    List<DomainTransition<String>> transitions = new ArrayList<>();

    source.transitions(transitions::add);
    source.set(domain);

    assertThat(source.get()).isSameAs(domain);
    assertThat(transitions).isEmpty();
  }

  @Test
  void transitionsShouldRejectTheSamePredecessorAndSuccessor() {
    Domain<String> domain = Domain.of("a");

    assertThatThrownBy(() -> new DomainTransition<>(domain, domain, new Edit.Replaced<>()))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void initialTransitionsShouldRequireAReplacedEdit() {
    Domain<String> domain = Domain.of("a");

    assertThatThrownBy(() -> new DomainTransition<>(null, domain, new Edit.Indexed<>(List.of())))
      .isInstanceOf(IllegalArgumentException.class);
    assertThat(new DomainTransition<>(null, domain, new Edit.Replaced<>()).previous()).isNull();
  }

  @Test
  void transitionsShouldAllowAnInitialTransition() {
    DomainTransition<String> transition = new DomainTransition<>(null, Domain.of("a"), new Edit.Replaced<>());

    assertThat(transition.previous()).isNull();
    assertThat(transition.successor()).isNotNull();
  }

  @Test
  void sourcesShouldRejectNullDomains() {
    assertThatThrownBy(() -> DomainSources.of(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainSources.mutable(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> DomainSources.mutable(Domain.any()).set(null)).isInstanceOf(NullPointerException.class);
  }
}
