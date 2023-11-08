import { Dispatch, SetStateAction } from "react";
import { setData } from "../../MockData/mockedJson";
import { REPLFunction } from "../REPL";

/**
 *
 * @param parameters - string array: load_file, filepath, headerStatus
 * @returns 2D array representing the output to print out - success or error message
 */
export const loadCSV: REPLFunction = async (
  parameters: string[]
): Promise<string[][]> => {
  //check for valid input format
  if (parameters.length != 3) {
    return [["Invalid parameters inputted"]];
  }
  let filename = parameters[1];
  let headerStatus = parameters[2];
  //load with backend
  let hostname = "http://localhost";
  let port = ":3232";
  let loadCSVQuery =
    "/loadcsv?filename=back/data/" + filename + "&headers=" + headerStatus;
  let result = [["placeholder"]];
  await fetch(hostname + port + loadCSVQuery)
    .then((response) => response.json())
    .then((responseObject) => {
      //will give detailed success or error message
      result = [[responseObject.type + "- " + responseObject.details]];
    });
  return result;
};

//MOCK LOAD FOR FRONTEND TESTS
//takes in react state variable to imitate the same functionality
export const MockLoadCSV = (
  setValidParsedData: Dispatch<SetStateAction<boolean>>,
  setParsedData: Dispatch<SetStateAction<string[][]>>,
  filepath: string,
  headers: string
) => {
  // //load with frontend state
  if (!filepath) {
    setValidParsedData(false);
    //if filepath is empty, don't even try it buddy boy
    return [["CSV NOT successfully loaded -- invalid filepath"]];
  } else {
    //if valid should automatically set the parsed data
    if (setData(setParsedData, filepath)) {
      setValidParsedData(true);
      return [["CSV successfully loaded"]];
    } else {
      //if invalid, then turn wellformedness state to false
      setValidParsedData(false);
      return [["CSV NOT successfully loaded -- invalid filepath"]];
    }
  }
};
