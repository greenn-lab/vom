package vom.client.bci.tasting.try_catch;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.RETURN;

public class TastingTryCatchMethodVisitor extends MethodVisitor {

  public static final String THROWABLE_NAME = Type.getInternalName(Throwable.class);
  private final Label
    tryStart = new Label(),
    tryEnd = new Label(),
    catchStart = new Label(),
    catchEnd = new Label();

  public TastingTryCatchMethodVisitor(MethodVisitor methodVisitor) {
    super(ASM7, methodVisitor);
  }

  @Override
  public void visitCode() {
    mv.visitCode();

    mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, THROWABLE_NAME);
    mv.visitLabel(tryStart);
    OpcodeUtils.print(mv, "Hello BCI!!");
    OpcodeUtils.print(mv, "are you fine?");
    mv.visitLabel(tryEnd);
    mv.visitJumpInsn(GOTO, catchEnd);
    mv.visitLabel(catchStart);

    mv.visitVarInsn(ASTORE, 1);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);

    OpcodeUtils.print(mv, "in the exception");

    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ATHROW);
    mv.visitLabel(catchEnd);

    mv.visitInsn(RETURN);
  }

  @Override
  public void visitInsn(int opcode) {
    super.visitInsn(opcode);
  }

  @Override
  public void visitEnd() {
    mv.visitEnd();
  }
}
