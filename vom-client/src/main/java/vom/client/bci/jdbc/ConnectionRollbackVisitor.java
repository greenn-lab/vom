package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import vom.client.bci.trove.Trover;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class ConnectionRollbackVisitor
  extends MethodVisitor
  implements Opcodes {

  public ConnectionRollbackVisitor(MethodVisitor visitor) {
    super(ASM_VERSION, visitor);
  }

  @Override
  public void visitCode() {
    mv.visitLdcInsn("rollback");
    Trover.query(mv);

    mv.visitCode();
  }

}
