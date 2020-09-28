package vom.client.asm;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvokeStatic implements Opcodes {
  
  private static final String VALUE_OF = "valueOf";
  
  
  public static void booleanValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Boolean",
        VALUE_OF,
        "(Z)Ljava/lang/Boolean;",
        false
    );
  }
  
  public static void characterValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Character",
        VALUE_OF,
        "(C)Ljava/lang/Character;",
        false
    );
  }
  
  public static void byteValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Byte",
        VALUE_OF,
        "(B)Ljava/lang/Byte;",
        false
    );
  }
  
  public static void shortValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Short",
        VALUE_OF,
        "(S)Ljava/lang/Short;",
        false
    );
  }
  
  public static void integerValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Integer",
        VALUE_OF,
        "(I)Ljava/lang/Integer;",
        false
    );
  }
  
  public static void floatValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Float",
        VALUE_OF,
        "(F)Ljava/lang/Float;",
        false
    );
  }
  
  public static void longValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Long",
        VALUE_OF,
        "(J)Ljava/lang/Long;",
        false
    );
  }
  
  public static void doubleValueOf(MethodVisitor mv) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Double",
        VALUE_OF,
        "(D)Ljava/lang/Double;",
        false
    );
  }
  
  
}
