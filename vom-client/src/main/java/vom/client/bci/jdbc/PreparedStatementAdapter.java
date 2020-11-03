package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.jdbc.visitor.PreparedStatementExecutesVisitor;
import vom.client.bci.jdbc.visitor.PreparedStatementParametersVisitor;

public class PreparedStatementAdapter
  extends VOMClassVisitAdapter {

  private boolean isExecute = false;
  private boolean isParameter = false;

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
    isExecute = (
      "execute".equals(methodName)
        || "executeLargeUpdate".equals(methodName)
        || "executeQuery".equals(methodName)
        || "executeUpdate".equals(methodName)
    ) && descriptor.startsWith("()");

    isParameter = methodName.startsWith("set")
      && descriptor.startsWith("(I")
      && descriptor.endsWith(")V");

    return isExecute || isParameter;
  }

  @Override
  public MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {

    if (isExecute) {
      visitor = new PreparedStatementExecutesVisitor(
        reader,
        visitor,
        methodName,
        descriptor
      );
    }

    if (isParameter) {
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
