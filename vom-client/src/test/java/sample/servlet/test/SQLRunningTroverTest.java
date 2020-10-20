package sample.servlet.test;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import sample.servlet.MockTestServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

class SQLRunningTroverTest {

  final HttpServlet servlet = new MockTestServletWithSQL();

  @Test
  void shouldSeizeQueryAndMethodExecutions() throws ServletException, IOException {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/hi/sql/test");

    servlet.service(request, new MockHttpServletResponse());
  }

  private class MockTestServletWithSQL extends MockTestServlet {

    @Override
    public String shout(int count) {
      executeStatement();
      return super.shout(count);
    }

    private void executeStatement() {

      HikariDataSource ds = new HikariDataSource();
      ds.setJdbcUrl("jdbc:h2:tcp://localhost:3506/mem:collector");
      ds.setUsername("sa");

      try {
        final Connection conn = ds.getConnection();
        final Statement stmt = conn.createStatement();
        stmt.execute("SELECT * FROM TEST");
        stmt.close();
        conn.commit();
      }
      catch (SQLException e) {
        e.printStackTrace();
      }

    }


  }
}
