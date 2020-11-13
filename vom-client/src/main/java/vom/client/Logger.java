package vom.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logger {

  private static final String LOGGER_DEFAULT_PROPERTIES =
    System.getProperty("user.home") + "/.vom/logger.properties";

  public static void initialize() {
    apply("vom/client/logger-default.properties");
    apply(LOGGER_DEFAULT_PROPERTIES);
  }

  private static void apply(String path) {
    final URL propFileUrl = ClassLoader.getSystemResource(path);

    new Thread(new Runnable() {
      @Override
      public void run() {
        LogManager manager;
        while ((manager = LogManager.getLogManager()) == null) {
          try {
            TimeUnit.SECONDS.sleep(1);
          }
          catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
          }
        }

        try {
          manager.readConfiguration(propFileUrl.openStream());
        }
        catch (Exception ignored) {
          // no works
        }
      }
    }).start();
  }

}
