package edu.brown.cs.student.main.csv_tests;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.csv.parser.CSVParser;
import edu.brown.cs.student.main.csv.parser.ListCreator;
import edu.brown.cs.student.main.csv.parser.Row;
import edu.brown.cs.student.main.csv.parser.RowCreator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * A class that tests the functionality of the CSVParser class
 */
public class CSVParserTest {

  // Parsers
  private static CSVParser fileCSVParser;
  private static CSVParser stringCSVParser;

  // Readers
  private static FileReader tenStarFR;
  private static StringReader stringReader;

  // CreatorFromRows
  private static RowCreator rowCreator;
  private static ListCreator listCreator;

  // Files
  private static final String tenStar =
      "C:\\Code\\CS320\\csv-michaeltu01\\data\\stars\\ten-star.csv";
  private static final String strCSV =
      "1998,  27,    9991\n"
          + "1997,  17,    9925\n"
          + "1998,  28,   10491\n"
          + "1998,   5,   10990\n"
          + "1997,  38,    9493";

  // Regex
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  @BeforeAll
  public static void setup() throws FileNotFoundException {
    rowCreator = new RowCreator();
    listCreator = new ListCreator();

    tenStarFR = new FileReader(tenStar);
    stringReader = new StringReader(strCSV);

    fileCSVParser = new CSVParser(tenStarFR, listCreator);
    stringCSVParser = new CSVParser(stringReader, rowCreator);
  }

  /** Tests parsing of CSV file with headers */
  @Test
  public void testParseCSVFileWithHeaders() {
    List<String> row1 = parseLineHelper("StarID,ProperName,X,Y,Z");
    List<String> row2 = parseLineHelper("0,Sol,0,0,0");
    List<String> row3 = parseLineHelper("1,,282.43485,0.00449,5.36884");
    List<String> row4 = parseLineHelper("2,,43.04329,0.00285,-15.24144");
    List<String> row5 = parseLineHelper("3,,277.11358,0.02422,223.27753");
    List<String> row6 = parseLineHelper("3759,96 G. Psc,7.26388,1.55643,0.68697");
    List<String> row7 = parseLineHelper("70667,Proxima Centauri,-0.47175,-0.36132,-1.15037");
    List<String> row8 = parseLineHelper("71454,Rigel Kentaurus B,-0.50359,-0.42128,-1.1767");
    List<String> row9 = parseLineHelper("71457,Rigel Kentaurus A,-0.50362,-0.42139,-1.17665");
    List<String> row10 = parseLineHelper("87666,Barnard's Star,-0.01729,-1.81533,0.14824");
    List<String> row11 = parseLineHelper("118721,,-2.28262,0.64697,0.29354");

    List<List<String>> csvParsed = fileCSVParser.parseCSV();
    List<List<String>> expected =
        new ArrayList<>(
            List.of(row1, row2, row3, row4, row5, row6, row7, row8, row9, row10, row11));
    assertEquals(expected, csvParsed);
  }

  /** Tests parsing of String file with no headers */
  @Test
  public void testParseStringFileWithNoHeaders() {
    List<String> row1 = parseLineHelper("1998,  27,    9991");
    List<String> row2 = parseLineHelper("1997,  17,    9925");
    List<String> row3 = parseLineHelper("1998,  28,   10491");
    List<String> row4 = parseLineHelper("1998,   5,   10990");
    List<String> row5 = parseLineHelper("1997,  38,    9493");

    Row rrow1 = new Row(row1);
    Row rrow2 = new Row(row2);
    Row rrow3 = new Row(row3);
    Row rrow4 = new Row(row4);
    Row rrow5 = new Row(row5);

    List<Row> csvParsed = stringCSVParser.parseCSV();
    List<Row> expected = new ArrayList<>(List.of(rrow1, rrow2, rrow3, rrow4, rrow5));
    assertEquals(expected, csvParsed);
  }

  /**
   * Parse each line into a List<String>
   *
   * @param line - a row of the CSV as a String
   * @return the row parsed into a List<String>
   */
  private List<String> parseLineHelper(String line) {
    String[] row = regexSplitCSVRow.split(line);
    List<String> rowAsLst = new ArrayList<>();
    for (String str : row) {
      rowAsLst.add(str.trim());
    }
    return rowAsLst;
  }
}
