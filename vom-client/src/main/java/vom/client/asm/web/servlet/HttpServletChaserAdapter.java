package vom.client.asm.web.servlet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import vom.client.asm.ClassWritable;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static vom.client.asm.VOMClientTransformer.ASM_VERSION;

public class HttpServletChaserAdapter extends ClassVisitor implements ClassWritable, Opcodes {

  private final String className;
  private final boolean isInterface;

  public HttpServletChaserAdapter(byte[] classfileBuffer, String className) {
    super(ASM_VERSION);

    final ClassReader reader = new ClassReader(classfileBuffer);
    this.cv = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

    this.className = className;
    this.isInterface = (reader.getAccess() & ACC_INTERFACE) != 0;

    reader.accept(this, EXPAND_FRAMES);
  }

  @Override
  public byte[] toBytes() {
    return ((ClassWriter) cv).toByteArray();
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

    if (!isInterface
      && null != visitor
      && Opcodes.ACC_PUBLIC == access
      && !"<init>".equals(name)
    ) {
      visitor = new HttpServletChaserMethodVisitor(
        visitor,
        access,
        className,
        name,
        descriptor
      );
    }

    return visitor;
  }


}
