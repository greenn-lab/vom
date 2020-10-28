package vom.client.bci.servlet;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class ServletJSPAdapterTest {

  @Test
  void shouldRunBCIJasperJSP() throws IOException {

    final String className = "org/apache/jasper/servlet/JspServlet";

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new ServletJSPAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

}
