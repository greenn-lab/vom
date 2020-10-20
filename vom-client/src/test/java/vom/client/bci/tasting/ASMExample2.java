package vom.client.bci.tasting;

import org.objectweb.asm.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;

public class ASMExample2 extends ClassLoader implements Opcodes {

  public static byte[] writeClassFile(ClassWriter writer, String name) throws IOException {
    byte[] code = writer.toByteArray();

    FileOutputStream out = new FileOutputStream(name);
    out.write(code);
    out.close();

    return code;
  }

  public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {
    final String className = "HelloASM";
    final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    writer.visit(
        V1_6,
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
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    mv.visitLdcInsn("Hello, ASM World!");
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "java/io/PrintStream",
        "println",
        "(Ljava/lang/String;)V",
        false
    );
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    mv = writer.visitMethod(
        ACC_PUBLIC + ACC_STATIC,
        "main", "([Ljava/lang/String;)V", null, null
    );
    mv.visitCode();
    mv.visitTypeInsn(NEW, className);
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V", false);
    mv.visitVarInsn(ASTORE, 0);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    final byte[] bytes = writeClassFile(writer, "./vom-client/target/classes/HelloASM.class");

    ASMExample2 ex = new ASMExample2();

    final Class<?> clazz = ex.defineClass(className, bytes, 0, bytes.length);
    clazz.newInstance();
  }

}
