package vom.client.bci.tasting;

import org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class TastingUtils {

  public static byte[] writeTastingClassfile(ClassWriter writer)
    throws IOException {

    byte[] code = writer.toByteArray();

    final FileOutputStream out =
      new FileOutputStream("./vom-client/target/classes/Tasting.class");
    out.write(code);
    out.close();

    return code;
  }

}
