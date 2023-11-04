package edu.brown.cs.student.main.server.census;

import java.time.LocalDateTime;

import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;

/**
 * Class which helps us use a MockAPIDataSource.
 */
public class MockAPIDataSource implements CensusDataSource {

  /**
   * This method returns a constant broadband percentage for testing purposes
   *
   * @param state  the target state
   * @param county the target county
   * @return CensusData object of the target state and county
   * @throws DatasourceException if retrieval from the data source fails
   */
  @Override
  public CensusData getBroadbandPct(String state, String county)
      throws DatasourceException, BadJsonException {
    return new CensusData(46.7, "");
  }
}
