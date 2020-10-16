package sample.servlet;

import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MockServletASMRunner {

  public static void main(String[] args) throws ServletException, IOException {
    final HttpServlet mockServletASM = new MockServletASM();
    final MockServletASM.MockGetReq request = new MockServletASM.MockGetReq(Mockito.mock(HttpServletRequest.class));
    final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    mockServletASM.toString();

    mockServletASM.service(
        request,
        response
    );
  }

}
