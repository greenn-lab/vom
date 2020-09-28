package vom.server.port.performance;

import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import vom.client.Commons.Noop;
import vom.client.SystemPerformance;
import vom.client.SystemPerformanceServiceGrpc.SystemPerformanceServiceImplBase;
import vom.server.port.PortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class SystemPerformanceServiceImpl extends SystemPerformanceServiceImplBase {
  
  @Override
  public void collect(SystemPerformance request, StreamObserver<Noop> responseObserver) {
    try (
        final FileOutputStream out = new FileOutputStream("D:/.system-performance", true)
    ) {
      request.writeTo(out);
      logger.info("collect: {}", JsonFormat.printer().print(request));
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    PortService.noopCompleted(responseObserver);
  }
}
