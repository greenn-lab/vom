package vom.client.asm.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_ADD_SQL;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_ADD_SQL_DESC;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_INTERNAL_NAME;

public class ConnectionPrepareStatementVisitor
  extends MethodVisitor
  implements Opcodes {

  public ConnectionPrepareStatementVisitor(MethodVisitor visitor) {
    super(ASM_VERSION, visitor);
  }

  @Override
  public void visitCode() {
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(
      INVOKESTATIC,
      DB_TROVE_INTERNAL_NAME,
      DB_TROVE_ADD_SQL,
      DB_TROVE_ADD_SQL_DESC,
      false
    );

    mv.visitCode();
  }

}
