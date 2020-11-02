package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.jdbc.visitor.PreparedStatementParametersVisitor;

public class JdbcPreparedStatementParametersAdapter
  extends VOMClassVisitAdapter {

  public JdbcPreparedStatementParametersAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.jdbc-prepared-statement")
      .contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return methodName.startsWith("set")
      && descriptor.startsWith("(I")
      && descriptor.endsWith(")V");
  }

  @Override
  public MethodVisitor methodVisitor(MethodVisitor visitor, String methodName, String descriptor) {
    return new PreparedStatementParametersVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );
  }

}
