package org.int4.fx.core.util;

import java.lang.reflect.RecordComponent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A specialized {@link Template} for implementations that are records.
 * <p>
 * This interface provides a default implementation of the {@link #args()}
 * method that uses reflection to extract all record components as named
 * arguments.
 */
public interface RecordBasedTemplate extends Template {

  @Override
  default Map<String, Object> args() {
    if(!getClass().isRecord()) {
      throw new IllegalStateException("Class must be a record: " + getClass().getName());
    }

    Map<String, Object> args = new LinkedHashMap<>();

    for(RecordComponent component : getClass().getRecordComponents()) {
      try {
        args.put(component.getName(), component.getAccessor().invoke(this));
      }
      catch(Exception e) {
        throw new IllegalStateException("Failed to extract record component: " + component.getName(), e);
      }
    }

    return Collections.unmodifiableMap(args);
  }
}
