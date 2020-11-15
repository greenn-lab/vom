package vom.client.connector;

import com.jsoniter.output.JsonStream;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vom.client.Config;
import vom.client.bci.trove.Trove;
import vom.client.bci.trove.TroveExecutor;
import vom.client.connector.sql.SqlManager;
import vom.client.exception.RoughAndReadyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectorConnection {

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
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setJdbcUrl("jdbc:h2:tcp://" +
      Config.getServerHost() + ":" +
      Config.getServerPort() + "/mem:collector"
    );
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    dataSource.setConnectionTimeout(TIMEOUT * 1000L);
    dataSource.setMaximumPoolSize(POOL_SIZE);
  }

  public static void give(final Trove trove) {
    GIVER_POOL.execute(new Runnable() {
      @Override
      public void run() {
        if (Config.isDebugMode()) {
          TroveExecutor.print(trove);
        }

        try {
          final String json = JsonStream.serialize(trove);
          sendExecChaser(trove, json);
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


  public static void sendExecChaser(Trove trove, String json) {
    execute(
      SqlManager.getInstance().get("insert-exec-chaser"),
      trove.getId(),
      trove.getCollected(),
      trove.getUri(),
      json
    );
  }

  public static void sendSystemPerf(
    Double cpu,
    long[] disk,
    long[] memory,
    long[] network
  ) {
    execute(
      SqlManager.getInstance().get("insert-system-perf"),
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
    catch (Throwable cause) {
      throw new RoughAndReadyException(cause);
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
    }
    finally {
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
