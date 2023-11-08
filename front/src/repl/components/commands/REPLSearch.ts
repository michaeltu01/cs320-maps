import { Dispatch, SetStateAction, useState } from "react";
//IMPORTS FOR MOCKING
import {
  numberCSV,
  mixedCSV,
  headerCSV,
  incomeDataCSV,
  doubleCSV,
} from "../../MockData/mockedJson";
import {
  search1,
  incomeDataOutput,
  headerCSVOutput,
  doubleOutput,
  noValue,
  numberOutput,
  isaacYiOutput,
} from "../../MockData/mockedSearches";
import { REPLFunction } from "../REPL";

/**
 *
 * @param parameters - array of strings containing the parameters for the search
 * @returns response output to display -- 2D array of strings
 */
export const searchCSV: REPLFunction = async (
  parameters: string[]
): Promise<string[][]> => {
  //initialize data
  let hostname = "http://localhost";
  let port = ":3232";
  let searchCSVQuery: string;
  let searchResults = [["placeholder"]];

  //check which method to call based on whether a column identifier is given
  if (parameters.length === 2) {
    //no col identifier
    searchCSVQuery = "/searchcsv?value=" + parameters[1];
    await fetch(hostname + port + searchCSVQuery)
      .then((response) => response.json())
      .then((responseObject) => {
        let type = responseObject.type;
        if (type === "error") {
          searchResults = [[type + ": " + responseObject.details]];
        } else {
          searchResults = responseObject.data;
        }
      });
  } else if (parameters.length === 4) {
    //given col identifier
    let flag = parameters[2];
    if (flag === "--colIndex") {
      searchCSVQuery =
        "/searchcsv?value=" + parameters[1] + "&index=" + parameters[3];
    } else if (flag === "--colName") {
      searchCSVQuery =
        "/searchcsv?value=" + parameters[1] + "&column=" + parameters[3];
    } else {
      return [["Invalid column identifier flag given"]];
    }
    await fetch(hostname + port + searchCSVQuery)
      .then((response) => response.json())
      .then((responseObject) => {
        //error handling if not successful query
        let type = responseObject.type;
        if (type === "error") {
          searchResults = [[type + ": " + responseObject.details]];
        } else {
          searchResults = responseObject.data;
        }
      });
  } else {
    return [["Invalid parameters given"]];
  }

  return searchResults;
};

//FOR MOCKING FRONT-END SEARCH
//return the results as string[][] -- empty array if no rows
//function called if search called with no column identifier
export function MockREPLSearchNoCol(parsedData: string[][], value: string) {
  if (parsedData === mixedCSV && value === "string") {
    return search1;
  }
  if (parsedData === incomeDataCSV && value === "providence") {
    return incomeDataOutput;
  }
  if (parsedData === headerCSV && value === "18") {
    return headerCSVOutput;
  }
  if (parsedData === headerCSV && value === "isaac yi") {
    return isaacYiOutput;
  }
  if (parsedData === doubleCSV && value === "hi") {
    return doubleOutput;
  }
  if (parsedData === mixedCSV && value === "i") {
    return [["No matching rows found"]];
  }

  return [["No matching rows found"]];
}

//If a column identifier is given (mocked)
export function MockREPLSearchWithCol(
  parsedData: string[][],
  flag: string,
  column: string,
  value: string
) {
  if (
    parsedData === headerCSV &&
    value === "isaac yi" &&
    column === "2" &&
    flag === "--colIndex"
  ) {
    return [["No matching rows found"]];
  }
  if (
    parsedData === headerCSV &&
    value === "isaac yi" &&
    column === "0" &&
    flag === "--colIndex"
  ) {
    return isaacYiOutput;
  }
  if (
    parsedData === headerCSV &&
    value === "isaac yi" &&
    column === "name" &&
    flag === "--colName"
  ) {
    return isaacYiOutput;
  }
  if (
    parsedData === headerCSV &&
    value === "isaac yi" &&
    column === "age" &&
    flag === "--colName"
  ) {
    return [["No matching rows found"]];
  }
  if (
    parsedData === doubleCSV &&
    value === "hi" &&
    column == "0" &&
    flag === "--colIndex"
  ) {
    return doubleOutput;
  }
  if (
    parsedData === numberCSV &&
    value === "1" &&
    column == "1" &&
    flag === "--colIndex"
  ) {
    return numberOutput;
  }
  return [["No matching rows found"]];
}
