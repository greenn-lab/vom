package vom.client.bci.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;
import vom.client.bci.VOMClassVisitAdapter;
import vom.client.bci.servlet.visitor.ServletChaseMethodVisitor;
import vom.client.bci.utility.OpcodeUtils;

import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;

public class ServletChaseMethodAdapter extends VOMClassVisitAdapter {

  public ServletChaseMethodAdapter(byte[] classfileBuffer, String className) {
    super(classfileBuffer, className);
  }

  @Override
  public boolean isAdaptable() {
    for (final String package_ : Config.getList("servlet.chase.packages")) {
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
    if (Config.isDebugMode()) {
      System.out.printf(
        "became entangled in vom (chase method) { %s#%s }%n",
        className,
        methodName
      );
    }

    return new ServletChaseMethodVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );
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
