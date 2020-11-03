package vom.client.bci.servlet.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMAbstractMethodVisitor;
import vom.client.bci.trove.TroveExecutor;

import static vom.client.bci.trove.JSPChaser.JSP_CHASER_INTERNAL;
import static vom.client.bci.trove.JSPChaser.JSP_CHASER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class ServletJasperJSPVisitor extends VOMAbstractMethodVisitor {

  private int varChase;


  public ServletJasperJSPVisitor(ClassReader reader, MethodVisitor visitor, String methodName, String descriptor) {
    super(reader, visitor, methodName, descriptor);
  }


  @Override
  public void visitCode() {
    // new JSPChaser(...)
    mv.visitTypeInsn(NEW, JSP_CHASER_INTERNAL);
    mv.visitInsn(DUP);

    // JSPChaser's 1st parameter
    mv.visitVarInsn(ALOAD, 3);

    mv.visitMethodInsn(
      INVOKESPECIAL,
      JSP_CHASER_INTERNAL,
      CONSTRUCTOR,
      VOID_STRING,
      false);

    varChase = newLocal(JSP_CHASER_TYPE);
    mv.visitVarInsn(ASTORE, varChase);
    mv.visitVarInsn(ALOAD, varChase);

    TroveExecutor.chase(mv);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      mv.visitVarInsn(ALOAD, varChase);

      TroveExecutor.close(mv);
    }

    mv.visitInsn(opcode);
  }

}
