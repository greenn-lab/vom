package vom.server;

import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import java.sql.SQLException;

//@SpringBootApplication
//@Slf4j
public class VOMServerApplication implements CommandLineRunner {

  @Override
  public void run(String... args) throws SQLException {

    final Server server = Server
        .createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "3506")
        .start();

    Runtime.getRuntime().addShutdownHook(
        new Thread(() -> {
          if (server.isRunning(false)) {
            server.stop();
          }
        })
    );
  }


  public static void main(String[] args) {
    SpringApplication.run(VOMServerApplication.class, args);
  }

}
