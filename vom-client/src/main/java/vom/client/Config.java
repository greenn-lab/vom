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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Config {

  private static final String VALUE_SPLIT_PATTERN = "[\\s,|]+";

  private static final String CONFIG_DEFAULT_PROPERTIES =
    System.getProperty("user.home") + "/.vom/config.properties";

  private static final Properties props = new Properties();


  public static String get(String key) {
    return props.getProperty(key);
  }

  public static List<String> getClassList(String key) {
    final String[] values =
      props.getProperty(key, "").split(VALUE_SPLIT_PATTERN);

    final List<String> list = new ArrayList<String>(values.length);
    for (String value : values) {
      value = value.trim().replace('.', '/');
      if (!value.isEmpty()) {
        list.add(value);
      }
    }

    return list;
  }

  public static String get(String key, String defaultValue) {
    return props.getProperty(key, defaultValue);
  }

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
    final URL propFileUrl = ClassLoader
      .getSystemResource("vom/client/config-default.properties");

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

      System.err.printf("\"%s\" is applied to vom configuration!%n", filepath);
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


    if (getId() == null) {
      try {
        final String id = InetAddress.getLocalHost().getHostName();
        setId(id);
      }
      catch (UnknownHostException e) {
        throw new FallDownException(e);
      }
    }
  }

  public static void print() {
    if (Config.isDebugMode()) {
      System.err.printf("id: %s%n", Config.getId());
      System.err.printf("server.host: %s%n", Config.getServerHost());
      System.err.printf("server.port: %s%n", Config.getServerPort());
      System.err.printf("polling interval: %s%n", Config.getPollingInterval());
      System.err.printf("servlet packages: %s%n",
        getClassList("monitor.packages"));

      System.out.println();
    }
  }
}
