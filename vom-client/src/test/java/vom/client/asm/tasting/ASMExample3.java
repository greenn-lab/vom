package vom.client.asm.tasting;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ASMExample3 extends ClassLoader implements Opcodes {
  
  public static byte[] writeClassFile(ClassWriter writer, String name) throws IOException {
    byte[] code = writer.toByteArray();
    
    FileOutputStream out = new FileOutputStream(name);
    out.write(code);
    out.close();
    
    return code;
  }
  
  public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
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
    
    
    //
    mv = writer.visitMethod(
        ACC_PUBLIC,
        "assignStr",
        "()V",
        null, null
    );
    mv.visitCode();
    mv.visitLdcInsn("Hi~");
    mv.visitVarInsn(ASTORE, 1);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    
    //
    mv = writer.visitMethod(
        ACC_PUBLIC,
        "assignInt",
        "()V",
        null, null
    );
    mv.visitCode();
    mv.visitInsn(ICONST_0); // 음수 1 (minus 1) 을 constraint pool(opstack) 에 push
    mv.visitVarInsn(ISTORE, 1);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    
    //
    mv = writer.visitMethod(
        ACC_PUBLIC,
        "assignLDCInt",
        "()V",
        null, null
    );
    mv.visitCode();
    mv.visitLdcInsn(56001);
    mv.visitVarInsn(ISTORE, 1);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    
    //
    mv = writer.visitMethod(
        ACC_PUBLIC,
        "assignObjectArray",
        "(Ljava/lang/String;IJ)V",
        null, null
    );
    mv.visitCode();
    mv.visitIntInsn(BIPUSH, 3);
    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
    mv.visitVarInsn(ASTORE, 4);
  
    mv.visitVarInsn(ALOAD, 4);
    mv.visitIntInsn(BIPUSH, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(AASTORE);
    
    mv.visitVarInsn(ALOAD, 4);
    mv.visitIntInsn(BIPUSH, 1);
    mv.visitVarInsn(ILOAD, 2);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
    mv.visitInsn(AASTORE);
    
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    
    final byte[] bytes = writeClassFile(writer, "./vom-client/target/classes/HelloASM.class");
    
    ASMExample3 ex = new ASMExample3();
    
    final Class<?> clazz = ex.defineClass(className, bytes, 0, bytes.length);
    final Object obj = clazz.newInstance();
  
    clazz.getMethod("assignStr").invoke(obj);
  }
  
}
