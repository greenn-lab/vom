package vom.client.bci.jdbc.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Type.LONG_TYPE;

public class PreparedStatementExecutesVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private int varStarted;


  public PreparedStatementExecutesVisitor(
    int access,
    String descriptor,
    MethodVisitor visitor
  ) {
    super(ASM7, access, descriptor, visitor);
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
    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
      mv.visitVarInsn(LLOAD, varStarted);
      TroveExecutor.taken(mv);
    }

    mv.visitInsn(opcode);
  }

}
