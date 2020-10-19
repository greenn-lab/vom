package vom.client.asm.web.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.asm.web.trove.WebTrove;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.utility.OpcodeUtils.CURRENT_TIME_MILLIS;
import static vom.client.asm.utility.OpcodeUtils.CURRENT_TIME_MILLIS_DESC;
import static vom.client.asm.utility.OpcodeUtils.SYSTEM_INTERNAL;

public class HttpServletServiceMethodVisitor extends LocalVariablesSorter {

  private int startIndex;

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

    startIndex = newLocal(Type.LONG_TYPE);
    mv.visitVarInsn(LSTORE, startIndex);

    // 1st parameter
    mv.visitVarInsn(ALOAD, 1);
    // 2nd parameter
    mv.visitVarInsn(LLOAD, startIndex);
    WebTrove.seize(mv);
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
      mv.visitVarInsn(LLOAD, startIndex);
      mv.visitInsn(LSUB);

      WebTrove.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}
