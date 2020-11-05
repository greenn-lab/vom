package vom.client.connector;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vom.client.Config;
import vom.client.bci.trove.Trove;
import vom.client.bci.trove.TroveExecutor;
import vom.client.connector.sql.SqlManager;
import vom.client.exception.CarryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerConnection {

  private static final Integer TIMEOUT =
    Integer.parseInt(Config.get("server.timeout", "2"));
  private static final Integer POOL_SIZE =
    Integer.parseInt(Config.get("server.pool", "100"));
  private static final ExecutorService GIVER_POOL =
    Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "giver-pool");
      }
    });

  protected static final HikariDataSource dataSource = new HikariDataSource();

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

  public static void give(final Trove trove) {
    GIVER_POOL.execute(new Runnable() {
      @Override
      public void run() {
        if (Config.isDebugMode()) {
          TroveExecutor.print(trove);
        }

        final String json = JSON.toJSONString(trove);
        System.err.println(json);

        try {
//          ServerConnection.getConnection();
        }
        catch (Throwable cause) {
          if (Config.isDebugMode()) {
            cause.printStackTrace();
          }
          else {
            System.err.printf("[%s] %s%n",
              Thread.currentThread().getName(),
              cause.getMessage());
          }
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
    }
    catch (SQLException e) {
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
    }
    catch (SQLException e) {
      // no work
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
