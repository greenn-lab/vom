package vom.client;

import vom.client.bci.VOMClassFileTransformer;
import vom.client.performance.SystemPerformanceWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;

public class Bootstrap {

  static {
    Config.configure();
    welcome();
  }

  private Bootstrap() {
  }

  /**
   * JVMTI(JVM Tool Interface)로 시작해요.
   *
   * @param configFilepath  -javaagent:jar-path=<설정파일경로>
   * @param instrumentation 기본 도구에요.
   */
  public static void premain(String configFilepath, Instrumentation instrumentation) {
    if (configFilepath != null) {
      Config.mergeProperties(configFilepath);
    }

    Config.print();

    instrumentation.addTransformer(
      new VOMClassFileTransformer(),
      true
    );

    bootSystemPerformance();
  }

  private static void welcome() {
    final InputStream in = ClassLoader.getSystemResourceAsStream("META-INF/welcome.txt");

    if (in != null) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      try {
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      }
      catch (IOException e) {
        // no work
      }
    }
  }

  private static void bootSystemPerformance() {
    final SystemPerformanceWorker worker = new SystemPerformanceWorker();

    worker.start();

    Runtime.getRuntime().addShutdownHook(
      new Thread("system-performance-worker-shutdown-hook") {
        @Override
        public void run() {
          worker.die();
        }
      }
    );

  }

}
