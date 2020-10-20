package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import vom.client.bci.trove.Trover;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class ConnectionPrepareStatementVisitor
  extends MethodVisitor
  implements Opcodes {

  public ConnectionPrepareStatementVisitor(MethodVisitor visitor) {
    super(ASM_VERSION, visitor);
  }

  @Override
  public void visitCode() {
    mv.visitVarInsn(ALOAD, 1);
    Trover.query(mv);

    mv.visitCode();
  }

}
