package vom.client.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import vom.client.asm.jdbc.JdbcAdapter;
import vom.client.asm.web.servlet.HttpServletChaserAdapter;
import vom.client.asm.web.servlet.HttpServletServiceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static vom.client.Config.containsDatabaseVendor;
import static vom.client.Config.containsJdbcClass;
import static vom.client.Config.containsServletChasedTarget;
import static vom.client.Config.containsServletClass;

public class VOMClientTransformer implements ClassFileTransformer {

  public static final int ASM_VERSION = Opcodes.ASM7;

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
    // 내 스스로 감시하진 말아줘...
    if (className.startsWith("vom/")) {
      return new byte[0];
    }

    // Servlet 이 호출 될 때,
    // 추적을 위한 코드를 이식 시켜요.
    if (containsServletClass(className)) {
      return new HttpServletServiceAdapter(classfileBuffer).toBytes();
    }

    // Servlet 관련 대상을 찾아서 추적해요.
    if (containsServletChasedTarget(className)) {
      return new HttpServletChaserAdapter(classfileBuffer, className).toBytes();
    }

    // JDBC 관련된 것들을 추적해요.
    if (
      containsDatabaseVendor(className)
        && (className.contains("Connection") || className.contains("Statement"))
    ) {
      final ClassReader reader = new ClassReader(classfileBuffer);
      final boolean isInterface =
        (reader.getAccess() & Opcodes.ACC_INTERFACE) != 0;

      if (!isInterface && containsJdbcClass(reader)) {
        return new JdbcAdapter(reader).toBytes();
      }
    }

    return new byte[0];
  }

}
