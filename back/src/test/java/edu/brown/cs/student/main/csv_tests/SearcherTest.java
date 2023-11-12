package edu.brown.cs.student.main.csv_tests;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.csv.parser.Row;
import edu.brown.cs.student.main.csv.searcher.Searcher;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;

/** A class that tests the functionality of the Searcher class/utility program */
public class SearcherTest {

  private static Searcher searchUtil;
  private final String tenStar =
      "/Users/isaacyi/Desktop/CSCI0320/maps-iyi3-mstu/back/data/stars/ten-star.csv";
  private final String postSec =
      "/Users/isaacyi/Desktop/CSCI0320/maps-iyi3-mstu/back/data/census/postsecondary_education.csv";
  private final String doiRI =
      "/Users/isaacyi/Desktop/CSCI0320/maps-iyi3-mstu/back/data/census/dol_ri_earnings_disparity.csv";
  private final String nonHeader =
      "/Users/isaacyi/Desktop/CSCI0320/maps-iyi3-mstu/back/data/test/non-header.csv";

  @BeforeAll
  public static void setup() {
    searchUtil = new Searcher();
  }

  /** Tests for searching a String value that's present */
  @org.junit.jupiter.api.Test
  public void testStringSearchValuePresent() {
    Row row1 = new Row(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));
    List<Row> expected = new ArrayList<>(List.of(row1));
    List<Row> actual = searchUtil.search(tenStar, "Proxima Centauri", true, null);

