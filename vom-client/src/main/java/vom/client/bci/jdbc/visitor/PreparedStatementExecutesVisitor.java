package vom.client.bci.jdbc.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMAbstractMethodVisitor;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Type.LONG_TYPE;

public class PreparedStatementExecutesVisitor
  extends VOMAbstractMethodVisitor {

  private int varStarted;


  public PreparedStatementExecutesVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
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
