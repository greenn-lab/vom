package vom.client.bci.jdbc;

import org.h2.Driver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class JdbcStatementAdapterTest {

  @Test
  void shouldGetSQLAndParameters() throws SQLException {
    final Driver driver = new Driver();
    final Connection connection = driver.connect("jdbc:h2:mem:test", null);
    final PreparedStatement ps = connection
      .prepareStatement(
        "SELECT current_date() FROM DUAL WHERE 1 = ?1 " +
        "AND 2 = ?2"
      );

    ps.setString(1, "1");
    ps.setInt(2, 2);

    final ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      final Object object = rs.getObject(1);
      System.out.println(object);

      Assertions.assertNotNull(object);
    }
  }

}
