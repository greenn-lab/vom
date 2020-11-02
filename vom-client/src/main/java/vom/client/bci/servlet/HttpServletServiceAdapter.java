package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.servlet.visitor.HttpServletServiceVisitor;

public class HttpServletServiceAdapter extends VOMClassVisitAdapter {

  public HttpServletServiceAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return Config.getList("classes.servlet").contains(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return "service".equals(methodName)
      && "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V"
      .equals(descriptor);
  }

  @Override
  public MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    return
      new HttpServletServiceVisitor(reader, visitor, methodName, descriptor);
  }

}
