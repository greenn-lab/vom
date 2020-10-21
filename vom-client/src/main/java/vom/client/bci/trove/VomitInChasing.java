package vom.client.bci.trove;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class VomitInChasing extends Chasing implements Serializable {

  private final Throwable cause;

  public VomitInChasing(Throwable cause) {
    this.cause = cause;
  }

  @Override
  public String getSignature() {
    return cause.getMessage();
  }
}
