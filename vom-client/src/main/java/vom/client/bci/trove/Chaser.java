package vom.client.bci.trove;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.Type;
import vom.client.fastjson.ChaserArgumentsCodec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class Chaser implements Serializable {

  public static final String CHASER_INTERNAL =
    Type.getInternalName(Chaser.class);


  @Setter
  private long started = System.currentTimeMillis();

  @Setter
  private long arrived;

  @JSONField(serializeUsing = ChaserArgumentsCodec.class)
  @Setter
  private Map<Integer, Trove.Argument> arguments =
    new HashMap<Integer, Trove.Argument>();

  public void addArgument(int index, Object argument) {
    arguments.put(index, new Trove.Argument(argument));
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
