package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Type.LONG_TYPE;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.utility.OpcodeUtils.CURRENT_TIME_MILLIS;
import static vom.client.bci.utility.OpcodeUtils.CURRENT_TIME_MILLIS_DESC;
import static vom.client.bci.utility.OpcodeUtils.SYSTEM_INTERNAL;

public class HttpServletServiceMethodVisitor extends LocalVariablesSorter {

  private int varStarted;

  public HttpServletServiceMethodVisitor(MethodVisitor visitor, int access, String descriptor) {
    super(ASM_VERSION, access, descriptor, visitor);
  }

  @Override
  public void visitCode() {
    mv.visitCode();

    mv.visitMethodInsn(
      INVOKESTATIC,
      SYSTEM_INTERNAL,
      CURRENT_TIME_MILLIS,
      CURRENT_TIME_MILLIS_DESC,
      false
    );

    varStarted = newLocal(LONG_TYPE);
    mv.visitVarInsn(LSTORE, varStarted);

    // 1st parameter
    mv.visitVarInsn(ALOAD, 1);
    // 2nd parameter
    mv.visitVarInsn(LLOAD, varStarted);
    Trover.seize(mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if ((RETURN >= opcode && IRETURN <= opcode)
      || ATHROW == opcode) {
      mv.visitMethodInsn(
        INVOKESTATIC,
        SYSTEM_INTERNAL,
        CURRENT_TIME_MILLIS,
        CURRENT_TIME_MILLIS_DESC,
        false
      );
      mv.visitVarInsn(LLOAD, varStarted);
      mv.visitInsn(LSUB);

      Trover.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}