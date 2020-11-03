package vom.client.bci.servlet;

import org.apache.jasper.servlet.JspServlet;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class ServletJSPAdapterTest {

  @Test
  void shouldRunBCIJasperJSP() throws IOException {

    final String className = Type.getInternalName(JspServlet.class);

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new ServletJSPAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

}
