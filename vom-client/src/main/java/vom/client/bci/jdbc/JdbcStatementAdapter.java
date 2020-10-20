package vom.client.bci.jdbc;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.bci.ClassWritable;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class JdbcStatementAdapter
  extends ClassVisitor
  implements Opcodes, ClassWritable {

  private static final String CONNECTION_INTERNAL_NAME =
    Type.getInternalName(Connection.class);

  private static final String PREPARED_STATEMENT_INTERNAL_NAME =
    Type.getInternalName(PreparedStatement.class);

  private final ClassReader reader;


  public JdbcStatementAdapter(ClassReader reader) {
    super(ASM_VERSION);

    this.cv = new ClassWriter(
      reader,
      ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
    );

    this.reader = reader;
    this.reader.accept(this, ClassReader.EXPAND_FRAMES);
  }


  @Override
  public byte[] toBytes() {
    return ((ClassWriter) cv).toByteArray();
  }

  @Override
  public MethodVisitor visitMethod(int access, String name,
                                   String descriptor, String signature,
                                   String[] exceptions) {
    final MethodVisitor visitor =
      super.visitMethod(access, name, descriptor, signature, exceptions);

    if (null != visitor
      && (ACC_PUBLIC & access) == 1) {

      if (isConnection()) {
        System.out.printf("........ %s%s%n", name, descriptor);

        if ("commit".equals(name)) {
          return new ConnectionCommitVisitor(visitor);
        }

        if ("rollback".equals(name)) {
          return new ConnectionRollbackVisitor(visitor);
        }

        if (isConnectionPrepareStatement(name, descriptor)) {
          return new ConnectionPrepareStatementVisitor(visitor);
        }
      }

      if (isStatementExecutesMethod(name, descriptor)) {
        return new StatementExecutesVisitor(access, descriptor, visitor);
      }

      if (isPreparedStatement(name)) {
        if (isPreparedStatementExecuteMethod(name, descriptor)) {
          return new PreparedStatementExecuteVisitor(access, descriptor, visitor);
        }
        else if (isPreparedStatementParameterMethod(name, descriptor)) {
          return new PreparedStatementParametersVisitor(
            visitor,
            access,
            descriptor
          );
        }
      }
    }

    return visitor;
  }

  private boolean isConnection() {
    for (String interface_ : reader.getInterfaces()) {
      if (CONNECTION_INTERNAL_NAME.equals(interface_)) {
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

  private boolean isPreparedStatement(String name) {
    for (String interface_ : reader.getInterfaces()) {
      if (PREPARED_STATEMENT_INTERNAL_NAME.equals(interface_)) {
        return true;
      }
    }

    return false;
  }

  private boolean isPreparedStatementParameterMethod(String name, String descriptor) {
    return name.startsWith("set")
      && descriptor.startsWith("(I")
      && descriptor.endsWith(")V");
  }

}
