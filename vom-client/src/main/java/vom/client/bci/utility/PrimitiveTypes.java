package vom.client.bci.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static vom.client.bci.utility.OpcodeUtils.BOOLEAN_NAME;
import static vom.client.bci.utility.OpcodeUtils.BYTE_NAME;
import static vom.client.bci.utility.OpcodeUtils.CHARACTER_NAME;
import static vom.client.bci.utility.OpcodeUtils.DOUBLE_NAME;
import static vom.client.bci.utility.OpcodeUtils.FLOAT_NAME;
import static vom.client.bci.utility.OpcodeUtils.INTEGER_NAME;
import static vom.client.bci.utility.OpcodeUtils.LONG_NAME;
import static vom.client.bci.utility.OpcodeUtils.SHORT_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrimitiveTypes implements Opcodes {

  private static final String VALUE_OF = "valueOf";


  public static void booleanValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      BOOLEAN_NAME,
      VALUE_OF,
      "(Z)Ljava/lang/Boolean;",
      false
    );
  }

  public static void characterValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      CHARACTER_NAME,
      VALUE_OF,
      "(C)Ljava/lang/Character;",
      false
    );
  }

  public static void byteValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      BYTE_NAME,
      VALUE_OF,
      "(B)Ljava/lang/Byte;",
      false
    );
  }

  public static void shortValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      SHORT_NAME,
      VALUE_OF,
      "(S)Ljava/lang/Short;",
      false
    );
  }

  public static void integerValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTEGER_NAME,
      VALUE_OF,
      "(I)Ljava/lang/Integer;",
      false
    );
  }

  public static void floatValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      FLOAT_NAME,
      VALUE_OF,
      "(F)Ljava/lang/Float;",
      false
    );
  }

  public static void longValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      LONG_NAME,
      VALUE_OF,
      "(J)Ljava/lang/Long;",
      false
    );
  }

  public static void doubleValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      DOUBLE_NAME,
      VALUE_OF,
      "(D)Ljava/lang/Double;",
      false
    );
  }

  public static void valueOf(Type type, MethodVisitor mv) {
    if (Type.BOOLEAN_TYPE == type) PrimitiveTypes.byteValueOf(mv);
    else if (Type.CHAR_TYPE == type) PrimitiveTypes.characterValueOf(mv);
    else if (Type.BYTE_TYPE == type) PrimitiveTypes.byteValueOf(mv);
    else if (Type.SHORT_TYPE == type) PrimitiveTypes.shortValueOf(mv);
    else if (Type.INT_TYPE == type) PrimitiveTypes.integerValueOf(mv);
    else if (Type.FLOAT_TYPE == type) PrimitiveTypes.floatValueOf(mv);
    else if (Type.LONG_TYPE == type) PrimitiveTypes.longValueOf(mv);
    else if (Type.DOUBLE_TYPE == type) PrimitiveTypes.doubleValueOf(mv);
  }


}
