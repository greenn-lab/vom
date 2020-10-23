package sample.servlet.test;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import sample.servlet.MockTestServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
        //e.printStackTrace();
        System.err.println(e.getMessage());
      }

      executionQuery2(ds);

    }

    private void executionQuery2(DataSource ds) {
      try {
        final Connection conn = ds.getConnection();
        final PreparedStatement stmt = conn.prepareStatement(
          "SELECT * FROM TEST WHERE 1 = ?1 AND '2' = ?2");

        stmt.setInt(1, 1);
        stmt.setInt(2, 'X');

        stmt.executeQuery();
        conn.rollback();

        stmt.close();
      }
      catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

  }
}
