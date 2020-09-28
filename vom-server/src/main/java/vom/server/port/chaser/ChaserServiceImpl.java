package vom.server.port.chaser;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vom.client.Booty;
import vom.client.ChaserServiceGrpc.ChaserServiceImplBase;
import vom.client.Commons.Noop;
import vom.server.port.PortService;

@Service
@Slf4j
public class ChaserServiceImpl extends ChaserServiceImplBase {
  
  @Override
  public void collectWeb(Booty request, StreamObserver<Noop> responseObserver) {
    try {
      logger.info("collect web: {}", JsonFormat.printer().print(request));
    }
    catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
    
    PortService.noopCompleted(responseObserver);
  }
}
