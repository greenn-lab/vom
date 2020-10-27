package vom.client.bci.trove;

import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Chasing implements Serializable {

  public static final String CHASE_INTERNAL =
    Type.getInternalName(Chasing.class);

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

  public abstract String signature();
}
