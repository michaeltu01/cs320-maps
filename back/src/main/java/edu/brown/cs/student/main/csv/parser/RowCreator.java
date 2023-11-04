package edu.brown.cs.student.main.csv.parser;

import edu.brown.cs.student.main.csv.exceptions.FactoryFailureException;
import java.util.List;

/**
 * Extends CreatorFromRow to create Row objects
 */
public class RowCreator implements CreatorFromRow<Row> {

  /**
   * Create a Row object from a List<String>
   *
   * @param row - a List of String values from a given CSV row
   * @return a Row object contains the given List<String>
   * @throws FactoryFailureException - Row object creation failed at any point
   */
  @Override
  public Row create(List<String> row) throws FactoryFailureException {
    if (row == null) {
      throw new FactoryFailureException("Row not found", null);
    }
    return new Row(row);
  }
}
