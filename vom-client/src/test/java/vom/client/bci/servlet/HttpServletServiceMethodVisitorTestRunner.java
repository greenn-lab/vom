package vom.client.bci.servlet;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class HttpServletServiceMethodVisitorTestRunner extends ClassLoader {

  public static void main(String[] args) throws IOException {

    final byte[] classfileBuffer =
      classfileBytes("javax/servlet/http/HttpServlet");

    final byte[] byteCodes =
      new HttpServletServiceAdapter(classfileBuffer).toBytes();

    writeTastingClassfile(byteCodes);
  }

}
