# Server-louietanmay-michaeltu01
*Team members*: @mstu, @lnadeau1

**Github repo**: https://github.com/cs0320-f23/server-louietanmay-michaeltu01

## Design Choices
### CSV Code
- Changes made to CSV code: overloaded the `search()` method to have a search function that takes in
a 2D list of data, since that is what is shared across `searchcsv` and `viewcsv` handlers

### User Story 1
- Pass an instance of a `LoadCSVHandler` into the constructor for `SearchCSVHandler` and `ViewCSVHandler`
in the main method so that `/searchcsv` and `/viewcsv` endpoints can access the loaded CSV data
- Protect the loaded CSV data and header row in a defensive and *immutable* copy when `/searchcsv` and `/viewcsv`
endpoints access those two pieces of data. The reason is to avoid the possibility that `/searchcsv` and `/viewcsv`
can mutate those data values.
- For `/searchcsv`, we decided to take 3 parameters: `value`, `column`, and `index`. For `/loadcsv`, we decided to take
2 parameters: `filename` and `headers`. We decided to place the `headers` parameter in `/loadcsv` because we felt it
was needed to be able to parse the CSV file.
- We protected against any nefarious behavior from the end user when passing in a filename by pre-defining a filepath to
a single directory. The end user can only access CSV files located in that directory, and not attempting to 
edit files located elsewhere on the server, such as configuration files.
- For `/viewcsv`, we removed double quotes from the data output, upon considering readability of the "RI 5-year..." file.

### User Story 2
- Store state codes upon the instantiation of the ACSAPIDataSource object inside a HashMap. We considered
efficiency when making this choice. We figured that the one-time payment would be much more efficient.
- However, the county codes will be looked up each time. Since we hadn't finished an implementation of a cache,
we didn't get a chance to cache the lookup for broadband percentage, but that's what we would have liked to have put
in our cache.
- `BroadbandHandler` takes in a `CensusDataSource` object as a parameter. `CensusDataSource` is an interface for
retrieving data sources for User Story 2. `ACSAPIDataSource`, the actual Census API, and `MockDataSource`, the mock
API used for testing, both implement the interface.

## Errors/Bugs
We found no bugs in our code.

The program should never error and crash the server. Errors should output JSON responses of type `error`.
There are three types of errors:
1. `error_bad_json`: returns in the `/broadband` endpoint when the state/county cannot be found, so the
results JSON cannot be properly retrieved from the ACS API
2. `error_bad_request`: user makes an invalid request that cannot be processed
3. `error_datasource`: error that occurs when loading or connecting to the data source

We caught these three main errors in each of our handlers via try-catch structures.

## Tests
View the Javadocs for what each test tests for. Each testing file corresponds to the file it's named after.

## Documentation

### How to run the program?
Run the `main` method in `Server` to start the server. Then, open your browser and make queries to the API
using the parameters and endpoints listed below.

Run the testing classes in IntelliJ by clicking the green "play" button by the class declaration.

### /loadcsv
**Parameters**
- `filename`: relative path to CSV file (path head: "`/Users/louienadeau/Desktop/cs32/server-louietanmay-michaeltu01/data/`")
- `headers`: either "true" or "false"; whether the file has headers or not

**Examples**: 
`localhost:3232/loadcsv?filename=simple.csv&headers=false`

### /searchcsv
*Requires a CSV to be loaded*

**Parameters**
- `value`: the value you are searching for in the loaded CSV file
- `column`: the name of the specific column in which you are looking for the value (OPTIONAL)
- `index`: the index of the specific column in which you are looking for the value (OPTIONAL)

**Examples**: \
`localhost:3232/searchcsv?value=Virginia` \
`localhost:3232/searchcsv?value=Phil&column=name` \
`localhost:3232/searchcsv?value=Hercules&index=2`

### /viewcsv
**Parameters**
- N/A

**Examples**: \
`localhost:3232/viewcsv`

### /broadband
**Parameters**
- `state`: state name
- `county`: county name

**Examples**: \
`localhost:3232/broadband?state=California&county=Los%20Angeles%20County`