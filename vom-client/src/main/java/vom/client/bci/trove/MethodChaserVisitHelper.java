package vom.client.bci.trove;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.bci.VOMAbstractMethodVisitor;

import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;

public abstract class MethodChaserVisitHelper extends VOMAbstractMethodVisitor {

  public static final Type METHOD_CHASER_TYPE = Type.getType(MethodChaser.class);
  public static final String METHOD_CHASER_INTERNAL =
    Type.getInternalName(MethodChaser.class);
  public static final String METHOD_CHASER_DESCRIPTOR =
    "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V";

  protected int varChase;


  public MethodChaserVisitHelper(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
  }


  protected abstract void setMethodChaserArguments(MethodVisitor mv);

  @Override
  public void visitCode() {
    // new MethodChaser(...)
    mv.visitTypeInsn(NEW, METHOD_CHASER_INTERNAL);
    mv.visitInsn(DUP);

    // MethodChaser's 1st parameter
    mv.visitLdcInsn(className);

    // MethodChaser's 2nd parameter
    mv.visitLdcInsn(methodName);

    // MethodChaser's 3rd parameter
    setMethodChaserArguments(mv);

    mv.visitMethodInsn(
      INVOKESPECIAL,
      METHOD_CHASER_INTERNAL,
      CONSTRUCTOR,
      METHOD_CHASER_DESCRIPTOR,
      false
    );

    varChase = newLocal(METHOD_CHASER_TYPE);
    mv.visitVarInsn(ASTORE, varChase);
    mv.visitVarInsn(ALOAD, varChase);

    TroveExecutor.chase(mv);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if (RETURN >= opcode && IRETURN <= opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      TroveExecutor.close(mv);
    }

    mv.visitInsn(opcode);
  }

}
