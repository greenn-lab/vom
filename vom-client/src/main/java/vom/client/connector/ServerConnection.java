package vom.client.connector;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vom.client.Config;
import vom.client.bci.trove.Trover;
import vom.client.connector.sql.SqlManager;
import vom.client.exception.CarryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerConnection {

  private static final Integer TIMEOUT =
    Integer.parseInt(Config.get("server.timeout", "2000"));
  private static final Integer POOL_SIZE =
    Integer.parseInt(Config.get("server.pool", "100"));
  private static ExecutorService GIVER_POOL =
    Executors.newFixedThreadPool(POOL_SIZE);
  private static final HikariDataSource dataSource = new HikariDataSource();

  static {
    dataSource.setJdbcUrl("jdbc:h2:tcp://" +
      Config.getServerHost() + ":" +
      Config.getServerPort() + "/mem:collector"
    );
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    dataSource.setConnectionTimeout(TIMEOUT);
    dataSource.setMaximumPoolSize(POOL_SIZE);
  }

  public static void give(final Trover trover) {
    GIVER_POOL.execute(new Runnable() {
      @Override
      public void run() {
        // TODO trover serialization and giving
        try {
          ServerConnection.getConnection();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        finally {
          System.out.println("---------------------------------");
          System.out.printf("%s, %d%n", trover.getId(), trover.getCollected());
          System.out.println("---------------------------------");
        }

      }
    });
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

  public static Connection getConnection() {
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
