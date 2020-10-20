package vom.client.bci.trove;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class VomitInChasing extends Throwable implements PointInChasing, Serializable {

  public VomitInChasing(Throwable cause) {
    super(cause);
  }

  @Override
  public void addArgument(Object argument) {
    throw new UnsupportedOperationException();
  }

}
