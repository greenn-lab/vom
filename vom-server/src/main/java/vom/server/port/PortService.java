package vom.server.port;

import io.grpc.stub.StreamObserver;
import vom.client.Commons.Noop;

public interface PortService {
  
  Noop NOOP = Noop.newBuilder().build();

  static void noopCompleted(StreamObserver<Noop> streamObserver) {
    streamObserver.onNext(NOOP);
    streamObserver.onCompleted();
    
  }
  
}
