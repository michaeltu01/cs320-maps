package edu.brown.cs.student.main.server_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.server.census.ACSAPIDataSource;
import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import org.junit.jupiter.api.Test;

public class ACSAPIDataSourceTests {

  private ACSAPIDataSource source;

  /**
   * Tests accessing ACS API to get broadband percent
   *
   * @throws DatasourceException
   * @throws BadJsonException
   */
  @Test
  public void testGetBroadbandPercent() throws DatasourceException, BadJsonException {
    source = new ACSAPIDataSource();

    String state = "California";
    String county = "Los Angeles County";

    assertEquals(89.9, source.getBroadbandPct(state, county).broadbandPct());

    state = "Virginia";
    county = "Virginia Beach City";

    assertEquals(92.0, source.getBroadbandPct(state, county).broadbandPct());
  }

  /**
   * Test whether the bad JSON exception throws when the state/county cannot be found
   *
   * @throws DatasourceException
   */
  @Test
  public void testBadJson() throws DatasourceException {
    source = new ACSAPIDataSource();

    String state = "California";
    String county = "Virginia Beach City";
    BadJsonException expected =
        assertThrows(BadJsonException.class, () -> source.getBroadbandPct(state, county));
    assertEquals(
        expected.getMessage(),
        "The county that you have given cannot be found: Virginia Beach City, California");

    String misspelledState = "Birginia";

    expected =
        assertThrows(BadJsonException.class, () -> source.getBroadbandPct(misspelledState, county));
    assertEquals(expected.getMessage(), "The state you have given cannot be found: Birginia");
  }
}
