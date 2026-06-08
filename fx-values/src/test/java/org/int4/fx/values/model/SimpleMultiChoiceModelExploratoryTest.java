package org.int4.fx.values.model;

import java.util.ArrayList;
import java.util.List;

import org.int4.common.test.explorer.Action;
import org.int4.common.test.explorer.Assertion;
import org.int4.common.test.explorer.Explorable;
import org.int4.common.test.explorer.ExploratoryTestRunner;
import org.int4.fx.values.domain.Domain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SimpleMultiChoiceModelExploratoryTest {

  @Test
  void explore() {
    ExploratoryTestRunner.explore(TestExplorable.class, TestExplorable::new);
  }

  public static class TestExplorable implements Explorable {
    private static final Domain<List<String>> RESTRICTED_COMPOSITE_DOMAIN = Domain.where(list -> list.size() < 3);
    private static final Domain<String> RESTRICTED_ELEMENT_DOMAIN = Domain.of("Apple", "Berry", "Citrus");
    private static final int MAX_LIST_SIZE = 5;

    private final MultiChoiceModel<String> model = new SimpleMultiChoiceModel<>(List.of(), Domain.any(), Domain.any());

    private List<String> expectedItems = new ArrayList<>();
    private Domain<List<String>> expectedCompositeDomain = Domain.any();
    private Domain<String> expectedElementDomain = Domain.any();

    record State(
      List<String> expectedItems,
      Domain<List<String>> expectedCompositeDomain,
      Domain<String> expectedElementDomain
    ) {}

    @Override
    public Object snapshot() {
      return new State(
        expectedItems == null ? null : new ArrayList<>(expectedItems),
        expectedCompositeDomain,
        expectedElementDomain
      );
    }

    @Assertion
    public void assertState() {
      boolean valid = (expectedItems == null || expectedItems.stream().allMatch(expectedElementDomain::contains)) && expectedCompositeDomain.contains(expectedItems);
      boolean applicable = !expectedElementDomain.equals(Domain.inapplicable()) && !expectedCompositeDomain.equals(Domain.inapplicable());

      List<String> items = valid && applicable ? expectedItems : null;

      assertAll(
        () -> assertThat(model.isApplicable()).isEqualTo(applicable),
        () -> assertThat(model.isValid()).isEqualTo(valid || !applicable),
        () -> assertThat(model.getCompositeDomain()).isEqualTo(expectedCompositeDomain),
        () -> assertThat(model.getElementDomain()).isEqualTo(expectedElementDomain),
        () -> assertThat(model.getValue()).isEqualTo(items)
      );
    }

    @Action
    @ValueSource(strings = {"Apple", "Berry", "Citrus", "Pear"})
    public Runnable add(String item) {
      if(expectedItems == null) {
        expectedItems = new ArrayList<>();
      }

      if(expectedItems.size() >= MAX_LIST_SIZE) {
        return () -> {};
      }

      expectedItems.add(item);

      return () -> model.add(item);
    }

    @Action
    public Runnable addNull() {
      if(expectedItems == null) {
        expectedItems = new ArrayList<>();
      }

      if(expectedItems.size() >= MAX_LIST_SIZE) {
        return () -> {};
      }

      expectedItems.add(null);

      return () -> model.add(null);
    }

    @Action
    @ValueSource(strings = {"Apple", "Berry", "Citrus", "Pear"})
    public Runnable remove(String item) {
      if(expectedItems != null) {
        expectedItems.remove(item);
      }

      return () -> model.remove(item);
    }

    @Action
    public Runnable removeNull() {
      if(expectedItems != null) {
        expectedItems.remove(null);
      }

      return () -> model.remove(null);
    }

    @Action
    public Runnable replace() {
      if(expectedItems == null) {
        expectedItems = new ArrayList<>();
      }

      expectedItems.clear();
      expectedItems.addAll(List.of("Berry", "Citrus", "Berry"));

      return () -> model.setValue(List.of("Berry", "Citrus", "Berry"));
    }

    @Action
    public Runnable replaceWithNull() {
      expectedItems = null;

      return () -> model.setValue(null);
    }

    @Action
    public Runnable toRestrictedCompositeDomain() {
      expectedCompositeDomain = RESTRICTED_COMPOSITE_DOMAIN;

      return () -> model.setCompositeDomain(RESTRICTED_COMPOSITE_DOMAIN);
    }

    @Action
    public Runnable toUnrestrictedCompositeDomain() {
      expectedCompositeDomain = Domain.any();

      return () -> model.setCompositeDomain(Domain.any());
    }

    @Action
    public Runnable toInapplicableCompositeDomain() {
      expectedCompositeDomain = Domain.inapplicable();

      return () -> model.setCompositeDomain(Domain.inapplicable());
    }

    @Action
    public Runnable toRestrictedElementDomain() {
      expectedElementDomain = RESTRICTED_ELEMENT_DOMAIN;

      return () -> model.setElementDomain(RESTRICTED_ELEMENT_DOMAIN);
    }

    @Action
    public Runnable toUnrestrictedElementDomain() {
      expectedElementDomain = Domain.any();

      return () -> model.setElementDomain(Domain.any());
    }

    @Action
    public Runnable toInapplicableElementDomain() {
      expectedElementDomain = Domain.inapplicable();

      return () -> model.setElementDomain(Domain.inapplicable());
    }
  }
}
