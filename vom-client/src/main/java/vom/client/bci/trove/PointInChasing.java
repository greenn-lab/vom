package vom.client.bci.trove;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class PointInChasing {

  @Setter
  private List<TroveArgument> arguments = new ArrayList<TroveArgument>();

  public void addArgument(Object argument) {
    arguments.add(new TroveArgument(argument));
  }

  public abstract String getSignature();
  
}
