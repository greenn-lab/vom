package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import vom.client.bci.trove.Trover;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class ConnectionCommitVisitor
  extends MethodVisitor
  implements Opcodes {

  public ConnectionCommitVisitor(MethodVisitor visitor) {
    super(ASM_VERSION, visitor);
  }

  @Override
  public void visitCode() {
    mv.visitLdcInsn("commit");
    Trover.query(mv);

    mv.visitCode();
  }

}
