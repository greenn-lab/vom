package vom.client.bci;

import vom.client.Config;
import vom.client.bci.jdbc.ConnectionAdapter;
import vom.client.bci.jdbc.PreparedStatementAdapter;
import vom.client.bci.jdbc.StatementAdapter;
import vom.client.bci.mybatis.MybatisDefaultSqlSessionAdapter;
import vom.client.bci.servlet.HttpServletAdapter;
import vom.client.bci.servlet.MethodChaserAdapter;
import vom.client.bci.servlet.ServletJSPAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

public class VOMClassFileTransformer implements ClassFileTransformer {

  private static final byte[] ZERO_BYTE = new byte[0];

  private static final List<Class<? extends ClassVisitAdapter>> adapters =
    Arrays.asList(
      HttpServletAdapter.class,
      ServletJSPAdapter.class,
      MethodChaserAdapter.class,
      ConnectionAdapter.class,
      PreparedStatementAdapter.class,
      StatementAdapter.class,

      MybatisDefaultSqlSessionAdapter.class
    );

  @Override
  public byte[] transform(
    ClassLoader loader,
    String className,
    Class<?> classBeingRedefined,
    ProtectionDomain protectionDomain,
    byte[] classfileBuffer
  ) {

    if (
      className == null
        || className.startsWith("vom/") // 내 스스로 감시하진 말아줘...
    ) {
      return ZERO_BYTE;
    }

    if (classBeingRedefined != null) {
      System.out.printf(
        "%n--- classBeingRedefined ---%n%s%n%s%n---/classBeingRedefined ---%n",
        classBeingRedefined.toString(),
        className
      );
    }

    try {
      return matchingAdapter(classfileBuffer, className);
    }
    catch (Throwable cause) {
      if (Config.isDebugMode()) {
        cause.printStackTrace();
      }
    }

    return ZERO_BYTE;
  }

  private byte[] matchingAdapter(byte[] buffer, String className) {
    for (Class<? extends ClassVisitAdapter> adapter : adapters) {
      try {
        final ClassVisitAdapter instance = adapter
          .getDeclaredConstructor(byte[].class, String.class)
          .newInstance(buffer, className);

        if (instance.isAdaptable()) {
          return instance.toBytes();
        }
      }
      catch (Throwable cause) {
        if (Config.isDebugMode()) {
          cause.printStackTrace(System.err);
        }
      }
    }

    return ZERO_BYTE;
  }

}
