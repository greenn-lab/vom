package sample.servlet.test;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import sample.servlet.MockTestServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.Date;

class SimpleTroverTest {

  @Test
  void shouldSeizeHttpServletRequest() throws ServletException, IOException {
    final HttpServlet servlet = new MockTestServlet();

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

    servlet.service(request, response);
  }
}
