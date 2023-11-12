import { Dispatch, SetStateAction } from "react";
import { setData } from "../../MockData/mockedJson";
import { REPLFunction } from "../REPL";

/**
 *
 * @param parameters - string array: load_file, filepath, headerStatus
 * @returns 2D array representing the output to print out - success or error message
 */
export const loadJson: REPLFunction = async (
  parameters: string[]
): Promise<string[][]> => {
    let result = [["placeholder"]];
  //check for valid input format
  if (parameters.length === 1) {
    await fetch("http://localhost:3232//loadjson")
    .then((response) => response.json())
    .then((responseObject) => {
      //will give detailed success or error message
      result = [[responseObject.type + "- " + responseObject.details]];
    });
    return result;
  }
  let filename = parameters[1];
  //load with backend
  let hostname = "http://localhost";
  let port = ":3232";
  let loadJSONQuery =
    "/loadjson?filepath="
  await fetch(hostname + port + loadJSONQuery + filename)
    .then((response) => response.json())
    .then((responseObject) => {
      //will give detailed success or error message
      result = [[responseObject.type + "- " + responseObject.details]];
    });
  return result;
};