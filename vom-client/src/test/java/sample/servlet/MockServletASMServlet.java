package sample.servlet;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import vom.client.asm.web.chaser.MockHttpServletChaserTarget;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

class MockServletASMServletTest {

  @Test
  void shouldSeizeHttpServletRequest() throws ServletException, IOException {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/hi/who/are/you");
    request.setParameter("name", "hello");
    request.setParameter("age", "12");
    request.addHeader("head", "merry");
    request.addHeader("foot", "dairy");
    request.addHeader("time", new Date());
    request.addHeader("hair", Long.MAX_VALUE);

    final MockHttpServletResponse response = new MockHttpServletResponse();
    response.setWriterAccessAllowed(true);

    final HttpServlet servlet = new MockServlet();

    servlet.service(
      request,
      response
    );
  }

  private static class MockServlet extends HttpServlet {

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
      try {
        Thread.sleep(300);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      return String.format(format, count);
    }


    @SuppressWarnings("unused")
    public void methodC(boolean param1, char param2, byte param3, short param4, int param5, float param6, long param7, double param8) {
      System.out.printf(
        "%s.methodC(...)%n",
        MockHttpServletChaserTarget.class.getName()
      );

      try {
        Thread.sleep(400);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
