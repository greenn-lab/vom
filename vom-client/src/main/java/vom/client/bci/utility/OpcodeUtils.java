package vom.client.bci.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.PrintStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpcodeUtils implements Opcodes {

  public static final String OBJECT_NAME = Type.getInternalName(Object.class);
  public static final String STRING_NAME = Type.getInternalName(String.class);
  public static final String BOOLEAN_NAME = Type.getInternalName(Boolean.class);
  public static final String CHARACTER_NAME =
    Type.getInternalName(Character.class);
  public static final String BYTE_NAME = Type.getInternalName(Byte.class);
  public static final String SHORT_NAME = Type.getInternalName(Short.class);
  public static final String INTEGER_NAME = Type.getInternalName(Integer.class);
  public static final String FLOAT_NAME = Type.getInternalName(Float.class);
  public static final String LONG_NAME = Type.getInternalName(Long.class);
  public static final String DOUBLE_NAME = Type.getInternalName(Double.class);
  public static final String SYSTEM_INTERNAL =
    Type.getInternalName(System.class);

  public static final String CONSTRUCTOR = "<init>";
  public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";
  public static final String CURRENT_TIME_MILLIS_DESC = "()J";

  public static final String VOID_NONE = "()V";
  public static final String VOID_LONG = "(J)V";
  public static final String VOID_OBJECT = "(L" + OBJECT_NAME + ";)V";
  public static final String VOID_STRING = "(L" + STRING_NAME + ";)V";


  public static void invokeSystemCurrentTimeMillis(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      SYSTEM_INTERNAL,
      CURRENT_TIME_MILLIS,
      CURRENT_TIME_MILLIS_DESC,
      false
    );
  }

  public static int loadLocalVariable(Type type) {
    switch (type.getSort()) {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.CHAR:
      case Type.SHORT:
      case Type.INT:
        return ILOAD;
      case Type.FLOAT:
        return FLOAD;
      case Type.LONG:
        return LLOAD;
      case Type.DOUBLE:
        return DLOAD;
      default:
        return ALOAD;
    }
  }

  public static void argumentsToObjectArray(
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


  ///// for debugging
  @SuppressWarnings("unused")
  public static void println(MethodVisitor mv, String str) {
    mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      SYSTEM_INTERNAL,
      "out",
      Type.getDescriptor(PrintStream.class)
    );
    mv.visitLdcInsn(str);
    mv.visitMethodInsn(
      Opcodes.INVOKEVIRTUAL,
      Type.getInternalName(PrintStream.class),
      "println",
      VOID_STRING,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void print(MethodVisitor mv, String str) {
    mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      SYSTEM_INTERNAL,
      "out",
      Type.getDescriptor(PrintStream.class)
    );
    mv.visitLdcInsn(str);
    mv.visitMethodInsn(
      Opcodes.INVOKEVIRTUAL,
      Type.getInternalName(PrintStream.class),
      "print",
      VOID_STRING,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void prePrint(MethodVisitor mv) {
    mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      SYSTEM_INTERNAL,
      "out",
      Type.getDescriptor(PrintStream.class)
    );
  }

  @SuppressWarnings("unused")
  public static void postPrint(MethodVisitor mv, String argumentType) {
    mv.visitMethodInsn(
      Opcodes.INVOKEVIRTUAL,
      Type.getInternalName(PrintStream.class),
      "println",
      "(" + argumentType + ")V",
      false
    );
  }

}
