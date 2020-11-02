package vom.client.bci.jdbc;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.VOMClientTransformer;
import vom.client.bci.jdbc.visitor.ConnectionPrepareStatementVisitor;
import vom.client.bci.jdbc.visitor.PreparedStatementExecutesVisitor;
import vom.client.bci.jdbc.visitor.PreparedStatementParametersVisitor;

import java.io.File;
import java.util.List;

public class JdbcAdapter extends VOMClassVisitAdapter {

  private static final List<String> connectionList =
    Config.getList("jdbc.connection");

  private static final List<String> statementList =
    Config.getList("jdbc.statement");

  private static final List<String> preparedList =
    Config.getList("jdbc.prepared");


  public JdbcAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return connectionList.contains(className)
      || statementList.contains(className)
      || preparedList.contains(className);
  }

  @Override
  public byte[] toBytes() {
    final ClassReader reader = new ClassReader(buffer);
    final ClassWriter writer = new ClassWriter(
      reader,
      ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
    );

    super.cv = writer;

    reader.accept(this, ClassReader.EXPAND_FRAMES);

    String out = "D:/vom-classes/" + className.replace('.', '/') + ".class";
    File outFile = new File(out);

    new File(outFile.getParent()).mkdirs();

    VOMClientTransformer.writeTastingClassfile(writer.toByteArray(), out);

    return writer.toByteArray();
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

    /*if (connectionList.contains(className)
      && ("commit".equals(name)
      || "rollback".equals(name))) {
      return new ConnectionCommitRollbackVisitor(access, name, descriptor, visitor);
    }*/

    if (connectionList.contains(className)
      && "prepareStatement".equals(name)
      && descriptor.startsWith("(Ljava/lang/String;")
      && descriptor.endsWith(")Ljava/sql/PreparedStatement;")) {
      return new ConnectionPrepareStatementVisitor(access, descriptor, visitor);
    }

//    if ((statementList.contains(className) || preparedList.contains(className))
//      && (name.startsWith("execute") || "addBatch".equals(name))
//      && descriptor.startsWith("(Ljava/lang/String;")) {
//      return new StatementExecutesVisitor(access, descriptor, visitor);
//    }

    if (preparedList.contains(className)
      && descriptor.startsWith("()")
      && ("execute".equals(name)
      || "executeLargeUpdate".equals(name)
      || "executeQuery".equals(name)
      || "executeUpdate".equals(name))
    ) {
      return new PreparedStatementExecutesVisitor(access, descriptor, visitor);
    }

    if (preparedList.contains(className)
      && name.startsWith("set")
      && descriptor.startsWith("(I")
      && descriptor.endsWith(")V")) {
      return new PreparedStatementParametersVisitor(visitor, access, descriptor);
    }

    return visitor;
  }

}
