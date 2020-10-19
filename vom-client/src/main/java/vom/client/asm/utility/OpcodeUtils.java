package vom.client.asm.utility;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class OpcodeUtils {

  public static final String SYSTEM_INTERNAL = "java/lang/System";
  public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";
  public static final String CURRENT_TIME_MILLIS_DESC = "()J";


  private OpcodeUtils() {
  }


  public static int loadLocalVariable(Type type) {
    switch (type.getSort()) {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.CHAR:
      case Type.SHORT:
      case Type.INT:
        return Opcodes.ILOAD;
      case Type.FLOAT:
        return Opcodes.FLOAD;
      case Type.LONG:
        return Opcodes.LLOAD;
      case Type.DOUBLE:
        return Opcodes.DLOAD;
      default:
        return Opcodes.ALOAD;
    }
  }

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

}
