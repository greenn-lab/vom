package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.trove.BootyInChasing.BOOTY_CONSTRUCTOR_DESC;
import static vom.client.bci.trove.BootyInChasing.BOOTY_INTERNAL;
import static vom.client.bci.trove.BootyInChasing.BOOTY_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.argumentsToObjectArray;
import static vom.client.bci.utility.OpcodeUtils.print;

public class HttpServletChaserMethodVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private static final Type ARGUMENTS_TYPE = Type.getType(Object[].class);

  private final String className;
  private final String methodName;
  private final Type[] arguments;

  private int varChase;


  public HttpServletChaserMethodVisitor(
    MethodVisitor visitor,
    int access,
    String className,
    String methodName,
    String descriptor
  ) {
    super(ASM_VERSION, access, descriptor, visitor);

    this.className = className;
    this.methodName = methodName;
    this.arguments = Type.getArgumentTypes(descriptor);
  }


  @Override
  public void visitCode() {
    // new BootyInChasing(...)
    mv.visitTypeInsn(NEW, BOOTY_INTERNAL);
    mv.visitInsn(DUP);

    // BootyInChasing's 1st parameter
    mv.visitLdcInsn(className);

    // BootyInChasing's 2nd parameter
    mv.visitLdcInsn(methodName);

    // BootyInChasing's 3rd parameter
    argumentsToObjectArray(
      mv,
      arguments,
      newLocal(ARGUMENTS_TYPE)
    );

    // execute BootyInChasing constructor
    mv.visitMethodInsn(
      INVOKESPECIAL,
      BOOTY_INTERNAL,
      CONSTRUCTOR,
      BOOTY_CONSTRUCTOR_DESC,
      false
    );

    varChase = newLocal(BOOTY_TYPE);
    mv.visitVarInsn(ASTORE, varChase);

    mv.visitVarInsn(ALOAD, varChase);
    Trover.chase(mv);

    print(mv, "Chased: " + className + "#" + methodName);

    mv.visitCode();

  }

  @Override
  public void visitInsn(int opcode) {
    if ((RETURN >= opcode && IRETURN <= opcode)
      || ATHROW == opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      Trover.bring(mv);
    }

    mv.visitInsn(opcode);
  }

}
