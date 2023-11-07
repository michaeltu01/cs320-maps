import { Dispatch, SetStateAction } from "react";
import { REPLFunction } from "../REPL";
import {
  successOutput,
  invalidCountyOutput,
  invalidStateOutput,
} from "../../MockData/mockedBroadband";

/**
 *
 * @param parameters - string array of parameters necessary for broadband - state and county
 * @returns - 2D string array to be formatted in a table output display
 */
export const broadbandRequest: REPLFunction = async (
  parameters: string[]
): Promise<string[][]> => {
  //parameters should = broadband, state, county
  if (parameters.length != 3) {
    return [["Invalid Format of Parameters"]];
  }
  let stateName = parameters[1];
  let countyName = parameters[2];
  //replace spaces for proper queries
  let state = stateName.replace(" ", "%20");
  let county = countyName.replace(" ", "%20");

  let hostname = "http://localhost";
  let port = ":3232";
  let broadbandQuery = "/broadband?state=" + state + "&county=" + county;

  let output = [["placeholder"]];
  let errorMessage = "";
  let broadbandPercent: number;
  await fetch(hostname + port + broadbandQuery)
    .then((response) => response.json())
    .then((responseObject) => {
      //if query fails, return the error message returned by backend
      if (responseObject.type === "error") {
        errorMessage = responseObject.type + ": " + responseObject.details;
      }
      broadbandPercent = responseObject.broadband;
    })
    .then((_) => {
      if (broadbandPercent === undefined) {
        output = [[errorMessage]];
      } else {
        //return the broadband percentage received as well as the inputted state and county
        output = [
          [
            "Broadband percentage for " +
              countyName +
              ", " +
              stateName +
              " is: " +
              broadbandPercent,
          ],
        ];
      }
    });
  return output;
};

//MOCK BROADBAND FUNCTION
export const MockBroadband = (state: string, county: string) => {
  if (state === "Virginia" && county === "Virginia Beach City") {
    return successOutput;
  } else if (state === "California" && county == "BadCounty") {
    return invalidCountyOutput;
  } else if (state === "BadState" && county == "San Diego") {
    return invalidStateOutput;
  }
  return [["Invalid Mock Query"]];
};
