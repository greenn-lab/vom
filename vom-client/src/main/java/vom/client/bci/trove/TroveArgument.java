package vom.client.bci.trove;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class TroveArgument implements Serializable {

  private final Class<?> type;
  private final transient Object value;

  public TroveArgument(Object value) {
    this.type = null == value ? null:value.getClass();
    this.value = value;
  }

}
