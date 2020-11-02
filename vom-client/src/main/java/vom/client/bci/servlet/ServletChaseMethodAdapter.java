package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.servlet.visitor.ServletChaseMethodVisitor;
import vom.client.bci.utility.OpcodeUtils;

import java.util.List;

import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;

public class ServletChaseMethodAdapter extends VOMClassVisitAdapter {

  private static final List<String> servletChasePackages =
    Config.getList("servlet.chase.packages");


  public ServletChaseMethodAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }

  @Override
  public boolean isAdaptable() {
    for (final String package_ : servletChasePackages) {
      if (OpcodeUtils.antPathMatches(package_, className)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return ACC_PUBLIC == access
          && !CONSTRUCTOR.equals(methodName)
          && !isGetterOrSetter(methodName, descriptor)
          && !isObjectMethods(methodName, descriptor);
  }

  @Override
  public MethodVisitor methodVisitor(MethodVisitor visitor, String methodName, String descriptor) {
    return null;
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
          "became entangled in vom (chase method) { %s#%s }%n",
          className,
          name
        );
      }

      visitor = new ServletChaseMethodVisitor(
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
