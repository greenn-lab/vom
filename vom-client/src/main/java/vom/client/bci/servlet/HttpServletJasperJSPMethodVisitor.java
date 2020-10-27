package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;
import vom.client.bci.utility.OpcodeUtils;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.trove.PaperInChasing.PAPER_INTERNAL;
import static vom.client.bci.trove.PaperInChasing.PAPER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class HttpServletJasperJSPMethodVisitor extends LocalVariablesSorter {

  private final String className;
  private int varChase;


  public HttpServletJasperJSPMethodVisitor(int access, String className, String descriptor, MethodVisitor visitor) {
    super(ASM_VERSION, access, descriptor, visitor);
    this.className = className;
  }

  @Override
  public void visitCode() {
    // new PaperInChasing()
    mv.visitTypeInsn(NEW, PAPER_INTERNAL);
    mv.visitInsn(DUP);

    // PaperInChasing's 1st parameter
    mv.visitVarInsn(ALOAD, 3);

    mv.visitMethodInsn(
      INVOKESPECIAL,
      PAPER_INTERNAL,
      CONSTRUCTOR,
      VOID_STRING,
      false);

    varChase = newLocal(PAPER_TYPE);
    mv.visitVarInsn(ASTORE, varChase);
    mv.visitVarInsn(ALOAD, varChase);

    Trover.chase(mv);

    OpcodeUtils.print(mv, "JSP Chased: " + className);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      Trover.bring(mv);
    }

    mv.visitInsn(opcode);
  }

}
