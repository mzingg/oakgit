package oakgit.engine.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class PlaceholderData {

  private final Map<Integer, Object> delegate = new HashMap<>();
  private int maxIndex = 0;

  public PlaceholderData set(int index, Object value) {
    if (index <= 0) {
      throw new IllegalArgumentException("index must be greater or equals 1");
    }
    delegate.put(index, value);
    if (index > maxIndex) {
      maxIndex = index;
    }
    return this;
  }

  public int maxIndex() {
    if (maxIndex <= 0) {
      throw new IllegalStateException("no elements assigned - cannot determine maximum index");
    }
    return maxIndex;
  }

  public boolean hasIndex(int index) {
    return delegate.containsKey(index);
  }

  public Stream<Object> valueStream() {
    return delegate.values().stream();
  }

  public Object get(int index) {
    return typedNullsafe(index, Object.class).orElse(null);
  }

  public String getString(int index) {
    return typedNullsafe(index, String.class).orElse(null);
  }

  public Integer getInteger(int index) {
    return typedNullsafe(index, Integer.class).orElse(null);
  }

  public Long getLong(int index) {
    return typedNullsafe(index, Long.class).orElse(null);
  }

  public byte[] getBytes(int index) {
    return typedNullsafe(index, byte[].class).orElse(null);
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<T> typedNullsafe(int index, Class<T> targetClass) {
    if (index <= 0) {
      throw new IllegalArgumentException("index must be greater or equals 1");
    }
    if (!hasIndex(index)) {
      throw new IllegalArgumentException("index [" + index + "] does not exist");
    }

    Object element = delegate.get(index);
    if (element != null) {
      if (element instanceof String && targetClass.equals(byte[].class)) {
        return Optional.of((T) ((String) element).getBytes());
      } else if (targetClass.isAssignableFrom(element.getClass())) {
        return Optional.of((T) element);
      } else if (targetClass.equals(Long.class) && element instanceof Integer) {
        return Optional.of((T) Long.valueOf(((Integer) element).longValue()));
      } else {
        throw new IllegalArgumentException("Element [" + element + "] with index [" + index + "] is not of type " + targetClass);
      }
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("delegate", delegate)
        .append("maxIndex", maxIndex)
        .toString();
  }
}
