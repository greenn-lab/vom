package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Type.LONG_TYPE;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class PreparedStatementExecuteVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private int varStarted;


  public PreparedStatementExecuteVisitor(
    int access,
    String descriptor,
    MethodVisitor visitor
  ) {
    super(ASM_VERSION, access, descriptor, visitor);
  }


  @Override
  public void visitCode() {
    OpcodeUtils.invokeSystemCurrentTimeMillis(mv);

    varStarted = newLocal(LONG_TYPE);
    mv.visitVarInsn(LSTORE, varStarted);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if (
      (IRETURN <= opcode && RETURN >= opcode)
      || ATHROW == opcode
    ) {
      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
      mv.visitVarInsn(LLOAD, varStarted);
      mv.visitInsn(LSUB);

      Trover.bring(mv);
    }

    mv.visitInsn(opcode);
  }

}
