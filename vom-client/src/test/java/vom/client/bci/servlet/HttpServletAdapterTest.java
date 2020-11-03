package vom.client.bci.servlet;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import javax.servlet.http.HttpServlet;
import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class HttpServletAdapterTest extends ClassLoader {

  @Test
  void shouldRunBCI() throws IOException {

    final String className = Type.getInternalName(HttpServlet.class);

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new HttpServletAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

}
