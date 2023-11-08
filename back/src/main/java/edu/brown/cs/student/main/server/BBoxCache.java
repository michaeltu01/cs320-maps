package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.BBoxCacheResponse;
import edu.brown.cs.student.main.server.json_classes.BoundaryBox;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class BBoxCache {
    private final LoadingCache<BoundaryBox, BBoxCacheResponse> cache;
    
    public BBoxCache(int maximumSize, int minutesExpire) {
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

                    @Override
                    public BBoxCacheResponse load(BoundaryBox bbox)
                        throws DatasourceException {
                        FeatureCollection geojson = Server.getSharedJson();
                        FeatureCollection filteredJson = geojson.filterByBoundaryBox(bbox);
                        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));

                        return new BBoxCacheResponse(filteredJson, dateTime);
                    }
                });
    }

    public BBoxCacheResponse search(BoundaryBox bbox) throws DatasourceException {
        try {
            return this.cache.getUnchecked(bbox);
        } catch (Exception e) {
            if (e.getCause() instanceof DatasourceException) {
                throw new DatasourceException(e.getMessage(), e.getCause());
            }
            throw new DatasourceException(e.getMessage());
        }
    }
}
