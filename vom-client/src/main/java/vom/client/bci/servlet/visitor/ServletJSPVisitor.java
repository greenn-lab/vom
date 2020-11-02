package vom.client.bci.servlet.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMAbstractMethodVisitor;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;

import static vom.client.bci.trove.JSPChaser.JSP_CHASER_INTERNAL;
import static vom.client.bci.trove.JSPChaser.JSP_CHASER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class ServletJSPVisitor extends VOMAbstractMethodVisitor {

  private int varChase;


  public ServletJSPVisitor(ClassReader reader, MethodVisitor visitor, String methodName, String descriptor) {
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
      // TODO remove
      OpcodeUtils.print(mv, "<< end >>");
      OpcodeUtils.prePrint(mv);
      mv.visitVarInsn(ALOAD, 0);
      OpcodeUtils.postPrint(mv, "Ljava/lang/Object;");

      mv.visitVarInsn(ALOAD, varChase);
      TroveExecutor.close(mv);
    }

    mv.visitInsn(opcode);
  }

}
