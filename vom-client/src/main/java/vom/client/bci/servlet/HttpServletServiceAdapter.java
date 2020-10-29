package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMClassVisitAdapter;

public class HttpServletServiceAdapter extends VOMClassVisitAdapter {

  private static final String DEFAULT_SERVLET_CLASSES =
    "javax/servlet/http/HttpServlet";


  public HttpServletServiceAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return DEFAULT_SERVLET_CLASSES.equals(className);
  }

  @Override
  public MethodVisitor visitMethod(
    int access,
    String name,
    String descriptor,
    String signature,
    String[] exceptions
  ) {
    final MethodVisitor visitor =
      super.visitMethod(access, name, descriptor, signature, exceptions);

    if (
      visitor != null
        && "service".equals(name)
        && "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V"
        .equals(descriptor)
    ) {
      return new HttpServletServiceVisitor(access, className, descriptor, visitor);
    }

    return visitor;
  }

}
