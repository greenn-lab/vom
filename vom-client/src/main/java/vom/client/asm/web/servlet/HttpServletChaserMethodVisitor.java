package vom.client.asm.web.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.asm.utility.PrimitiveTypes;

import java.util.Arrays;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.utility.PrimitiveTypes.OBJECT_NAME;

public class HttpServletChaserMethodVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private static final String INTERNAL_NAME =
    Type.getInternalName(HttpServletChaserMethodVisitor.class);

  private final String classAndMethod;
  private final Type[] arguments;

  public HttpServletChaserMethodVisitor(
    MethodVisitor visitor,
    int access,
    String className,
    String methodName,
    String descriptor
  ) {
    super(ASM_VERSION, access, descriptor, visitor);

    this.classAndMethod = className + "#" + methodName;
    this.arguments = Type.getArgumentTypes(descriptor);
  }

  @Override
  public void visitCode() {
    invokeMethodSwipe(
      mv,
      classAndMethod,
      arguments,
      newLocal(Type.getType(Object[].class))
    );

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    mv.visitInsn(opcode);

    if (RETURN >= opcode && IRETURN <= opcode) {
      mv.visitMaxs(0, 0);
      mv.visitEnd();
    }
  }

  private void invokeMethodSwipe(MethodVisitor mv, String classAndMethod, Type[] arguments, int arrayIndex) {
    mv.visitLdcInsn(classAndMethod);

    if (0 == arguments.length) {
      mv.visitInsn(ACONST_NULL);
    }
    else {
      mv.visitIntInsn(BIPUSH, arguments.length);
      mv.visitTypeInsn(ANEWARRAY, OBJECT_NAME);
      mv.visitVarInsn(ASTORE, arrayIndex);

      int index = 1;
      int arrayPoint = 0;
      for (final Type argument : arguments) {
        mv.visitVarInsn(ALOAD, arrayIndex);
        mv.visitIntInsn(BIPUSH, arrayPoint++);

        switch (argument.getSort()) {
          case Type.BOOLEAN:
            mv.visitVarInsn(ILOAD, index);
            PrimitiveTypes.booleanValueOf(mv);
            break;
          case Type.CHAR:
            mv.visitVarInsn(ILOAD, index);
            PrimitiveTypes.characterValueOf(mv);
            break;
          case Type.BYTE:
            mv.visitVarInsn(ILOAD, index);
            PrimitiveTypes.byteValueOf(mv);
            break;
          case Type.SHORT:
            mv.visitVarInsn(ILOAD, index);
            PrimitiveTypes.shortValueOf(mv);
            break;
          case Type.INT:
            mv.visitVarInsn(ILOAD, index);
            PrimitiveTypes.integerValueOf(mv);
            break;
          case Type.FLOAT:
            mv.visitVarInsn(FLOAD, index);
            PrimitiveTypes.floatValueOf(mv);
            break;
          case Type.LONG:
            mv.visitVarInsn(LLOAD, index);
            PrimitiveTypes.longValueOf(mv);
            break;
          case Type.DOUBLE:
            mv.visitVarInsn(DLOAD, index);
            PrimitiveTypes.doubleValueOf(mv);
            break;
          default:
            mv.visitVarInsn(ALOAD, index);
        }

        mv.visitInsn(AASTORE);

        index += argument.getSize();
      }

      mv.visitVarInsn(ALOAD, arrayIndex);
    }

    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      "swipe",
      "(Ljava/lang/String;[Ljava/lang/Object;)V",
      false
    );
  }

}
