package edu.brown.cs.student.main.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.brown.cs.student.main.server.census.CensusData;
import edu.brown.cs.student.main.server.census.CensusDataSource;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.util.concurrent.TimeUnit;

/**
 * Cache class which is designed to store API requests to the external API so that repeated
 * requests do not have to be made unnecessarily.
 */
public class Cache {
  private final LoadingCache<String, CensusData> cache;
  private final CensusDataSource dataSource;

  /**
   * Cache constructor which launches the cache.
   * @param dataSource
   */
  public Cache(CensusDataSource dataSource) {
    this.dataSource = dataSource;
    this.cache = CacheBuilder.newBuilder()
        // How many entries maximum in the cache?
        .maximumSize(10)
        // How long should entries remain in the cache?
        .expireAfterWrite(1, TimeUnit.MINUTES)
        // Keep statistical info around for profiling purposes
        .recordStats()
        .build(
            // Strategy pattern: how should the cache behave when
            // it's asked for something it doesn't have?
            new CacheLoader<>() {
              @Override
              public CensusData load(String key) throws DatasourceException, BadRequestException {
                // key should be formatted in the form: "[county]. [state]" (ex. Los Angeles County, California)
                String[] splitKey = key.split(", ");
                String county = splitKey[0];
                String state = splitKey[1];
                System.out.println("called load for: "+key);
                // If this isn't yet present in the cache, load it:
                return null;
              }
            });
  }
}
