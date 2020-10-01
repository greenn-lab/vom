package vom.client;

import vom.client.exception.FallDownException;
import org.objectweb.asm.Opcodes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class Config {
  
  public static final int ASM_VERSION = Opcodes.ASM7;
  
  protected static final Set<String> DEFAULT_JDBC_CLASSES = new HashSet<String>();
  public static final Set<String> DEFAULT_SERVLET_CLASSES = new HashSet<String>();
  
  private static final Properties props = new Properties();
  private static final Long id;
  private static final Set<String> packages;
  
  static {
    DEFAULT_SERVLET_CLASSES.add("javax/servlet/http/HttpServlet");
    
    DEFAULT_JDBC_CLASSES.add("javax/sql/Statement");
    DEFAULT_JDBC_CLASSES.add("javax/sql/PreparedStatement");
    DEFAULT_JDBC_CLASSES.add("javax/sql/CallableStatement");
    
    // 설정파일을 통해서 초기 설정을 구성해요.
    configure();
    
    id = Long.parseLong(props.getProperty("id"));

    packages = new HashSet<String>();
    for (String pkg : props.getProperty("monitor.packages").split("[\\s,|]+")) {
      packages.add(pkg.replace('.', '/'));
    }
  }
  
  
  private Config() {
  }
  
  
  public static Long getId() {
    return id;
  }
  
  public static int getPollingInterval() {
    return Integer.parseInt(props.getProperty("polling.interval"));
  }
  
  public static void setId(String name) {
    if (name != null && !name.trim().equals("")) {
      props.setProperty("id", name.trim());
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
  
  public static void configure() {
    InputStream in = null;
    
    try {
      final URL propFileUrl = ClassLoader.getSystemResource("default-config.properties");
      
      in = propFileUrl.openStream();
      props.load(in);
      
      in.close();
      in = new FileInputStream(System.getProperty("user.home") + "/.vom/default-config.properties");
      
      final Properties userProps = new Properties();
      userProps.load(in);
      
      for (final String name : userProps.stringPropertyNames()) {
        props.setProperty(name, userProps.getProperty(name));
      }
    }
    catch (IOException e) {
      // no work
    }
    finally {
      if (in != null) {
        try {
          in.close();
        }
        catch (IOException e) {
          e.printStackTrace(System.err);
        }
      }
    }
    
    if (getId() == null || "".equals(getId())) {
      try {
        props.setProperty("id", InetAddress.getLocalHost().getHostName());
      }
      catch (UnknownHostException e) {
        throw new FallDownException(e);
      }
    }
  }
  
}
