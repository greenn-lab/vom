package vom.client.chaser;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vom.client.Booty;
import vom.client.ChaserServiceGrpc;
import vom.client.ChaserServiceGrpc.ChaserServiceFutureStub;
import vom.client.Commons;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ChaserClient {
  
  public static void main(String[] args) throws InterruptedException {
    final ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 3506)
        .usePlaintext()
        .build();
    
    statusChanged(channel);
    
    final Booty web = Booty.newBuilder()
        .setId("WAS-2ND")
        .setTimestamp(System.currentTimeMillis())
        .setUri("/pseudo/test")
        .putParameter("ping", "hello")
        .putParameter("pong", "hi")
        .putHeader("head", "merry")
        .putHeader("arms", "8")
        .setError("error!")
        .build();
    
    while (!channel.isTerminated()) {
      final ConnectivityState state = channel.getState(true);
      System.out.println(state.toString());
      
      //channel.awaitTermination(1, TimeUnit.SECONDS);
      
      
      final ChaserServiceFutureStub stub = ChaserServiceGrpc.newFutureStub(channel);
      final ListenableFuture<Commons.Noop> noopListenableFuture = stub.collectWeb(web);
      noopListenableFuture.addListener(new Runnable() {
        @Override
        public void run() {
          System.out.printf("[%s]listened\n", Thread.currentThread().getName());
        }
      }, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
      
      
      TimeUnit.SECONDS.sleep(1);
    }
    
    
  }
  
  private static void statusChanged(ManagedChannel channel) {
    channel.notifyWhenStateChanged(ConnectivityState.CONNECTING, new Runnable() {
      @Override
      public void run() {
        System.out.printf("[%s]status: %s\n", Thread.currentThread().getName(), "CONNECTING");
      }
    });
    
    channel.notifyWhenStateChanged(ConnectivityState.READY, new Runnable() {
      @Override
      public void run() {
        System.out.printf("[%s]status: %s\n", Thread.currentThread().getName(), "READY");
      }
    });
    
    channel.notifyWhenStateChanged(ConnectivityState.IDLE, new Runnable() {
      @Override
      public void run() {
        System.out.printf("[%s]status: %s\n", Thread.currentThread().getName(), "IDLE");
      }
    });
  }
  
}
