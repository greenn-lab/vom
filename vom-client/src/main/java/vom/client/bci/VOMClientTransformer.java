package vom.client.bci;

import org.objectweb.asm.ClassWriter;
import vom.client.bci.jdbc.JdbcAdapter;
import vom.client.bci.servlet.HttpServletServiceAdapter;
import vom.client.bci.servlet.ServletJSPAdapter;
import vom.client.bci.servlet.ServletWovenMethodAdapter;
import vom.client.exception.FallDownException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

public class VOMClientTransformer implements ClassFileTransformer {

  private static final byte[] ZERO_BYTE = new byte[0];

  private static final List<Class<? extends VOMClassVisitAdapter>> adapters =
    Arrays.asList(
      HttpServletServiceAdapter.class,
      ServletJSPAdapter.class,
      ServletWovenMethodAdapter.class,
      JdbcAdapter.class
    );

  @Override
  public byte[] transform(
    ClassLoader loader,
    String className,
    Class<?> classBeingRedefined,
    ProtectionDomain protectionDomain,
    byte[] classfileBuffer
  ) {

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
      return matchingAdapter(classfileBuffer, className);
    }
    catch (Throwable cause) {
      cause.printStackTrace();
    }

    return ZERO_BYTE;
  }

  private byte[] matchingAdapter(byte[] buffer, String className) {
    try {
      for (Class<? extends VOMClassVisitAdapter> adapter : adapters) {
        final VOMClassVisitAdapter instance = adapter
          .getDeclaredConstructor(byte[].class, String.class)
          .newInstance(buffer, className);

        if (instance.isAdaptable()) {
          return instance.toBytes();
        }
      }
    }
    catch (Exception e) {
      // no work
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
