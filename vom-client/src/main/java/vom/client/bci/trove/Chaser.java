package vom.client.bci.trove;

import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Chaser implements Serializable {

  public static final String CHASER_INTERNAL =
    Type.getInternalName(Chaser.class);


  @Setter
  private long started = System.currentTimeMillis();

  @Setter
  private long arrived;

  @Setter
  private List<Trove.Argument> arguments = new ArrayList<Trove.Argument>();

  public void addArgument(Object argument) {
    arguments.add(new Trove.Argument(argument));
  }

  public void close() {
    arrived = System.currentTimeMillis();
  }

  public void keeping(Trove trove) {
    if (trove != null) {
      trove.addBooty(this);
    }
  }

  public abstract String signature();
}
