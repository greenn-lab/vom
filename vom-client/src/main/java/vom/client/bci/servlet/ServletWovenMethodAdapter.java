package vom.client.bci.servlet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.Config;
import vom.client.bci.ClassWritable;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;

public class ServletWovenMethodAdapter extends ClassVisitor implements ClassWritable, Opcodes {

  private final String className;

  public ServletWovenMethodAdapter(byte[] classfileBuffer, String className) {
    super(ASM_VERSION);

    final ClassReader reader = new ClassReader(classfileBuffer);
    this.cv = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

    this.className = className;

    reader.accept(this, EXPAND_FRAMES);
  }

  @Override
  public byte[] toBytes() {
    return ((ClassWriter) cv).toByteArray();
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

    if (null != visitor
      && ACC_PUBLIC == access
      && !CONSTRUCTOR.equals(name)
      && !isGetterOrSetter(name, descriptor)
      && !isObjectMethods(name, descriptor)
    ) {
      if (Config.isDebugMode()) {
        System.out.printf(
          "entangled to vom { %s#%s } under arrest%n",
          className,
          name
        );
      }

      visitor = new ServletWovenMethodVisitor(
        visitor,
        access,
        className,
        name,
        descriptor
      );
    }

    return visitor;
  }

  private boolean isObjectMethods(String className, String descriptor) {
    return "toString".equals(className)
      && "()Ljava/lang/String;".equals(descriptor);
  }

  private boolean isGetterOrSetter(String methodName, String descriptor) {
    try {

      if (
        methodName.startsWith("get")
          && descriptor.startsWith("()")
      ) {
        return true;
      }

      if (
        methodName.startsWith("set")
          && descriptor.endsWith("V")
          && 1 == Type.getArgumentTypes(descriptor).length
      ) {
        return true;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

}
