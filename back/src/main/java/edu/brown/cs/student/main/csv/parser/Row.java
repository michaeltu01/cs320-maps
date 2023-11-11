package edu.brown.cs.student.main.csv.parser;

import java.util.List;

/** Object that represents a row in a CSV */
public class Row {
  private List<String> values;

  /**
   * Constructor for the Row object class
   *
   * @param valuesLst - given list of Strings after being parsed by CSVParser
   */
  public Row(List<String> valuesLst) {
    this.values = valuesLst;
  }

  /**
   * Converts the Row object into a String format
   *
   * @return formatted String
   */
  @Override
  public String toString() {
    String output = "|";
    for (String str : values) {
      output += " " + str + " |";
    }
    return output;
  }

  /**
   * Checks whether this Row object contains the given lowercase String
   *
   * @param lowerCaseStr - a String in lowercase
   * @return true if the String is found; if not, returns false
   */
  public boolean contains(String lowerCaseStr) {
    for (String val : this.values) {
      if (val.toLowerCase().equals(lowerCaseStr)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Uses the java.util.List.get() method to get a String given an index
   *
   * @param idx - index of String in list
   * @return the String at the given index
   */
  public String get(int idx) throws IndexOutOfBoundsException {
    return this.values.get(idx);
  }

  /**
   * Obtains the index of a given String in lower case
   *
   * @param lowerCaseStr - String in lower case to search for
   * @return the index of the given String; -1 if String not found
   */
  public int indexOf(String lowerCaseStr) {
    for (int i = 0; i < this.values.size(); i++) {
      if (values.get(i).toLowerCase().equals(lowerCaseStr)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Class-specific equals method for Row objects
   *
   * @param o - a given Object to compare to
   * @return true if this Row has the same values as Object o; false, if not
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Row row = (Row) o;
    if (this.values.size() != row.values.size()) {
      return false;
    }
    for (int i = 0; i < this.values.size(); i++) {
      if (!this.values.get(i).equals(row.values.get(i))) {
        return false;
      }
    }
    return true;
  }
}
