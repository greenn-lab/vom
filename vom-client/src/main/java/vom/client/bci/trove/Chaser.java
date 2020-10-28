package vom.client.bci.trove;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Chaser implements Serializable {

  @Setter
  private long started = System.currentTimeMillis();

  @Setter
  private long arrived;

  @Setter
  private List<TroveArgument> arguments = new ArrayList<TroveArgument>();

  public void addArgument(Object argument) {
    arguments.add(new TroveArgument(argument));
  }

  public void arrived() {
    arrived = System.currentTimeMillis();
  }

  public void keep(Trove trove) {
    if (trove != null) {
      trove.addBooty(this);
    }
  }

  public abstract String signature();
}
