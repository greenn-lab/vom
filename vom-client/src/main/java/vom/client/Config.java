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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Config {

  private static final String VALUE_SPLIT_PATTERN = "[\\s,|]+";

  private static final String CONFIG_DEFAULT_PROPERTIES =
    System.getProperty("user.home") + "/.vom/config.properties";

  private static final Properties props = new Properties();

  private static final Map<String, List<String>> listConfigCaches =
    new HashMap<String, List<String>>(5);


  public static String get(String key) {
    return props.getProperty(key);
  }

  public static List<String> getList(String key) {
    if (listConfigCaches.containsKey(key)) {
      return listConfigCaches.get(key);
    }

    listConfigCaches.put(key, Arrays.asList(
      props.getProperty(key).split(VALUE_SPLIT_PATTERN)
    ));

    return getList(key);
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
          props.setProperty(name,
            name.startsWith("classes.")
            ? props.getProperty(name) + " " + value
            : value);
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
        getList("packages.chase"));

      System.out.println();
    }
  }
}
