package vom.client.bci.servlet;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

public class HttpServletServiceVisitor extends LocalVariablesSorter {

  private final Label beginTry = new Label();

  private final String className;


  public HttpServletServiceVisitor(int access, String className, String descriptor, MethodVisitor visitor) {
    super(ASM7, access, descriptor, visitor);
    this.className = className;
  }

  @Override
  public void visitCode() {

    // Trover.seize()'s 1st parameter
    mv.visitVarInsn(ALOAD, 1);

    // Trover.seize()'s 2nd parameter
    mv.visitVarInsn(ALOAD, 0);

    TroveExecutor.seize(mv);

    OpcodeUtils.println(mv, "Seized: " + className);

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
    mv.visitVarInsn(ALOAD, 0);

    // Trover.expel()'s 3rd parameter
    mv.visitVarInsn(ALOAD, 1);

    TroveExecutor.expel(mv, true);

    // TODO remove
    OpcodeUtils.print(mv, "<<end>>");
    OpcodeUtils.prePrint(mv);
    mv.visitVarInsn(ALOAD, 0);
    OpcodeUtils.postPrint(mv, "Ljava/lang/Object;");


    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ATHROW);

    mv.visitMaxs(maxStack, maxLocals);
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      // TODO remove
      OpcodeUtils.print(mv, "<<end>>");
      OpcodeUtils.prePrint(mv);
      mv.visitVarInsn(ALOAD, 0);
      OpcodeUtils.postPrint(mv, "Ljava/lang/Object;");

      // Trover.expel()'s 1st parameter
      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);

      // Trover.expel()'s 2nd parameter
      mv.visitVarInsn(ALOAD, 0);

      TroveExecutor.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}
