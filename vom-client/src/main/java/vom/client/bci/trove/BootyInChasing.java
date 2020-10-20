package vom.client.bci.trove;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BootyInChasing implements PointInChasing, Serializable {

  private final String signature;
  private final String name;
  private final long elapsed;

  @Setter
  private List<TroveArgument> arguments = new ArrayList<TroveArgument>();


  @Builder
  public BootyInChasing(String signature, String name, long elapsed) {
    this.signature = signature;
    this.name = name;
    this.elapsed = elapsed;
  }


  @Override
  public void addArgument(Object argument) {
    arguments.add(new TroveArgument(argument));
  }

}
