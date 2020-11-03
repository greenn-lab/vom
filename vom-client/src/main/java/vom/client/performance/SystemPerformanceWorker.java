package vom.client.performance;

import vom.client.Config;
import vom.client.connector.ServerConnection;

import java.util.concurrent.TimeUnit;

public class SystemPerformanceWorker extends Thread {

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
        ServerConnection.sendSystemStats(
          SystemPerformanceService.getCpu(), disk, memory, network
        );
      }
      catch (Throwable cause) {
        // no work
      }

      try {
        TimeUnit.SECONDS.sleep(Config.getPollingInterval());
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
