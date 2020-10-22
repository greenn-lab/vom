package vom.client.bci.servlet;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class HttpServletServiceMethodVisitorTest extends ClassLoader {

  @Test
  void shouldRunBCI() throws IOException {

    final byte[] classfileBuffer =
      classfileBytes("javax/servlet/http/HttpServlet");

    final byte[] byteCodes =
      new HttpServletServiceAdapter(classfileBuffer).toBytes();

    writeTastingClassfile(byteCodes);
  }

}
