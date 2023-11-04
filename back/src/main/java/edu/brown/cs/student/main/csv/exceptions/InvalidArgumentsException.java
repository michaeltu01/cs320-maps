package edu.brown.cs.student.main.csv.exceptions;

/** This is an error provided to catch any error that may occur in the user's given arguments. */
public class InvalidArgumentsException extends Exception {
  public InvalidArgumentsException(String message) {
    super(message);
  }
}
