package vom.client.bci.trove;

import lombok.Getter;
import org.objectweb.asm.Type;

import java.io.Serializable;

@Getter
public class MethodChaser extends Chaser implements Serializable {

  public static final Type METHOD_CHASER_TYPE = Type.getType(MethodChaser.class);
  public static final String METHOD_CHASER_INTERNAL =
    Type.getInternalName(MethodChaser.class);
  public static final String METHOD_CHASER_DESCRIPTOR =
    "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V";


  private final String className;
  private final String methodName;


  public MethodChaser(
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
  public String signature() {
    return className + "#" + methodName;
  }
}
