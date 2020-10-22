package sample.servlet.test;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import sample.servlet.MockTestServlet;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MultiThreadTroverTest {

  @Test
  void shouldRunningSuccessfulInMultiThread() {
    final MockTestServlet servlet = new MockTestServlet();

    final ExecutorService service = Executors.newFixedThreadPool(50);
    service.execute(new Runnable() {
      @Override
      public void run() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/multi/thread/test");

        try {
          servlet.service(request, new MockHttpServletResponse());
        }
        catch (ServletException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
        }

      }
    });
  }

}
