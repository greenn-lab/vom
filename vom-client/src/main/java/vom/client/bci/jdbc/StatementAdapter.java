package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import static vom.client.bci.utility.OpcodeUtils.isAbleToAssign;

public class StatementAdapter extends VOMClassVisitAdapter {

  private static final List<? extends Class<?>> DEFAULT_JDBC_CLASSES =
    Arrays.asList(
      Connection.class,
      java.sql.Statement.class,
      PreparedStatement.class,
      java.sql.CallableStatement.class
    );

  public StatementAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    if (!containsDatabaseVendor()) return false;

    return isAbleToAssignForJdbcClass();
  }

  @Override
  public MethodVisitor visitMethod(
    int access,
    String name,
    String descriptor,
    String signature,
    String[] exceptions
  ) {
    final MethodVisitor visitor =
      cv.visitMethod(access, name, descriptor, signature, exceptions);

    if (null == visitor) return null;

    if ("commit".equals(name) || "rollback".equals(name)) {
      return new ConnectionCommitRollbackVisitor(access, name, descriptor, visitor);
    }

    if (isConnectionPrepareStatement(name, descriptor)) {
      return new ConnectionPrepareStatementVisitor(access, descriptor, visitor);
    }

    if (isStatementExecutesMethod(name, descriptor)) {
      return new StatementExecutesVisitor(access, descriptor, visitor);
    }

    if (isAbleToAssign(className, java.sql.PreparedStatement.class)) {
      if (isPreparedStatementExecuteMethod(name, descriptor)) {
        return new PreparedStatementExecuteVisitor(access, descriptor, visitor);
      }

      if (isPreparedStatementParameterMethod(name, descriptor)) {
        return new PreparedStatementParametersVisitor(
          visitor,
          access,
          descriptor
        );
      }
    }

    return visitor;
  }

  private boolean isAbleToAssignForJdbcClass() {
    try {
      final Class<?> clazz = Class.forName(
        className.replace('/', '.'),
        true,
        Thread.currentThread().getContextClassLoader());

      if (clazz.isInterface()) return false;

      for (Class<?> jdbcClass : DEFAULT_JDBC_CLASSES) {
        if (jdbcClass.isAssignableFrom(clazz)) {
          return true;
        }
      }
    }
    catch (Exception e) {
      // no work
    }


    return false;
  }

  private boolean containsDatabaseVendor() {
    for (final String vendor : Config.databaseVendors) {
      if (className.contains(vendor)) {
        return true;
      }
    }

    return false;
  }

  private boolean isConnectionPrepareStatement(String name, String descriptor) {
    return "prepareStatement".equals(name)
      && descriptor.startsWith("(Ljava/lang/String;")
      && descriptor.endsWith(")Ljava/sql/PreparedStatement;");
  }

  private boolean isStatementExecutesMethod(String name, String descriptor) {
    // Statement#execute(sql: String, ...)
    // Statement#executeLargeUpdate(sql: String, ...)
    // Statement#executeQuery(sql: String)
    // Statement#executeUpdate(sql: String, ...)
    // Statement#addBatch(sql: String)
    return (name.startsWith("execute") || ("addBatch".equals(name)))
      && descriptor.startsWith("(Ljava/lang/String;");
  }

  private boolean isPreparedStatementExecuteMethod(String name, String descriptor) {
    return (
      "execute".equals(name)
        || "executeLargeUpdate".equals(name)
        || "executeQuery".equals(name)
        || "executeUpdate".equals(name)
    ) && descriptor.startsWith("()");
  }

  private boolean isPreparedStatementParameterMethod(String name, String descriptor) {
    return name.startsWith("set")
      && descriptor.startsWith("(I")
      && descriptor.endsWith(")V");
  }

}
