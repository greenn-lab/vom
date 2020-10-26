package vom.client.bci.servlet;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.INVOKEDYNAMIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Type.LONG_TYPE;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class HttpServletServiceMethodVisitor extends LocalVariablesSorter {

  private final Label beginTry = new Label();

  private int varStarted;

  public HttpServletServiceMethodVisitor(MethodVisitor visitor, int access, String descriptor) {
    super(ASM_VERSION, access, descriptor, visitor);
  }

  @Override
  public void visitCode() {

    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
      INVOKEVIRTUAL,
      "java/lang/Object",
      "getClass",
      "()Ljava/lang/Class;",
      false
    );
    mv.visitMethodInsn(
      INVOKEVIRTUAL,
      "java/lang/Class",
      "getClassLoader",
      "()Ljava/lang/ClassLoader;",
      false
    );
    final int i = newLocal(Type.getType(ClassLoader.class));
    mv.visitVarInsn(ASTORE, i);

    OpcodeUtils.prePrint(mv);
    mv.visitVarInsn(ALOAD, i);
    OpcodeUtils.postPrint(mv, "Ljava/lang/Object;");



    // Trover.seize()'s 1st parameter
//    mv.visitVarInsn(ALOAD, 1);

    // Trover.seize()'s 2nd parameter
//    OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
//    Trover.seize(mv);

//    mv.visitLabel(beginTry);
    mv.visitCode();

    OpcodeUtils.print(mv, "whyyyyyyyyyyyyyyyyyyyyyyyy");

  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
//    final Label tryEnd = new Label();
//    mv.visitTryCatchBlock(beginTry, tryEnd, tryEnd, null);
//    mv.visitLabel(tryEnd);
//    mv.visitVarInsn(ASTORE, 1);
//
//    // Trover.expel()'s 1st parameter
//    OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
//    mv.visitVarInsn(LLOAD, varStarted);
//    mv.visitInsn(LSUB);
//
//    // Trover.expel()'s 2nd parameter
//    mv.visitVarInsn(ALOAD, 1);
//
//    Trover.expel(mv, true);
//
//    mv.visitVarInsn(ALOAD, 1);
//    mv.visitInsn(ATHROW);
//
    mv.visitMaxs(maxStack + 8, maxLocals + 8);
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
//      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
//      mv.visitVarInsn(LLOAD, varStarted);
//      mv.visitInsn(LSUB);

      mv.visitLdcInsn(123L);
      Trover.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}
