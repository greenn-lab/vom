package vom.client.asm.web.chaser;

import org.objectweb.asm.Type;
import vom.client.asm.web.servlet.HttpServletChaserAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

class HttpServletChaserAdapterRunner {

  public static void main(String[] args) throws IOException {

    final String className = Type.getInternalName(MockHttpServletChaserTarget.class);
    final ClassLoader classLoader = HttpServletChaserAdapterRunner.class.getClassLoader();
    final URL url = classLoader
        .getResource(className + ".class");

    if (url == null)
      throw new NullPointerException();

    final FileInputStream in = new FileInputStream(url.getFile());
    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    int i;
    while ((i = in.read()) != -1) {
      out.write(i);
    }

    final byte[] bytes = out.toByteArray();
    final HttpServletChaserAdapter adapter = new HttpServletChaserAdapter(bytes, className);

    final File file = new File(url.getFile().replaceFirst("\\.class", "BCI\\.class"));
    final FileOutputStream classOut =
        new FileOutputStream(file);
d:
    classOut.write(adapter.toBytes());
    classOut.close();
  }

}
