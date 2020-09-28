package vom.client.exception;

public class FallDownException extends RuntimeException {
  
  public FallDownException(Throwable e) {
    super(e);
    e.printStackTrace(System.err);
  }

}
