package qualtrix.exceptions;

public class RateLimitAcquireFailed extends Exception {
  public RateLimitAcquireFailed(String msg) {
    super(msg);
  }
}
