package vom.client.bci.trove;

import lombok.Getter;
import org.objectweb.asm.Type;

import java.io.Serializable;

@Getter
public class BootyInChasing extends Chasing implements Serializable {

  public static final Type BOOTY_TYPE = Type.getType(BootyInChasing.class);
  public static final String BOOTY_INTERNAL =
    Type.getInternalName(BootyInChasing.class);
  public static final String BOOTY_CONSTRUCTOR_DESC =
    "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V";


  private final String className;
  private final String methodName;


  public BootyInChasing(
    String className,
    String methodName,
    Object[] arguments
  ) {
    this.className = className;
    this.methodName = methodName;

    if (arguments != null) {
      for (Object argument : arguments) {
        addArgument(argument);
      }
    }
  }


  @Override
  public String getSignature() {
    return String.format("%s#%s", className, methodName);
  }

}
