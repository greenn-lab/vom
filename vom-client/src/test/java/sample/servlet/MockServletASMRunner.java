package sample.servlet;


import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

public class MockServletASMRunner {

  public static void main(String[] args) throws ServletException, IOException {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    final HttpServlet servlet = new MockServletASM();

    servlet.service(request, response);

    servlet.service(
        request,
        response
    );
  }

}