    assertTrue(listEquals(expected, actual));
  }

  /** Tests for searching an Integer value that's present */
  @org.junit.jupiter.api.Test
  public void testNumberSearchValuePresent() {
    Row row1 = new Row(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));
    List<Row> expected = new ArrayList<>(List.of(row1));
    List<Row> actual = searchUtil.search(tenStar, "70667", true, null);
    List<Row> actualNeg = searchUtil.search(tenStar, "-0.36132", true, null);

    assertTrue(listEquals(expected, actual));
    assertTrue(listEquals(expected, actualNeg));
  }

  /** Tests for multiple rows that contain a given search value */
  @org.junit.jupiter.api.Test
  public void testMultipleSearchValuesPresent() {
    Row row1 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "214",
                "brown-university",
                "0.069233258",
                "Men",
                "1"));
    Row row2 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "235",
                "brown-university",
                "0.076027176",
                "Women",
                "2"));
    List<Row> expected = new ArrayList<>(List.of(row1, row2));
    List<Row> actual = searchUtil.search(postSec, "Asian", true, null);
    List<Row> actualIndex = searchUtil.search(postSec, "Asian", true, "0");
    List<Row> actualColId = searchUtil.search(postSec, "Asian", true, "IPEDS Race");

    assertTrue(listEquals(expected, actual));
    assertTrue(listEquals(expected, actualIndex));
    assertTrue(listEquals(expected, actualColId));

    List<Row> actualEmpStr = searchUtil.search(tenStar, "", true, null);
    Row row3 = new Row(List.of("1", "", "282.43485", "0.00449", "5.36884"));
    Row row4 = new Row(List.of("2", "", "43.04329", "0.00285", "-15.24144"));
    Row row5 = new Row(List.of("3", "", "277.11358", "0.02422", "223.27753"));
    Row row6 = new Row(List.of("118721", "", "-2.28262", "0.64697", "0.29354"));
    List<Row> expectedEmpStr = new ArrayList<>(List.of(row3, row4, row5, row6));

    assertTrue(listEquals(expectedEmpStr, actualEmpStr));
  }

  /** Tests for searching by column id */
  @org.junit.jupiter.api.Test
  public void testSearchValuesByCol() {
    Row row1 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "214",
                "brown-university",
                "0.069233258",
                "Men",
                "1"));
    Row row2 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "235",
                "brown-university",
                "0.076027176",
                "Women",
                "2"));
    List<Row> expected = new ArrayList<>(List.of(row1, row2));
    List<Row> actualIndex = searchUtil.search(postSec, "Asian", true, "0");
    List<Row> actualColId = searchUtil.search(postSec, "Asian", true, "IPEDS Race");

    assertTrue(listEquals(expected, actualIndex));
    assertTrue(listEquals(expected, actualColId));

    Row row3 = new Row(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));
    List<Row> expected1 = new ArrayList<>(List.of(row3));
    List<Row> actual1 = searchUtil.search(tenStar, "70667", true, "StarID");
    List<Row> actualNeg = searchUtil.search(tenStar, "-0.36132", true, "3");

    assertTrue(listEquals(expected1, actual1));
    assertTrue(listEquals(expected1, actualNeg));
  }

  /** Tests for searching when a value is not present in the entire CSV */
  @org.junit.jupiter.api.Test
  public void testSearchValueNotPresent() {
    List<Row> expected = new ArrayList<>();
    List<Row> actual1 = searchUtil.search(tenStar, "Proxima Centauri", true, null);
    List<Row> actual2 = searchUtil.search(doiRI, "-1000", true, null);
    List<Row> actual3 = searchUtil.search(postSec, "1", true, "University");
    List<Row> actual4 = searchUtil.search(postSec, "Asian", true, "1");

    Row row1 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "214",
                "brown-university",
                "0.069233258",
                "Men",
                "1"));
    Row row2 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "235",
                "brown-university",
                "0.076027176",
                "Women",
                "2"));
    List<Row> incorrectLst = new ArrayList<>(List.of(row1, row2));

    assertFalse(listEquals(expected, actual1));
    assertTrue(listEquals(expected, actual2));
    assertTrue(listEquals(expected, actual3));
    assertFalse(listEquals(incorrectLst, actual4));
  }

  /** Tests for searching when a value is not present in the specified column */
  @org.junit.jupiter.api.Test
  public void testSearchValueNotPresentInCol() {
    List<Row> expected = new ArrayList<>();
    List<Row> actual1 = searchUtil.search(postSec, "1", true, "University");
    List<Row> actual2 = searchUtil.search(postSec, "Asian", true, "1");

    Row row1 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "214",
                "brown-university",
                "0.069233258",
                "Men",
                "1"));
    Row row2 =
        new Row(
            List.of(
                "Asian",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "235",
                "brown-university",
                "0.076027176",
                "Women",
                "2"));
    List<Row> incorrectLst = new ArrayList<>(List.of(row1, row2));

    assertTrue(listEquals(expected, actual1));
    assertFalse(listEquals(incorrectLst, actual2));
  }

  /** Tests searching on a CSV with no headers */
  @org.junit.jupiter.api.Test
  public void testSearchInFileWithoutHeaders() {
    List<Row> actual1 = searchUtil.search(nonHeader, "1994", false, null);
    List<Row> actual2 = searchUtil.search(nonHeader, "5994", false, null);
    List<Row> actual3 = searchUtil.search(nonHeader, "7", false, "1");

    Row row1 = new Row(List.of("1994", "61", "5500"));
    Row row2 = new Row(List.of("1995", "72", "5994"));
    Row row3 = new Row(List.of("1993", "72", "5994"));
    Row row4 = new Row(List.of("1998", "7", "11000"));

    List<Row> expected1 = new ArrayList<>(List.of(row1));
    List<Row> expected2 = new ArrayList<>(List.of(row2, row3));
    List<Row> expected3 = new ArrayList<>(List.of(row4));

    assertTrue(listEquals(expected1, actual1));
    assertTrue(listEquals(expected2, actual2));
    assertTrue(listEquals(expected3, actual3));
  }

  public void ExtraConstructor1Test() {}

  /**
   * Checks for equality in every value of the List
   *
   * @param lst1 - a List<Row>
   * @param lst2 - another List<Row>
   * @return true if all Rows contain the same values; if not, return false
   */
  public boolean listEquals(List<Row> lst1, List<Row> lst2) {
    if (lst1.size() != lst2.size()) {
      return false;
    }
    for (int i = 0; i < lst1.size(); i++) {
      if (!lst1.get(i).equals(lst2.get(i))) {
        return false;
      }
    }
    return true;
  }
}
