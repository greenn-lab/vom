package vom.client.asm.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.asm.jdbc.trove.DBTrove;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;

public class StatementExecutesVisitor
  extends MethodVisitor
  implements Opcodes {

  public StatementExecutesVisitor(MethodVisitor visitor) {
    super(ASM_VERSION, visitor);
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      mv.visitMethodInsn(
        INVOKESTATIC,
        Type.getInternalName(DBTrove.class),
        "seize",
        "()V",
        false
      );
    }

    mv.visitInsn(opcode);
  }

}
