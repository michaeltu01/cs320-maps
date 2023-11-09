package edu.brown.cs.student.main.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.server.census.CensusData;
import edu.brown.cs.student.main.server.census.CensusDataSource;
import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This is the CacheAPI class which effictevely caches the response from getBroadbandPercentage, so
 * as to reduce the amount of API calls especially if a specific call has been made before.
 */
public class BroadbandCache {
  private final LoadingCache<ArrayList<String>, CensusData> cache;
  private final CensusDataSource censusDataSource;

  public BroadbandCache(int maximumSize, int minutesExpire, CensusDataSource censusDataSource) {
    this.censusDataSource = censusDataSource;

    // Look at the docs -- there are lots of builder parameters you can use
    //   including ones that affect garbage-collection (not needed for Server).
    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(maximumSize)
            // How long should entries remain in the cache?
            .expireAfterWrite(minutesExpire, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                new CacheLoader<>() {

                  /**
                   * This is the load method, if a key does not exist inside of the cache, fill it
                   * in with the get broadbandPercentage If getBroadbandPercentage throws an error,
                   * propagate it back up.
                   *
                   * @param key (formatted as (state, county))
                   * @return
                   * @throws DatasourceException
                   * @throws BadJSONException
                   */
                  @Override
                  public CensusData load(ArrayList<String> key)
                      throws DatasourceException, BadJsonException {
                    // the key is formatted as (state, county)
                    return censusDataSource.getBroadbandPct(key.get(0), key.get(1));
                    // the result is formated as , the (broadband percentage, the retrieval time)
                  }
                });
  }

  /**
   * this is the search method for the cache, which effectively calls load if the value doesn't
   * exist which then calls getBroadbandPercentage on the acsDataSource
   *
   * @param stateName
   * @param countyName
   * @return
   * @throws DatasourceException
   * @throws BadJSONException
   */
  public CensusData search(String stateName, String countyName)
      throws DatasourceException, BadJsonException {
    // "get" is designed for concurrent situations; for today, use getUnchecked:
    ArrayList<String> inputArr = new ArrayList<>();
    inputArr.add(stateName);
    inputArr.add(countyName);

    try {
      return this.cache.getUnchecked(inputArr);
    } catch (Exception e) {
      // used instance of to check what kind of exception was thrown, and then more formally
      // throw the datasource or badjson exception, to be handled in BroadbandHandler to put the
      // necessary responses in the response map.
      if (e.getCause() instanceof DatasourceException) {
        throw new DatasourceException(e.getMessage(), e.getCause());
      }
      if (e.getCause() instanceof BadJsonException) {
        throw new BadJsonException(e.getMessage(), e.getCause());
      }
    }
    // this should never be thrown as Datasource and BadJSON are caught above.
    throw new DatasourceException("Error while searching");
  }
}
