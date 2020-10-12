package vom.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import vom.client.exception.FallDownException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Config {

  private static final String CONFIG_DEFAULT_PROPERTIES =
    System.getProperty("user.home") + "/.vom/config.properties";

  private static final Properties props = new Properties();
  private static final Set<String> packages = new HashSet<String>();


  public static String getId() {
    return props.getProperty("id");
  }

  public static int getPollingInterval() {
    return Integer.parseInt(props.getProperty("polling.interval"));
  }

  public static void setId(String id) {
    if (id != null && !"".equals(id.trim())) {
      props.setProperty("id", id.trim());
    }
  }

  public static Set<String> getPackages() {
    return packages;
  }

  public static String getServerHost() {
    return props.getProperty("server.host", "localhost");
  }

  public static int getServerPort() {
    return Integer.parseInt(props.getProperty("server.port", "3506"));
  }

  public static boolean isDebugMode() {
    return Boolean.parseBoolean(props.getProperty("debug", "false"));
  }

  /**
   * 설정파일을 통해서 초기 설정을 구성해요.
   */
  public static void configure() {
    final URL propFileUrl = ClassLoader.getSystemResource("config-default.properties");

    InputStream in = null;

    try {
      in = propFileUrl.openStream();
      props.load(in);
    }
    catch (IOException e) {
      // no work
    } finally {
      if (in != null) {
        try {
          in.close();
        }
        catch (IOException e) {
          // no work
        }
      }
    }

    mergeProperties(CONFIG_DEFAULT_PROPERTIES);

    if (getId() == null) {
      try {
        final String id = InetAddress.getLocalHost().getHostName();
        setId(id);
      }
      catch (UnknownHostException e) {
        throw new FallDownException(e);
      }
    }

    final String[] packages = props.getProperty("monitor.packages")
      .split("[\\s,|]+");

    for (String pkg : packages) {
      Config.packages.add(pkg.replace('.', '/'));
    }
  }

  public static void mergeProperties(final String filepath) {
    InputStream in = null;

    try {
      in = new FileInputStream(filepath);
      final Properties newProps = new Properties();
      newProps.load(in);

      for (final String name : newProps.stringPropertyNames()) {
        final String value = newProps.getProperty(name);

        if (value != null && !"".equals(value.trim())) {
          props.setProperty(name, value);
        }
      }

      System.err.printf("%s is applied to configuration%n", filepath);
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        }
        catch (IOException e) {
          // no work
        }
      }
    }
  }

}
