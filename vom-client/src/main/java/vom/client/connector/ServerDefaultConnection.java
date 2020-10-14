package vom.client.connector;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vom.client.Config;
import vom.client.asm.jdbc.trove.DBTrove;
import vom.client.asm.web.trove.WebTrove;
import vom.client.exception.CarryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerDefaultConnection {

  private static final HikariDataSource dataSource = new HikariDataSource();

  static {
    final Integer timeout =
      Config.getIntegerProperty("server.timeout", "2000");
    final Integer poolSize =
      Config.getIntegerProperty("server.pool", "100");

    try {
      dataSource.getConnection("sa", "");
      dataSource.setJdbcUrl("jdbc:h2:tcp://" +
        Config.getServerHost() + ":" +
        Config.getServerPort() + "/mem:trace"
      );
      dataSource.setUsername("sa");
      dataSource.setPassword("");
      dataSource.setConnectionTimeout(timeout);
      dataSource.setMaximumPoolSize(poolSize);
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private static final String INSERT_DB_TROVE =
    "INSERT INTO TROVE (" +
      "ID, COLLECTED, TYPE, BIN, )";

  public static void send(DBTrove trove) {
    if (1 != executePrepared(INSERT_DB_TROVE)) {
      throw new CarryException("too many inserted");
    }
  }

  public static void send(WebTrove trove) {

  }

  private static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private static int executePrepared(String sql) {
    Connection connection = null;
    PreparedStatement ps = null;

    try {
      connection = getConnection();
      ps = connection.prepareStatement(sql);
      return ps.executeUpdate();
    }
    catch (SQLException e) {
      throw new CarryException(e.getMessage(), e);
    } finally {
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException e) {
          // no work
        }
      }

      if (connection != null) {
        try {
          connection.close();
        }
        catch (SQLException e) {
          // no work
        }
      }
    }
  }

}
