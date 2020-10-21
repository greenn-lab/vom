package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Type.LONG_TYPE;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class HttpServletServiceMethodVisitor extends LocalVariablesSorter {

  private int varStarted;

  public HttpServletServiceMethodVisitor(MethodVisitor visitor, int access, String descriptor) {
    super(ASM_VERSION, access, descriptor, visitor);
  }

  @Override
  public void visitCode() {

    // Trover.seize()'s 1st parameter
    mv.visitVarInsn(ALOAD, 1);

    // Trover.seize()'s 2nd parameter
    OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
    varStarted = newLocal(LONG_TYPE);
    mv.visitVarInsn(LSTORE, varStarted);
    mv.visitVarInsn(LLOAD, varStarted);

    Trover.seize(mv);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
      OpcodeUtils.invokeSystemCurrentTimeMillis(mv);
      mv.visitVarInsn(LLOAD, varStarted);
      mv.visitInsn(LSUB);

      Trover.expel(mv);
    }

    mv.visitInsn(opcode);
  }

}
