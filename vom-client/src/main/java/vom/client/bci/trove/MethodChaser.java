package vom.client.bci.trove;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MethodChaser extends Chaser implements Serializable {

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
