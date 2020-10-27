package vom.client.bci.servlet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import vom.client.bci.ClassWritable;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

public class HttpServletJasperAdapter extends ClassVisitor implements ClassWritable {

  private final String className;


  public HttpServletJasperAdapter(byte[] classfileBuffer, String className) {
    super(ASM_VERSION);

    this.className = className;

    final ClassReader reader = new ClassReader(classfileBuffer);
    this.cv = new ClassWriter(
      reader,
      ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
    );

    reader.accept(this, ClassReader.EXPAND_FRAMES);
  }


  @Override
  public byte[] toBytes() {
    return ((ClassWriter) cv).toByteArray();
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    final MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);

    if (
      visitor != null &&
        "serviceJspFile".equals(name) &&
        "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/String;Z)V"
          .equals(descriptor)
    ) {
      return new HttpServletJasperJSPMethodVisitor(access, className, descriptor, visitor);
    }

    return visitor;
  }

}
