package vom.client.asm.web.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.asm.utility.PrimitiveTypes;
import vom.client.asm.web.trove.WebTrove;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.utility.OpcodeUtils.CURRENT_TIME_MILLIS;
import static vom.client.asm.utility.OpcodeUtils.CURRENT_TIME_MILLIS_DESC;
import static vom.client.asm.utility.OpcodeUtils.SYSTEM_INTERNAL;
import static vom.client.asm.utility.PrimitiveTypes.OBJECT_NAME;

public class HttpServletChaserMethodVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private final String className;
  private final String methodName;
  private final Type[] arguments;

  private int elapsedIndex;


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
    mv.visitCode();

    mv.visitMethodInsn(
      INVOKESTATIC,
      SYSTEM_INTERNAL,
      CURRENT_TIME_MILLIS,
      CURRENT_TIME_MILLIS_DESC,
      false
    );

    elapsedIndex = newLocal(Type.LONG_TYPE);
    mv.visitVarInsn(LSTORE, elapsedIndex);
  }

  @Override
  public void visitInsn(int opcode) {
    if ((RETURN >= opcode && IRETURN <= opcode)
      || ATHROW == opcode) {
      // 1st parameter
      mv.visitLdcInsn(className);
      // 2nd parameter
      mv.visitLdcInsn(methodName);
      // 3rd parameter
      mv.visitMethodInsn(
        INVOKESTATIC,
        SYSTEM_INTERNAL,
        CURRENT_TIME_MILLIS,
        CURRENT_TIME_MILLIS_DESC,
        false
      );
      mv.visitVarInsn(LLOAD, elapsedIndex);
      mv.visitInsn(LSUB);

      // from 4th parameters
      chasingParameters(
        mv,
        arguments,
        newLocal(Type.getType(Object[].class))
      );

      WebTrove.chase(mv);
    }

    super.visitInsn(opcode);
  }

  private void chasingParameters(
    MethodVisitor mv,
    Type[] arguments,
    int arrayIndex
  ) {
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
  }

}
