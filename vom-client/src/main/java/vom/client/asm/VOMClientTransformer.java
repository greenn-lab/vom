package vom.client.asm;

import org.objectweb.asm.Opcodes;
import vom.client.Config;
import vom.client.asm.web.chaser.HttpServletChaserAdapter;
import vom.client.asm.web.servlet.HttpServletServiceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

public class VOMClientTransformer implements ClassFileTransformer {

  public static final int ASM_VERSION = Opcodes.ASM7;
  
  private static final Set<String> DEFAULT_JDBC_CLASSES = new HashSet<>();
  private static final Set<String> DEFAULT_SERVLET_CLASSES = new HashSet<>();
  
  static {
    DEFAULT_SERVLET_CLASSES.add("javax/servlet/http/HttpServlet");
    
    DEFAULT_JDBC_CLASSES.add("javax/sql/Statement");
    DEFAULT_JDBC_CLASSES.add("javax/sql/PreparedStatement");
    DEFAULT_JDBC_CLASSES.add("javax/sql/CallableStatement");
  }


  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
    // 정의된 Servlet 이 호출 될 때,
    // 추적을 위한 코드를 이식 시켜요.
    if (isServletClass(className)) {
      return new HttpServletServiceAdapter(classfileBuffer).toBytes();
    }
    
    // Servlet 관련 대상을 찾아서 추적해요.
    if (isChasingTargetClass(className)) {
      return new HttpServletChaserAdapter(classfileBuffer).toBytes();
    }
    
    return new byte[0];
  }
  
  private boolean isServletClass(final String className) {
    for (final String servletClass : DEFAULT_SERVLET_CLASSES) {
      if (servletClass.startsWith(className)) {
        return true;
      }
    }
    
    return false;
  }
  
  private boolean isChasingTargetClass(final String className) {
    for (final String pkg : Config.getPackages()) {
      if (className.startsWith(pkg)) {
        return true;
      }
    }
    
    return false;
  }
}
