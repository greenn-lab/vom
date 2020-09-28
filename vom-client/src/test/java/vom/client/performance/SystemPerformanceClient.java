package vom.client.performance;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import vom.client.Commons;
import vom.client.SystemPerformance;
import vom.client.SystemPerformance.Disk;
import vom.client.SystemPerformance.Network;
import vom.client.SystemPerformanceServiceGrpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static vom.client.SystemPerformance.Memory;
import static vom.client.SystemPerformance.Sensors;

class SystemPerformanceClient {
  
  public static final StreamObserver<Commons.Noop> RESPONSE_OBSERVER = new StreamObserver<Commons.Noop>() {
    @Override
    public void onNext(Commons.Noop value) {
      System.err.printf("onNext(Noop): %s\n", value.toString());
    }
    
    @Override
    public void onError(Throwable t) {
      t.printStackTrace(System.err);
    }
    
    @Override
    public void onCompleted() {
      System.err.println("END!!");
    }
  };
  
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    final ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 3506)
            .usePlaintext()
            .build();
    
    final SystemPerformanceServiceGrpc.SystemPerformanceServiceFutureStub fstub =
        SystemPerformanceServiceGrpc.newFutureStub(channel);
    
    final SystemPerformance asp = SystemPerformance.newBuilder()
        .setId("WAS-1ST")
        .setTimestamp(System.currentTimeMillis())
        .setCpu(1.23D)
        .setDisk(Disk.newBuilder().setFree(123).setTotal(456).build())
        .setMemory(Memory.newBuilder().setFree(789).setTotal(12).build())
        .setNetwork(Network.newBuilder().setReceived(345).setSent(678).build())
        .setSensors(Sensors.newBuilder().setTemperature(9.01D).setVoltage(2.34D)
            .addFanSpeed(567)
            .addFanSpeed(890).build())
        .build();
    
    final ListenableFuture<Commons.Noop> collect = fstub.collect(asp);
    collect.addListener(new Runnable() {
      @Override
      public void run() {
        System.out.println("I'm running!!!");
      }
    }, Executors.newSingleThreadExecutor());
    
    final Commons.Noop noop = collect.get();//collect.get(10, TimeUnit.SECONDS);
    
    System.err.printf("FINISH: %s", noop.toString());
  }
  
}
