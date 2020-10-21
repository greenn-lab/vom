package vom.client.bci.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.LLOAD;
import static vom.client.bci.utility.PrimitiveTypes.OBJECT_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpcodeUtils {

  public static final String SYSTEM_INTERNAL =
    Type.getInternalName(System.class);
  public static final String CONSTRUCTOR = "<init>";
  public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";
  public static final String CURRENT_TIME_MILLIS_DESC = "()J";

  public static final String VOID_LONG = "(J)V";
  public static final String VOID_OBJECT = "(Ljava/lang/Object;)V";
  public static final String VOID_THROWS = "(Ljava/lang/Throwable;)V";


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
  public static void print(MethodVisitor mv, String str) {
    mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      SYSTEM_INTERNAL,
      "out",
      "Ljava/io/PrintStream;"
    );
    mv.visitLdcInsn(str);
    mv.visitMethodInsn(
      Opcodes.INVOKEVIRTUAL,
      "java/io/PrintStream",
      "println",
      "(Ljava/lang/String;)V",
      false
    );
  }

  @SuppressWarnings("unused")
  public static void prePrint(MethodVisitor mv) {
    mv.visitFieldInsn(
      Opcodes.GETSTATIC,
      SYSTEM_INTERNAL,
      "out",
      "Ljava/io/PrintStream;"
    );
  }

  @SuppressWarnings("unused")
  public static void postPrint(MethodVisitor mv, String argumentType) {
    mv.visitMethodInsn(
      Opcodes.INVOKEVIRTUAL,
      "java/io/PrintStream",
      "println",
      "(" + argumentType + ")V",
      false
    );
  }


}
