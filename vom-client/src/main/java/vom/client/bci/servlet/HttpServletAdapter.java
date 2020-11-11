package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.ClassVisitAdapter;
import vom.client.bci.servlet.visitor.HttpServletServiceVisitor;

public class HttpServletAdapter extends ClassVisitAdapter {

  public HttpServletAdapter(byte[] classfileBuffer, String className) {
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
