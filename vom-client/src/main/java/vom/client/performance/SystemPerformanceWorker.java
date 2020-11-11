package vom.client.performance;

import vom.client.Config;
import vom.client.connector.CollectorConnection;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class SystemPerformanceWorker extends Thread implements Serializable {

  private boolean running = true;


  public SystemPerformanceWorker() {
    setName("system-performance-worker");
    setDaemon(true);
  }

  @Override
  public void run() {
    while (true) {
      final double cpu = SystemPerformanceService.getCpu();
      final long[] disk = SystemPerformanceService.getDisk();
      final long[] memory = SystemPerformanceService.getMemory();
      final long[] network = SystemPerformanceService.getNetwork();

      try {
        CollectorConnection.sendSystemPerf(cpu, disk, memory, network);
      }
      catch (Throwable cause) {
        // no works
      }

      try {
        TimeUnit.SECONDS.sleep(Config.getServerPullGap());
      }
      catch (InterruptedException e) {
        this.interrupt();
        e.printStackTrace();
      }

      if (!running) {
        break;
      }
    }
  }

  public void die() {
    running = false;
  }

}
