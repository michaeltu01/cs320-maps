package edu.brown.cs.student.main.server.exceptions;

/**
 * Class for errors when user makes invalid requests that cannot be processed.
 */
public class BadRequestException extends Exception {
  private final Throwable cause;

  public BadRequestException(String message) {
    super(message);
    this.cause = null;
  }

  public BadRequestException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}
