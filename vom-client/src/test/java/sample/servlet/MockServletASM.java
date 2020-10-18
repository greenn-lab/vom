package sample.servlet;

import vom.client.asm.web.chaser.MockHttpServletChaserTarget;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MockServletASM extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    System.out.println();
    System.out.println();

    System.out.println("HTTP request by GET method!");
    final String shouted = shout(123);
    System.out.println(shouted);

    methodC(true, 'C', (byte) 0x0001, Short.MAX_VALUE, Integer.MAX_VALUE,
        Float.MIN_VALUE, Long.MAX_VALUE, Double.MIN_VALUE);

    System.out.println();
    System.out.println();
  }

  public String shout(int count) {
    final String format = "I shout %dth!!!";
    return String.format(format, count);
  }


  public void methodC(boolean param1, char param2, byte param3, short param4, int param5, float param6, long param7, double param8) {
    System.out.printf(
        "%s.methodC(...)%n",
        MockHttpServletChaserTarget.class.getName()
    );
  }

  protected static class MockGetReq extends HttpServletRequestWrapper {

    private final Map<String, String> parameters = new HashMap<String, String>(2);
    private final Hashtable<String, String> headers = new Hashtable<String, String>();

    public MockGetReq(HttpServletRequest request) {
      super(request);
      parameters.put("say", "hello");
      parameters.put("hi", "bye");

      headers.put("head", "merry");
      headers.put("arms", "8");
    }

    @Override
    public String getRequestURI() {
      return "/mock/sample-uri/";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getParameterMap() {
      return parameters;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getHeaderNames() {
      return headers.keys();
    }

    @Override
    public String getHeader(String name) {
      return headers.get(name);
    }

    @Override
    public String getMethod() {
      return "GET";
    }
  }

}
