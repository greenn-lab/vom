package vom.client.performance;

import vom.client.Config;
import vom.client.connector.ServerConnection;

import java.util.Arrays;
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
      try {
        final long[] disk = SystemPerformanceService.getDisk();
        System.out.println(Arrays.toString(disk));
        final long[] memory = SystemPerformanceService.getMemory();
        System.out.println(Arrays.toString(memory));
        final long[] network = SystemPerformanceService.getNetwork();
        System.out.println(Arrays.toString(network));

        ServerConnection.sendSystemStats(
          SystemPerformanceService.getCpu(), disk, memory, network
        );

        System.out.println("fall in sleep");
        TimeUnit.SECONDS.sleep(Config.getPollingInterval());
      }
      catch (InterruptedException e) {
        e.printStackTrace(System.err);
        Thread.currentThread().interrupt();
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
