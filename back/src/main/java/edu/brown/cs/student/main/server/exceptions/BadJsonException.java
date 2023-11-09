package edu.brown.cs.student.main.server.exceptions;

/**
 * Class for errors in /broadband endpoint when state/county cannot be found, so the results JSON
 * cannot be properly retrieved from the ACS API
 */
public class BadJsonException extends Exception {
  private final Throwable cause;

  public BadJsonException(String message) {
    super(message);
    this.cause = null;
  }

  public BadJsonException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}
