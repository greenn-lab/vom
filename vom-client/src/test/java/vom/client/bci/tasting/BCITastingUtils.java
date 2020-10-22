package vom.client.bci.tasting;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BCITastingUtils {

  public static final String OBJECT_INTERNAL =
    Type.getInternalName(Object.class);

  public static byte[] classfileBytes(String className) {
    final String classInternalName =
      className.replace('.', '/') + ".class";

    final InputStream in =
      ClassLoader.getSystemResourceAsStream(classInternalName);

    assert null != in;

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    byte[] bytes = new byte[128];
    int i;

    try {
      while ((i = in.read(bytes, 0, bytes.length)) > -1) {
        bout.write(bytes, 0, i);
      }

      bout.close();

      return bout.toByteArray();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] writeTastingClassfile(ClassWriter writer)
    throws IOException {
    byte[] code = writer.toByteArray();
    return writeTastingClassfile(code);
  }

  public static byte[] writeTastingClassfile(byte[] bytes)
    throws IOException {

    final FileOutputStream out =
      new FileOutputStream("./target/classes/Tasting.class");
    out.write(bytes);
    out.close();

    return bytes;
  }

}
