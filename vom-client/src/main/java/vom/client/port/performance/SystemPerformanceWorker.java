package vom.client.port.performance;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vom.client.Commons.Noop;
import vom.client.Config;
import vom.client.SystemPerformance;
import vom.client.SystemPerformance.Disk;
import vom.client.SystemPerformance.Memory;
import vom.client.SystemPerformanceServiceGrpc;
import vom.client.SystemPerformanceServiceGrpc.SystemPerformanceServiceFutureStub;
import vom.client.port.NoopListenableService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SystemPerformanceWorker extends Thread {
  
  private static final ManagedChannel channel;
  private static final SystemPerformanceServiceFutureStub stub;
  
  static {
    final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    final ManagedChannelBuilder<?> builder = ManagedChannelBuilder
        .forAddress(Config.getServerHost(), Config.getServerPort())
        .executor(executor)
        .usePlaintext();
    
    channel = builder.build();
    stub = SystemPerformanceServiceGrpc.newFutureStub(channel);

    Runtime.getRuntime().addShutdownHook(new Thread("gRPC-shutdown-hook") {
      @Override
      public void run() {
        executor.shutdownNow();

        try {
          if (!executor.awaitTermination(Config.getPollingInterval(), TimeUnit.SECONDS)) {
            executor.shutdown();
          }
        }
        catch (InterruptedException e) {
          this.interrupt();
          e.printStackTrace();
        }
      }
    });
  }
  
  private boolean running = true;
  
  
  public SystemPerformanceWorker() {
    setName("system-performance-worker");
    setDaemon(true);
  }
  
  @Override
  public void run() {
    while (!channel.isTerminated() && !channel.isShutdown()) {
      final long[] disk = SystemPerformanceService.getDisk();
      final long[] memory = SystemPerformanceService.getMemory();
      final long[] network = SystemPerformanceService.getNetwork();
      
      final ListenableFuture<Noop> collect = stub.collect(
          SystemPerformance.newBuilder()
              .setId(Config.getId())
              .setTimestamp(System.currentTimeMillis())
              .setCpu(SystemPerformanceService.getCpu())
              .setDisk(Disk.newBuilder().setTotal(disk[0]).setFree(disk[1]).build())
              .setMemory(Memory.newBuilder().setTotal(memory[0]).setFree(memory[1]).build())
              .setNetwork(SystemPerformance.Network.newBuilder().setSent(network[1]).setReceived(network[2]).build())
              .build()
      );
      
      NoopListenableService.listening(collect);
      
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
