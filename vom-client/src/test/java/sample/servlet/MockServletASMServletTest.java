package sample.servlet;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import vom.client.asm.web.chaser.MockHttpServletChaserTarget;
import vom.client.asm.web.trove.WebTrove;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class MockServletASMServletTest {

  @Test
  void shouldSeizeHttpServletRequest() throws InterruptedException {
    final HttpServlet servlet = new MockServlet();

    final int[] threadCounter = {0};
    final ExecutorService service = Executors.newFixedThreadPool(10, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "my-worker-" + String.format("%02d", threadCounter[0]++));
      }
    });

    final CountDownLatch latch = new CountDownLatch(50);

    for (int i = 0; i < 50; i++) {
      service.execute(new Runnable() {
        @Override
        public void run() {
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

          try {
            servlet.service(
              request,
              response
            );
          } catch (ServletException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }

          latch.countDown();
        }
      });

    }

    latch.await();

    Collections.sort(WebTrove.logs, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.substring(0, 13).compareTo(o2.substring(0, 13));
      }
    });
    for (String line : WebTrove.logs.toArray(new String[0]))
      System.out.print(line);
  }

  private static class MockServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
      System.out.println("HTTP request by GET method!");
      final String shouted = shout(123);
      System.out.println(shouted);

      methodC(true, 'C', (byte) 0x0001, Short.MAX_VALUE, Integer.MAX_VALUE,
        Float.MIN_VALUE, Long.MAX_VALUE, Double.MIN_VALUE);
    }

    public String shout(int count) {
      final String format = "I shout %dth!!!";
      try {
        Thread.sleep(600);
      } catch (InterruptedException e) {
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
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
