package vom.client.exception;

public class CarryException extends RuntimeException {

  public CarryException(String message) {
    super(message);
  }

  public CarryException(Throwable cause) {
    super(cause);
  }

  public CarryException(String message, Throwable cause) {
    super(message, cause);
  }
}
