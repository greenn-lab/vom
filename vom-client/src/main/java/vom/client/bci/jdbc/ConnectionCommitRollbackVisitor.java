package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import vom.client.bci.trove.Trover;
import vom.client.bci.utility.OpcodeUtils;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class ConnectionCommitRollbackVisitor
  extends MethodVisitor
  implements Opcodes {

  private String methodName;


  public ConnectionCommitRollbackVisitor(
    String methodName,
    MethodVisitor visitor
  ) {
    super(ASM_VERSION, visitor);
    this.methodName = methodName;
  }


  @Override
  public void visitCode() {
    mv.visitLdcInsn(methodName);
    Trover.query(mv);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
//      mv.visitLdcInsn(-1L);
      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
      Trover.bring(mv);
    }

    mv.visitInsn(opcode);
  }
}
