package org.int4.fx.values.model;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

import org.int4.fx.values.domain.Domain;

abstract class ModelBase<T> extends ObservableValueBase<T> implements ValueModel<T> {
  private final ReadOnlyBooleanWrapper applicable = new ReadOnlyBooleanWrapper();
  private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();
  private final ObjectProperty<Domain<T>> domain = new SimpleObjectProperty<>();

  /**
   * Indicates that the current raw value isn't correct, as it was unconvertable
   * to a domain value. Comparisons with the raw value should therefore always
   * be considered false if this flag is true.
   */
  private boolean unconvertable;

  ModelBase(Domain<T> domain) {
    this.domain.set(Objects.requireNonNull(domain, "domain"));
  }

  void init() {

    /*
     * This can be done differently once there are flexible constructor bodies
     * in Java 25. It must be done after the subtype has set its raw value, as
     * it will be queried too early otherwise.
     */

    domain.subscribe(v -> {
      applicable.set(v.isNotEmpty());

      if(applicable.get()) {
        if(!unconvertable) {
          setValue(getRawValue());
        }
      }
      else {
        valid.set(true);
      }
    });
  }

  @Override
  public final ObservableValue<Boolean> applicable() {
    return applicable.getReadOnlyProperty();
  }

  @Override
  public final ObservableValue<Boolean> valid() {
    return valid.getReadOnlyProperty();
  }

  @Override
  public final ObjectProperty<Domain<T>> domainProperty() {
    return domain;
  }

  @Override
  public <U> boolean trySet(U value, Function<U, T> converter) {
    try {
      T convertedValue = converter.apply(value);
      T raw = getRawValue();

      /*
       * If previously unconvertable, it means the raw value is actually not
       * correctly representing the previous state, and therefore it should
       * be considered always different from the new converted value:
       */

      if(unconvertable || !Objects.equals(raw, convertedValue)) {
        setValue(convertedValue);

        return true;
      }
    }
    catch(Exception e) {
      markInvalid();
    }

    return false;
  }

  private void markInvalid() {
    valid.set(false);

    this.unconvertable = true;
  }

  protected final void updateValidity(T newValue) {
    unconvertable = false;
    valid.set(getDomain().isEmpty() || getDomain().contains(newValue));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + (isApplicable() ? Objects.toString(getValue()) : "not applicable") + "]";
  }
}
