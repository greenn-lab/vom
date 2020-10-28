package vom.client.bci;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import vom.client.bci.jdbc.JdbcStatementAdapter;
import vom.client.bci.servlet.ServletWovenMethodAdapter;
import vom.client.bci.servlet.HttpServletServiceAdapter;
import vom.client.exception.FallDownException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static vom.client.Config.containsDatabaseVendor;
import static vom.client.Config.containsJdbcClass;
import static vom.client.Config.containsServletChasedTarget;
import static vom.client.Config.containsServletClass;

public class VOMClientTransformer implements ClassFileTransformer {

  public static final int ASM_VERSION = Opcodes.ASM7;

  public static Set<ClassLoader> LOADERS = new HashSet<ClassLoader>();

  private static final byte[] ZERO_BYTE = new byte[0];


  @Override
  public byte[] transform(
    ClassLoader loader,
    String className,
    Class<?> classBeingRedefined,
    ProtectionDomain protectionDomain,
    byte[] classfileBuffer
  ) {

    if (loader instanceof URLClassLoader) {
      LOADERS.add(loader);
    }

    // 내 스스로 감시하진 말아줘...
    if (
      className == null
//          || className.startsWith("vom/")
    ) {
      return ZERO_BYTE;
    }

    if (classBeingRedefined != null) {
      System.out.printf(
        "%n%n%n--- classBeingRedefined ---%n%s%n%s%n---/classBeingRedefined ---%n",
        classBeingRedefined.toString(),
        className
      );
    }

    try {
      // Servlet 이 호출 될 때,
      // 추적을 위한 코드를 이식 시켜요.
      if (containsServletClass(className)) {
        System.out.println("ServletClass: " + className + " | " + loader);
        return new HttpServletServiceAdapter(classfileBuffer, className).toBytes();
      }

      // JSP 가 호출 될 때,
      // 추적을 위한 코드를 이식 시켜요.
//      if (containsJSPClass(className)) {
//        System.out.println("JSPClass: " + className + " | " + loader);
//        return new HttpServletJasperAdapter(classfileBuffer, className).toBytes();
//      }

      // 모니터링 패키지(monitor.packages) 설정에 속해있는
      // 대상들을 추적해요.
      if (containsServletChasedTarget(className)) {
        return new ServletWovenMethodAdapter(classfileBuffer, className).toBytes();
      }

      // JDBC 관련된 것들을 추적해요.
      if (
        containsDatabaseVendor(className)
          && (className.contains("Connection") || className.contains("Statement"))
      ) {
        final ClassReader reader = new ClassReader(classfileBuffer);
        final boolean isInterface = (ACC_INTERFACE & reader.getAccess()) != 0;

        if (!isInterface && containsJdbcClass(reader)) {
          return new JdbcStatementAdapter(reader).toBytes();
        }
      }
    } finally {
    }

    return ZERO_BYTE;
  }

  /**
   * @deprecated it only needs for check bytecodes
   */
  @Deprecated
  public static byte[] writeTastingClassfile(ClassWriter writer) {
    byte[] code = writer.toByteArray();
    return writeTastingClassfile(code);
  }


  /**
   * @deprecated it only needs for check bytecodes
   */
  @Deprecated
  public static byte[] writeTastingClassfile(byte[] bytes) {
    try {
      final FileOutputStream out =
        new FileOutputStream("D:/Tasting.class");
      out.write(bytes);
      out.close();

      return bytes;
    }
    catch (IOException e) {
      throw new FallDownException(e);
    }
  }

}
