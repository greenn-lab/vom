package vom.client.bci.servlet.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import vom.client.bci.AbstractMethodVisitor;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;

public class HttpServletServiceVisitor extends AbstractMethodVisitor {

  private final Label beginTry = new Label();


  public HttpServletServiceVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
  }


  @Override
  public void visitCode() {
    // Trover.seize()'s 1st parameter
    mv.visitVarInsn(ALOAD, 1);

    // Trover.seize()'s 2nd parameter
    mv.visitVarInsn(ALOAD, 0);

    TroveExecutor.seize(mv);

    mv.visitLabel(beginTry);
    mv.visitCode();
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    final Label tryEnd = new Label();
    mv.visitTryCatchBlock(beginTry, tryEnd, tryEnd, null);
    mv.visitLabel(tryEnd);
    mv.visitVarInsn(ASTORE, 1);

    // Trover.expel()'s 1st parameter
    OpcodeUtils.invokeSystemCurrentTimeMillis(mv);

    // Trover.expel()'s 2nd parameter
    mv.visitVarInsn(ALOAD, 1);

    // Trover.expel()'s 3rd parameter
    mv.visitVarInsn(ALOAD, 0);

    TroveExecutor.expel(mv);

    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ATHROW);

    mv.visitMaxs(maxStack, maxLocals);
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      // Trover.expel()'s 1st parameter
      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);

      // Trover.expel()'s 2nd parameter
      mv.visitVarInsn(ALOAD, 1);

      // Trover.expel()'s 3rd parameter
      mv.visitVarInsn(ALOAD, 0);

      TroveExecutor.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}
