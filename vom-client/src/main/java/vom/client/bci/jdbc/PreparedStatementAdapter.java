package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.jdbc.visitor.PreparedStatementExecutesVisitor;
import vom.client.bci.jdbc.visitor.PreparedStatementParametersVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreparedStatementAdapter extends VOMClassVisitAdapter {

  private static final Set<String> executeMethodNames =
    new HashSet<String>(
      Arrays.asList(
        "execute",
        "executeLargeUpdate",
        "executeQuery",
        "executeUpdate"
      )
    );

  private boolean isExecuteMethod = false;
  private boolean isParameterMethod = false;


  public PreparedStatementAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.jdbc-prepared-statement")
      .contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    isExecuteMethod = executeMethodNames.contains(methodName)
      && descriptor.startsWith("()");

    isParameterMethod = methodName.startsWith("set")
      && descriptor.startsWith("(I")
      && descriptor.endsWith(")V");

    return isExecuteMethod || isParameterMethod;
  }

  @Override
  public MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {

    if (isExecuteMethod) {
      visitor = new PreparedStatementExecutesVisitor(
        reader,
        visitor,
        methodName,
        descriptor
      );
    }

    if (isParameterMethod) {
      visitor = new PreparedStatementParametersVisitor(
        reader,
        visitor,
        methodName,
        descriptor
      );
    }

    return visitor;
  }

}
