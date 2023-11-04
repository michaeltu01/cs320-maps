package edu.brown.cs.student.main.csv.parser;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implements parsing functionality (User Story 2)
 * @param <T> - a generic object to turn the parsed rows into via CreatorFromRow interface
 */
public class CSVParser<T> {
  private BufferedReader buffReader;
  private CreatorFromRow<T> rowCreator;

  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private final String removeQuotes = "\"(.*?)\"";

  /** Constructor for CSVParser class */
  public CSVParser(Reader csvReader, CreatorFromRow<T> rowCreator) {
    this.buffReader = new BufferedReader(csvReader);
    this.rowCreator = rowCreator;
  }

  /**
   * Parses the CSV and creates row objects according to the CreatorFromRow object
   *
   * @return a List of generic object type T containing the rows of the CSV
   */
  public List<T> parseCSV() {
    List<T> listOfRows = new ArrayList<T>();
    try {
      String line = buffReader.readLine();
      while (line != null) {
        List<String> row = parseLine(line);
        T rowObj = rowCreator.create(row);
        listOfRows.add(rowObj);

        line = buffReader.readLine();
      }
    } catch (IOException e) {
      System.err.println(e.getStackTrace());
    } catch (FactoryFailureException e) {
      System.err.println(e.getStackTrace());
    }
    return listOfRows;
  }

  /**
   * Parse each line into a List<String>
   *
   * @param line - a row of the CSV as a String
   * @return the row parsed into a List<String>
   */
  private List<String> parseLine(String line) {
    String[] row = regexSplitCSVRow.split(line);
    List<String> rowAsLst = new ArrayList<>();
    for (String str : row) {
      rowAsLst.add(str.trim().replaceAll(removeQuotes, "$1"));
    }
    return rowAsLst;
  }
}
