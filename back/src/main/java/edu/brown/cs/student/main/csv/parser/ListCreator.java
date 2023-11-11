package edu.brown.cs.student.main.csv.parser;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** Implements CreatorFromRow to create Lists of Strings */
public class ListCreator implements CreatorFromRow<List<String>> {

  /**
   * Create a List object from a List<String>
   *
   * @param row - a List of String values from a given CSV row
   * @return a ArrayList<String> object contains the given List<String>
   * @throws FactoryFailureException - List object creation failed at any point
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (row == null) {
      throw new FactoryFailureException("Row not found", null);
    }
    return new ArrayList<String>(row);
  }
}
