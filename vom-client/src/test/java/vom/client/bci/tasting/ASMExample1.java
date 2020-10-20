package vom.client.bci.tasting;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.io.IOException;

public class ASMExample1 implements Opcodes {

  public static byte[] writeClassFile(ClassWriter writer, String name) throws IOException {
    byte[] code = writer.toByteArray();

    FileOutputStream out = new FileOutputStream(name);
    out.write(code);
    out.close();

    return code;
  }

  public static void main(String[] args) throws IOException {
    final String className = "example/HelloASM";
    final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    writer.visit(
        V1_5,
        ACC_PUBLIC,
        className,
        null,
        Type.getInternalName(Object.class),
        null
    );

    MethodVisitor mv =
        writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
        INVOKESPECIAL,
        "java/lang/Object",
        "<init>",
        "()V",
        false
    );
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintWriter;");
    mv.visitLdcInsn("Hello, ASM World!");
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/io/PrintWriter",
        "println",
        "(Ljava/lang/String;)V",
        false
    );
    mv.visitInsn(RETURN);
    mv.visitEnd();

    writeClassFile(writer, "D:/HelloASM.class");
  }

}
