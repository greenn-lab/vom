package vom.client.bci.servlet;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trove;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Type.LONG_TYPE;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class HttpServletServiceVisitor extends LocalVariablesSorter {

  private final Label beginTry = new Label();

  private final String className;

  private int varStarted;


  public HttpServletServiceVisitor(int access, String className, String descriptor, MethodVisitor visitor) {
    super(ASM_VERSION, access, descriptor, visitor);
    this.className = className;
  }

  @Override
  public void visitCode() {
    OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
    varStarted = newLocal(LONG_TYPE);
    mv.visitVarInsn(LSTORE, varStarted);

    // Trover.seize()'s 1st parameter
    mv.visitVarInsn(LLOAD, varStarted);

    // Trover.seize()'s 2nd parameter
    mv.visitVarInsn(ALOAD, 1);

    // Trover.seize()'s 3rd parameter
    mv.visitVarInsn(ALOAD, 0);

    Trove.seize(mv);

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
    mv.visitVarInsn(LLOAD, varStarted);
    mv.visitInsn(LSUB);

    // Trover.expel()'s 3rd parameter
    mv.visitVarInsn(ALOAD, 0);

    // Trover.expel()'s 3rd parameter
    mv.visitVarInsn(ALOAD, 1);

    Trove.expel(mv, true);

    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ATHROW);

    mv.visitMaxs(maxStack, maxLocals);
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {

      // Trover.expel()'s 1st parameter
      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
      mv.visitVarInsn(LLOAD, varStarted);
      mv.visitInsn(LSUB);

      // Trover.expel()'s 2nd parameter
      mv.visitVarInsn(ALOAD, 0);

      Trove.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}
