package vom.client.asm;

import org.objectweb.asm.Type;
import org.junit.jupiter.api.extension.Extension;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;

public final class ASMTestTransformer implements Extension {

  public static void transform(
      Class<?> targetClass,
      Class<?> adapterClass,
      Class<?>[] adapterClassParameters
  ) {
    final String internalName = Type.getInternalName(targetClass);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final URL url = adapterClass.getClassLoader().getResource(internalName + ".class");

    assert url != null;

    final String path = url.getPath();

    try {
      final InputStream in = new FileInputStream(path);
      byte[] bytes = new byte[1024];
      int i;
      while ((i = in.read(bytes, 0, bytes.length)) > -1) {
        out.write(bytes, 0, i);
      }

      final Constructor<?> constructor = adapterClass.getConstructor(adapterClassParameters);
      @SuppressWarnings("JavaReflectionInvocation") final ClassWritable o = (ClassWritable) constructor.newInstance(internalName, out.toByteArray());
      byte[] adaptedBytes = o.toBytes();

      final String replace = path.replaceAll("\\.class", "BCI.class");
      FileOutputStream classOut = new FileOutputStream(replace);
      classOut.write(adaptedBytes);
      classOut.close();

      System.out.println(replace);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

}
