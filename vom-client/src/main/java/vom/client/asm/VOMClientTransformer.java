package vom.client.asm;

import vom.client.Config;
import vom.client.asm.web.chaser.HttpServletChaserAdapter;
import vom.client.asm.web.servlet.HttpServletServiceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class VOMClientTransformer implements ClassFileTransformer {
  
  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
    // 정의된 Servlet 이 호출 될 때,
    // 추적을 위한 코드를 이식 시켜요.
    if (isServletClass(className)) {
      return new HttpServletServiceAdapter(classfileBuffer)
          .toByteArray();
    }
    
    // Servlet 관련 대상을 찾아서 추적해요.
    if (isChasingTargetClass(className)) {
      return new HttpServletChaserAdapter(classfileBuffer)
          .toByteArray();
    }
    
    return new byte[0];
  }
  
  private boolean isServletClass(final String className) {
    for (final String servletClass : Config.DEFAULT_SERVLET_CLASSES) {
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
