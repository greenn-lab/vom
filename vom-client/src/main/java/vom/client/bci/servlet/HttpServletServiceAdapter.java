package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.servlet.visitor.HttpServletServiceVisitor;

import java.util.List;

public class HttpServletServiceAdapter extends VOMClassVisitAdapter {

  private static final List<String> servletSeizes =
    Config.getList("servlet.seize");

  private static final List<String> servletFaints =
    Config.getList("servlet.faint");


  public HttpServletServiceAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }


  @Override
  public boolean isAdaptable() {
    if (servletFaints.contains(className)) {
      return false;
    }

    return servletSeizes.contains(className);
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
