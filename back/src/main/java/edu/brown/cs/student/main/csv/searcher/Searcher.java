package edu.brown.cs.student.main.csv.searcher;

import edu.brown.cs.student.main.csv.parser.CSVParser;
import edu.brown.cs.student.main.csv.parser.Row;
import edu.brown.cs.student.main.csv.parser.RowCreator;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements search functionality (User Story 1)
 */
public class Searcher {

  /**
   * Parse the file into a List<Row>
   * @param filename - the name of the file to parse
   *
   * @return the list of all the Rows in the file
   */
  public List<Row> parseFile(String filename) {
    try (FileReader fileReader = new FileReader(new File(filename))) {
      RowCreator rowCreator = new RowCreator();
      CSVParser csvParser = new CSVParser(fileReader, rowCreator);
      return csvParser.parseCSV();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(0);
    }

    return null; // should never run
  }

  /**
   * Search the file for the target value (case-independent)
   * @param filename - name of the file to search
   * @param searchVal - the value to search for
   * @param hasHeaders - true if the file has headers in the first row; false, if not
   * @param columnId - specific column to search within; could be an index or the column name
   *
   * @return the List of Rows containing the given value
   */
  public List<Row> search(String filename, String searchVal, boolean hasHeaders, String columnId) {
    Row headers = null;
    List<Row> csvRows = this.parseFile(filename);
    List<Row> searchResults = new ArrayList<Row>();
    if (hasHeaders) {
      headers = csvRows.remove(0); // removes the header row into a separate Row object
    }
    if (columnId == null) {
      for (Row r : csvRows) {
        if (r.contains(searchVal.toLowerCase())) {
          searchResults.add(r);
        }
      }
      return searchResults;
    } else {
      try {
        int columnIndex = Integer.valueOf(columnId);
        for (Row r : csvRows) {
          if (r.get(columnIndex).toLowerCase().equals(searchVal.toLowerCase())) {
            searchResults.add(r);
          }
        }
        return searchResults;
      } catch (NumberFormatException e) {
        int columnIndex = headers.indexOf(columnId.toLowerCase());
        if (columnIndex != -1) {
          for (Row r : csvRows) {
            if (r.get(columnIndex).toLowerCase().equals(searchVal.toLowerCase()))
            // r.get(columnIndex) should never return IndexOutOfBoundsException
            {
              searchResults.add(r);
            }
          }
          return searchResults;
        } else {
          System.err.println("Column identifier not found when searching.");
          System.exit(0);
        }
      } catch (IndexOutOfBoundsException e) {
        System.err.println("Column index out of bounds.");
        System.exit(0);
      }
    }
    return searchResults;
  }

  /**
   * Searches for a specific value in the entire given 2D List of Strings (without header row)
   * @param data 2D List of Strings representing CSV
   * @param searchVal the String to search for in the data
   * @return the rows containing the value
   */
  public List<List<String>> search(List<List<String>> data, String searchVal) {
    List<List<String>> searchResults = new ArrayList<>();
    for (List<String> row : data) {
      for (String str : row) {
        if (str.toLowerCase().equals(searchVal.toLowerCase())) {
          searchResults.add(row);
        }
      }
    }
    return searchResults;
  }

  /**
   * Searches for a specific value in the given 2D List of Strings by column index (without header row)
   * @param data 2D List of Strings representing CSV
   * @param searchVal the String to search for in the data
   * @param colIndex the name of the column to search for the value
   * @return the rows containing the value
   */
  public List<List<String>> search(List<List<String>> data, String searchVal, int colIndex) {
    List<List<String>> searchResults = new ArrayList<>();
    for (List<String> row : data) {
      if (row.get(colIndex).toLowerCase().equals(searchVal.toLowerCase())) {
        searchResults.add(row);
      }
    }
    return searchResults;
  }
}
