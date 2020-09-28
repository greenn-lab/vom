package vom.client.port;

import com.google.common.util.concurrent.ListenableFuture;
import vom.client.Commons.Noop;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoopListenableService {
  
  public static void listening(@SuppressWarnings("unused") ListenableFuture<Noop> streamObserver) {
    // no work
  }
  
}
