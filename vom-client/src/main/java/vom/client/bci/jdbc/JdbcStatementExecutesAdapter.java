package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.jdbc.visitor.StatementExecutesVisitor;

public class JdbcStatementExecutesAdapter extends VOMClassVisitAdapter {

  public JdbcStatementExecutesAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.jdbc-statement").contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return (methodName.startsWith("execute") || "addBatch".equals(methodName))
      && descriptor.startsWith("(Ljava/lang/String;");
  }

  @Override
  public MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    return new StatementExecutesVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );
  }

}
