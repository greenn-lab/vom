package vom.client;

import org.objectweb.asm.ClassReader;
import vom.client.exception.FallDownException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class Config {

  private static final String VALUE_SPLIT_PATTERN = "[\\s,|]+";

  private static final String CONFIG_DEFAULT_PROPERTIES =
    System.getProperty("user.home") + "/.vom/config.properties";

  private static final Properties props = new Properties();

  private static final Set<String> packages = new HashSet<String>();

  private static final Set<String> databaseVendors = new HashSet<String>();

  private static final Set<String> DEFAULT_SERVLET_CLASSES = new HashSet<String>(
    Collections.singletonList(
      "javax/servlet/http/HttpServlet"
    )
  );

  private static final Set<String> DEFAULT_JSP_CLASSES = new HashSet<String>(
    Collections.singletonList(
      "org/apache/jasper/servlet/JspServlet"
    )
  );

  private static final Set<String> DEFAULT_JDBC_CLASSES = new HashSet<String>(
    Arrays.asList(
      "java/sql/Connection",
      "java/sql/Statement",
      "java/sql/PreparedStatement",
      "java/sql/CallableStatement"
    )
  );


  private Config() {
  }


  public static String get(String key) {
    return props.getProperty(key);
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

  public static boolean containsServletChasedTarget(String className) {
    for (final String package_ : packages) {
      if (className.startsWith(package_)) {
        return true;
      }
    }

    return false;
  }


  /**
   * Connection Pool 에서 Proxy 나 Delegating 로 SQL 객체를
   * 구성하기 때문에 불필요한 구현체들이 추출되는데, 이를 방지하기 위해
   * 벤더가 실제적으로 사용하는 Statement 구현체를 추려내는 목적으로
   * 사용해요.
   *
   * @return 지정된 데이터베이스 벤더에 포함될까, 아닐까.
   */
  public static boolean containsDatabaseVendor(String className) {
    for (final String vendor : databaseVendors) {
      if (className.contains(vendor)) {
        return true;
      }
    }

    return false;
  }

  public static boolean containsJdbcClass(final ClassReader reader) {
    for (String interface_ : reader.getInterfaces()) {
      if (DEFAULT_JDBC_CLASSES.contains(interface_)) {
        return true;
      }
    }

    return false;
  }

  public static boolean containsJSPClass(final String className) {
    return DEFAULT_JSP_CLASSES.contains(className);
  }

  public static boolean containsServletClass(final String className) {
    return DEFAULT_SERVLET_CLASSES.contains(className);
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

    Collections.addAll(
      databaseVendors,
      props.getProperty("database.vendors").split(VALUE_SPLIT_PATTERN)
    );

    for (String package_ : props.getProperty("monitor.packages").split(VALUE_SPLIT_PATTERN)) {
      package_ = package_.replace('.', '/').trim();
      if (!"".equals(package_)) {
        packages.add(package_);
      }
    }
  }

  public static void print() {
    if (Config.isDebugMode()) {
      System.err.printf("id: %s%n", Config.getId());
      System.err.printf("server.host: %s%n", Config.getServerHost());
      System.err.printf("server.port: %s%n", Config.getServerPort());
      System.err.printf("polling interval: %s%n", Config.getPollingInterval());
      System.err.printf("servlet packages: %s%n", packages.toString());

      System.out.println();
    }
  }
}
