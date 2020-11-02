package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.servlet.visitor.ServletJSPVisitor;

public class ServletJSPAdapter extends VOMClassVisitAdapter {

  public ServletJSPAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("servlet.jsp").contains(className);
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
