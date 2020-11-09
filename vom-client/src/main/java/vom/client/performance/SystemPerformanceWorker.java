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
      long[] disk = SystemPerformanceService.getDisk();
      long[] memory = SystemPerformanceService.getMemory();
      long[] network = SystemPerformanceService.getNetwork();

      try {
        CollectorConnection.sendSystemPerf(
          SystemPerformanceService.getCpu(), disk, memory, network
        );
      }
      catch (Throwable cause) {
        // no work
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
