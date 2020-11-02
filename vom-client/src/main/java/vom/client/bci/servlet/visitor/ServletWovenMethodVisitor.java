package vom.client.bci.servlet.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.TroveExecutor;

import static vom.client.bci.trove.MethodChaser.METHOD_CHASER_DESCRIPTOR;
import static vom.client.bci.trove.MethodChaser.METHOD_CHASER_INTERNAL;
import static vom.client.bci.trove.MethodChaser.METHOD_CHASER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.argumentsToObjectArray;

public class ServletWovenMethodVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private static final Type ARGUMENTS_TYPE = Type.getType(Object[].class);

  private final String className;
  private final String methodName;
  private final Type[] arguments;

  private int varChase;


  public ServletWovenMethodVisitor(
    MethodVisitor visitor,
    int access,
    String className,
    String methodName,
    String descriptor
  ) {
    super(ASM7, access, descriptor, visitor);

    this.className = className;
    this.methodName = methodName;
    this.arguments = Type.getArgumentTypes(descriptor);
  }


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
    argumentsToObjectArray(
      mv,
      arguments,
      newLocal(ARGUMENTS_TYPE)
    );

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