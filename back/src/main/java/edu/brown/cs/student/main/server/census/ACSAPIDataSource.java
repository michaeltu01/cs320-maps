package edu.brown.cs.student.main.server.census;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/**
 * Class which reaches out to the ACSAPI in order to retrieve state codes, county codes, and
 * ultimately, broadband percentages.
 */
public class ACSAPIDataSource implements CensusDataSource {

  private final Map<String, String> stateToStateCode;

  /**
   * Constructor which stores the state codes and makes sure that it's size is accurate.
   *
   * @throws DatasourceException if issue occurs with requesting data from data source
   */
  public ACSAPIDataSource() throws DatasourceException {
    this.stateToStateCode = storeStateCodes();
    assert (stateToStateCode.keySet().size() == 52);
  }

  /**
   * Stores all state codes into a HashMap corresponding to the state name
   *
   * @return a HashMap mapping state names to state codes
   * @throws DatasourceException if issue occurs with requesting data from data source
   */
  private Map<String, String> storeStateCodes() throws DatasourceException {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type stateCodeRow = Types.newParameterizedType(List.class, String.class);
      Type stateCodeRowList = Types.newParameterizedType(List.class, stateCodeRow);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(stateCodeRowList);
      // NOTE: important! pattern for handling the input stream
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) throw new DatasourceException("Malformed response from ACS");
      Map<String, String> stateCodeMap = new HashMap<>();
      for (int i = 1; i < body.size(); i++) {
        List<String> row = body.get(i);
        stateCodeMap.put(
            row.get(0).toUpperCase(), row.get(1)); // index 0 = state name; index 1 = state code
      }
      return stateCodeMap;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed.
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }

  /**
   * Return the county code by searching for the county name in the state of the given state code
   *
   * @param stateCode state code corresponding to the state of the county
   * @param countyName name of the county to search formatted in the format of the API county name
   *     entries
   * @return the county code as a String
   * @throws DatasourceException if issue occurs with requesting data from data source
   */
  private String getCountyCode(String stateCode, String countyName)
      throws DatasourceException, BadJsonException {
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type countyCodeRow = Types.newParameterizedType(List.class, String.class);
      Type countyCodeRowList = Types.newParameterizedType(List.class, countyCodeRow);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(countyCodeRowList);
      // NOTE: important! pattern for handling the input stream
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) throw new DatasourceException("Malformed response from ACS");
      for (int i = 1; i < body.size(); i++) {
        List<String> row = body.get(i);
        if (row.get(0).toLowerCase().equals(countyName.toLowerCase())) { // index 0 = county name
          // System.out.println("county code: " + row.get(2));
          return row.get(2); // index 2 = county code
        }
      }
      throw new BadJsonException("The county that you have given cannot be found: " + countyName);
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * This method retrieves the percentage of households with broadband access from the ACS API for a
   * target location given by the target state and county.
   *
   * @param stateName the target state
   * @param countyName the target county
   * @return CensusData object of the target state and county
   */
  @Override
  public CensusData getBroadbandPct(String stateName, String countyName)
      throws DatasourceException, BadJsonException {
    String stateCode = stateToStateCode.get(stateName.toUpperCase());
    if (stateCode == null) {
      throw new BadJsonException("The state you have given cannot be found: " + stateName);
    }
    String countyNameFormatted = countyName + ", " + stateName;
    String countyCode = getCountyCode(stateCode, countyNameFormatted);

    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type broadbandRow = Types.newParameterizedType(List.class, String.class);
      Type broadbandRowList = Types.newParameterizedType(List.class, broadbandRow);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(broadbandRowList);
      // NOTE: important! pattern for handling the input stream
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) throw new DatasourceException("Malformed response from ACS");
      // body should be 2 rows (header + result)
      List<String> result = body.get(1);
      String dateTime =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
      return new CensusData(Double.parseDouble((result.get(1))), dateTime);
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }
}
