package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMClassVisitAdapter;

public class ServletJSPAdapter extends VOMClassVisitAdapter {

  private static final String DEFAULT_JSP_CLASSES =
    "org/apache/jasper/servlet/JspServlet";


  public ServletJSPAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }

  @Override
  public boolean isAdaptable() {
    return DEFAULT_JSP_CLASSES.equals(className);
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
      return new ServletJSPVisitor(access, descriptor, visitor);
    }

    return visitor;
  }

}
