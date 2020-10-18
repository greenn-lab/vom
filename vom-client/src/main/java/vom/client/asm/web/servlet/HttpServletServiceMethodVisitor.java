package vom.client.asm.web.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.io.PrintStream;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.web.trove.WebTrove.WEB_TROVE_EXPEL;
import static vom.client.asm.web.trove.WebTrove.WEB_TROVE_EXPEL_DESC;
import static vom.client.asm.web.trove.WebTrove.WEB_TROVE_INTERNAL_NAME;

public class HttpServletServiceMethodVisitor extends LocalVariablesSorter {

  public HttpServletServiceMethodVisitor(MethodVisitor visitor, int access, String descriptor) {
    super(ASM_VERSION, access, descriptor, visitor);
  }

  @Override
  public void visitCode() {
//    mv.visitLdcInsn("Hello!!");
//    mv.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out",
//      Type.getDescriptor(PrintStream.class));
//    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class),
//      "println", "(Ljava/lang/String;)V", false);

//    mv.visitVarInsn(Opcodes.ALOAD, 1);
//
//    mv.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out",
//      Type.getDescriptor(PrintStream.class));
//    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class),
//      "println", "(Ljava/lang/Object;)V", false);
//
//    mv.visitMethodInsn(
//      INVOKESTATIC,
//      WEB_TROVE_INTERNAL_NAME,
//      WEB_TROVE_SEIZE,
//      WEB_TROVE_SEIZE_DESC,
//      false);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
      mv.visitMethodInsn(
        INVOKESTATIC,
        WEB_TROVE_INTERNAL_NAME,
        WEB_TROVE_EXPEL,
        WEB_TROVE_EXPEL_DESC,
        false);
    }

    mv.visitInsn(opcode);
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    super.visitMaxs(0, 0);
  }
}
