package vom.client.asm.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.asm.jdbc.trove.DBTrove;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;

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
      Type.getInternalName(DBTrove.class),
      "addSql",
      "(Ljava/lang/String;)V",
      false
    );

    mv.visitCode();
  }

}
