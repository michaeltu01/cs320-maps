import { REPLFunction } from "../REPL";

/**
 *
 * @param _parameters - string[] that should be empty
 * @returns - 2D array of strings representing output. The CSV in success, and error message in failure.
 */
//view the parsed CSV data
export const viewCSV: REPLFunction = async (
  parameters: string[]
): Promise<string[][]> => {
  if (parameters.length != 0) {
    return [["Invalid parameters inputted"]];
  }
  //view with backend
  let hostname = "http://localhost";
  let port = ":3232";
  let viewCSVQuery = "/viewcsv";
  let type = "";
  let details = "";
  let output = [["placeholder"]];
  await fetch(hostname + port + viewCSVQuery)
    .then((response) => response.json())
    .then((responseObject) => {
      type = responseObject.type;
      details = responseObject.details;
      if (type === "error") {
        output = [[type + ": " + details]];
      } else {
        output = responseObject.data;
      }
    });
  return output;
};

//MOCK VIEW FOR FRONTEND TESTING
export const mockView = (parsedData: string[][], validParsedData: boolean) => {
  // //view with frontend
  //if unsuccessful load or no load called, return clear message
  if (validParsedData === false) {
    return [["No CSV loaded -- please call load_file first!"]];
  } else {
    //successful -> show it!
    return parsedData;
  }
};
