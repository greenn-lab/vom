package vom.client.asm.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_INTERNAL_NAME;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_SEIZE;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_SEIZE_DESC;

public class PreparedStatementExecuteVisitor
  extends MethodVisitor
  implements Opcodes {

  public PreparedStatementExecuteVisitor(MethodVisitor visitor) {
    super(ASM_VERSION, visitor);
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      mv.visitMethodInsn(
        INVOKESTATIC,
        DB_TROVE_INTERNAL_NAME,
        DB_TROVE_SEIZE,
        DB_TROVE_SEIZE_DESC,
        false
      );
    }

    mv.visitInsn(opcode);
  }

}
