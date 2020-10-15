package vom.client.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.Config;
import vom.client.asm.jdbc.JdbcAdapter;
import vom.client.asm.web.servlet.HttpServletChaserAdapter;
import vom.client.asm.web.servlet.HttpServletServiceAdapter;

import javax.servlet.http.HttpServlet;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VOMClientTransformer implements ClassFileTransformer {

  public static final int ASM_VERSION = Opcodes.ASM7;

  private static final Set<String> DEFAULT_JDBC_CLASSES = new HashSet<String>();
  private static final Set<String> DEFAULT_SERVLET_CLASSES = new HashSet<String>();

  static {
    DEFAULT_SERVLET_CLASSES.add(Type.getInternalName(HttpServlet.class));

    DEFAULT_JDBC_CLASSES.add(Type.getInternalName(Connection.class));
    DEFAULT_JDBC_CLASSES.add(Type.getInternalName(Statement.class));
    DEFAULT_JDBC_CLASSES.add(Type.getInternalName(PreparedStatement.class));
    DEFAULT_JDBC_CLASSES.add(Type.getInternalName(CallableStatement.class));
  }


  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
    // Servlet 이 호출 될 때,
    // 추적을 위한 코드를 이식 시켜요.
    if (isServletClass(className)) {
      return new HttpServletServiceAdapter(classfileBuffer).toBytes();
    }

    // Servlet 관련 대상을 찾아서 추적해요.
    else if (isChasingTargetClass(className)) {
      return new HttpServletChaserAdapter(classfileBuffer, className).toBytes();
    }

    // JDBC 관련된 것들을 추적해요.
    else if (
      className.contains("Connection")
        || className.contains("Statement")
    ) {
      final ClassReader reader = new ClassReader(classfileBuffer);
      final boolean isInterface =
        (reader.getAccess() & Opcodes.ACC_INTERFACE) != 0;

      if (!isInterface && inJdbc(reader)) {
        return new JdbcAdapter(reader).toBytes();
      }
    }


    return new byte[0];
  }

  private boolean inJdbc(final ClassReader reader) {
    for (String inf : reader.getInterfaces()) {
      if (DEFAULT_JDBC_CLASSES.contains(inf)
      && containsVendor(reader.getClassName())) {
        return true;
      }
    }

    return false;
  }


  /**
   * Connection Pool 에서 Delegating 하는 식으로 SQL 객체를
   * 구성하기 때문에 불필요한 구현체들이 추출되는데, 이를 방지하기 위해
   * vendor 가 실제적으로 사용하는 PreparedStatement 구현체를 추려내요.
   *
   * @return 지정된 벤더에 포함될까, 아닐까.
   */
  private boolean containsVendor(String name) {
    final Set<String> vendors = new HashSet<String>(
      Arrays.asList(Config.get("chaser.db.vendors").split("[\\s,|]+"))
    );

    for (String vendor : vendors) {
      if (name.contains(vendor)) {
        return true;
      }
    }

    return false;
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
