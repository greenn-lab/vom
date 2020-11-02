package vom.client.bci.servlet;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class HttpServletServiceAdapterTest extends ClassLoader {

  @Test
  void shouldRunBCI() throws IOException {

    final String className = "javax/servlet/http/HttpServlet";

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new HttpServletServiceAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

}