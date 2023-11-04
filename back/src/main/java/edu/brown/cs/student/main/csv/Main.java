package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.exceptions.InvalidArgumentsException;
import edu.brown.cs.student.main.csv.parser.Row;
import edu.brown.cs.student.main.csv.searcher.Searcher;
import java.util.List;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args)
      throws ArrayIndexOutOfBoundsException, InvalidArgumentsException {
    // Argument format: [filename] [search value] [header boolean value] [column id]
    // Use double quotes for arguments with spaces
    Searcher searchUtil = new Searcher();
    try {
      if (args.length > 4) {
        throw new InvalidArgumentsException("Too many arguments");
      }
      // Parse file name and search value arguments
      String filename = args[0];
      String searchValue = args[1];
      // Parse header boolean
      String headerBoolAsString = args[2];
      boolean hasHeader;
      // Error handling for invalid input
      if (headerBoolAsString.toLowerCase().equals("true")) {
        hasHeader = true;
      } else if (headerBoolAsString.toLowerCase().equals("false")) {
        hasHeader = false;
      } else {
        throw new InvalidArgumentsException("Header existence not specified");
      }
      if (args.length == 3) {
        List<Row> searchAllResults = searchUtil.search(filename, searchValue, hasHeader, null);
        if (searchAllResults.isEmpty()) {
          System.out.println("No results found.");
        } else {
          for (Row r : searchAllResults) {
            System.out.println(r);
          }
        }
      } else {
        String columnId = args[3];
        List<Row> searchColResults = searchUtil.search(filename, searchValue, hasHeader, columnId);
        if (searchColResults.isEmpty()) {
          System.out.println("No results found.");
        } else {
          for (Row r : searchColResults) {
            System.out.println(r);
          }
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("Invalid arguments");
    }
  }
}
