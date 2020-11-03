package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.jdbc.visitor.ConnectionPrepareStatementVisitor;

public class ConnectionAdapter
  extends VOMClassVisitAdapter {

  public ConnectionAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.jdbc-connection")
      .contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return "prepareStatement".equals(methodName)
      && descriptor.startsWith("(Ljava/lang/String;")
      && descriptor.endsWith(")Ljava/sql/PreparedStatement;");
  }

  @Override
  public MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    return new ConnectionPrepareStatementVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );
  }

}
