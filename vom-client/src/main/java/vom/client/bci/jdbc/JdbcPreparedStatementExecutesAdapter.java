package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;

public class JdbcPreparedStatementExecutesAdapter
  extends VOMClassVisitAdapter {

  public JdbcPreparedStatementExecutesAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.jdbc-prepared-statement")
      .contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return descriptor.startsWith("()")
      &&
      (
        "execute".equals(methodName)
          || "executeLargeUpdate".equals(methodName)
          || "executeQuery".equals(methodName)
          || "executeUpdate".equals(methodName)
      );
  }

  @Override
  public MethodVisitor methodVisitor(MethodVisitor visitor, String methodName, String descriptor) {
    return null;
  }
}
