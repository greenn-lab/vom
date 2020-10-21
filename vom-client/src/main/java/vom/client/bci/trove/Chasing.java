package vom.client.bci.trove;

import jdk.nashorn.internal.codegen.types.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Chasing {

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

  public abstract String getSignature();

}
