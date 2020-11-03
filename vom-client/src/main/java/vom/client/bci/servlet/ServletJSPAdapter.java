package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.servlet.visitor.HttpServletServiceVisitor;
import vom.client.bci.servlet.visitor.ServletJasperJSPVisitor;

public class ServletJSPAdapter extends VOMClassVisitAdapter {

  public ServletJSPAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.jsp-servlet").contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return "serviceJspFile".equals(methodName) &&
      ("(Ljavax/servlet/http/HttpServletRequest;" +
        "Ljavax/servlet/http/HttpServletResponse;" +
        "Ljava/lang/String;" +
        "Z)V").equals(descriptor);
  }

  @Override
  public MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    final ServletJasperJSPVisitor jspVisitor = new ServletJasperJSPVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );

    return new HttpServletServiceVisitor(
      reader,
      jspVisitor,
      methodName,
      descriptor
    );
  }

}
