package edu.brown.cs.student.main.server.census;

import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;

/**
 * Interface for CensusDataSources including the MockAPI and ACSAPIDataSource.
 */
public interface CensusDataSource {

  /**
   * This method retrieves the percentage of households with broadband access for a target location
   * given by the target state and county
   * @param state the target state
   * @param county the target county
   * @return CensusData object of the target state and county
   * @throws DatasourceException if retrieval from the data source fails
   */
  CensusData getBroadbandPct(String state, String county)
      throws DatasourceException, BadJsonException;

}
