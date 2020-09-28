package vom.client.performance;

import vom.client.Config;

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
