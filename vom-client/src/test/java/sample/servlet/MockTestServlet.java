package sample.servlet;

import vom.client.bci.web.chaser.MockHttpServletChaserTarget;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class MockTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
      System.out.println("HTTP request by GET method!");
      final String shouted = shout(123);
      System.out.println(shouted);

      noArgument();

      methodC(true, 'C', (byte) 0x0001, Short.MAX_VALUE, Integer.MAX_VALUE,
        Float.MIN_VALUE, Long.MAX_VALUE, Double.MIN_VALUE);
    }

    public String shout(int count) {
      final String format = "I shout %dth!!!";

      try {
        TimeUnit.MILLISECONDS.sleep(600);
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
        TimeUnit.MILLISECONDS.sleep(400);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    public void noArgument() {
      System.out.println("Here is no args :)");
    }
  }
