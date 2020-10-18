package vom.client.connector;

import com.zaxxer.hikari.HikariDataSource;
import vom.client.Config;
import vom.client.connector.sql.SqlManager;
import vom.client.exception.CarryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class ServerDefaultConnection {

  private static final HikariDataSource dataSource = new HikariDataSource();

  static {
    final Integer timeout =
      Integer.parseInt(Config.get("server.timeout", "2000"));
    final Integer poolSize =
      Integer.parseInt(Config.get("server.pool", "100"));

    dataSource.setJdbcUrl("jdbc:h2:tcp://" +
      Config.getServerHost() + ":" +
      Config.getServerPort() + "/mem:collector"
    );
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    dataSource.setConnectionTimeout(timeout);
    dataSource.setMaximumPoolSize(poolSize);
  }


  private ServerDefaultConnection() {
  }


  public static void sendSystemStats(
    Double cpu,
    long[] disk,
    long[] memory,
    long[] network
  ) {
    execute(
      SqlManager.getInstance().get("system-stats"),
      Config.getId(),
      System.currentTimeMillis(),
      cpu,
      disk[0], disk[1],
      memory[0], memory[1],
      network[0], network[1], network[2]
    );
  }

  private static Connection getConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new CarryException("refuse connect to server!");
    }
  }

  private static void execute(String sql, Object... parameters) {
    Connection connection = null;
    PreparedStatement ps = null;

    try {
      connection = getConnection();
      ps = connection.prepareStatement(sql);

      for (int i = 0; i < parameters.length; i++) {
        ps.setObject(i + 1, parameters[i]);
      }

      ps.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      // no work
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
          // no work
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          // no work
        }
      }
    }
  }

}
