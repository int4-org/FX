package org.int4.fx.values.domain;

final class InapplicableDomain<T> implements Domain<T> {
  private static final Membership.Excluded INAPPLICABLE_EXCLUSION = new Membership.Excluded(DomainTemplates.INAPPLICABLE);
  private static final View<?> NOOP_VIEW = new View<>() {};

  private static final InapplicableDomain<?> INSTANCE = new InapplicableDomain<>();

  @SuppressWarnings("unchecked")
  static <T> InapplicableDomain<T> instance() {
    return (InapplicableDomain<T>)INSTANCE;
  }

  private InapplicableDomain() {
  }

  @Override
  public boolean contains(T value) {
    return false;
  }

  @Override
  public Membership evaluate(T value) {
    return INAPPLICABLE_EXCLUSION;
  }

  @Override
  public Domain<T> nullable() {
    return this;
  }

  @Override
  public View<T> view(Class<?>... classes) {
    @SuppressWarnings("unchecked")
    View<T> castView = (View<T>)NOOP_VIEW;

    return castView;
  }

  @Override
  public <V extends View<T>> V requireView(Class<?> viewClass) {
    throw new IllegalStateException("Domain does not support view: " + viewClass.getSimpleName());
  }

  @Override
  public String toString() {
    return "Domain[inapplicable]";
  }
}
