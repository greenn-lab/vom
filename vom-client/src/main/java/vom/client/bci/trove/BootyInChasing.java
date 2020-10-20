package vom.client.bci.trove;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class BootyInChasing extends PointInChasing implements Serializable {

  private final String className;
  private final String methodName;
  private final long elapsed;


  @Builder
  public BootyInChasing(String className, String methodName, long elapsed) {
    this.className = className;
    this.methodName = methodName;
    this.elapsed = elapsed;
  }

  @Override
  public String getSignature() {
    return String.format("%s#%s", className, methodName);
  }
}
