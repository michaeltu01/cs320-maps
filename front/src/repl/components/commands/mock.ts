import { Dispatch, SetStateAction } from "react";
import { MockLoadCSV } from "./REPLLoad";
import { MockREPLSearchNoCol, MockREPLSearchWithCol } from "./REPLSearch";
import { mockView } from "./view";
import { MockBroadband } from "./broadband";
import { mockFilterOverlay } from "../../../geodata/mockVariables";

/**
 *
 * @param parameters - parsed parameters from the inputted commandString
 * @param parsedData - react state variable storing the parsed data after a successful load
 * @param setParsedData - react state dispatch that allows setting the parsed data
 * @param validParsedData - react state variable storing a boolean of whether the parsedData is valid
 * @param setValidParsedData - react state dispatch that allows setting whether the parsedData is valid
 * @returns
 */
export function mock(
  //mock load, view, or search
  parameters: string[],
  parsedData: string[][],
  setParsedData: Dispatch<SetStateAction<string[][]>>,
  validParsedData: boolean,
  setValidParsedData: Dispatch<SetStateAction<boolean>>,
  setFilterOverlay: Dispatch<
    SetStateAction<GeoJSON.FeatureCollection | undefined>
  >
) {
  //command is the second parameter because first one is mock
  let command = parameters[1];
  if (command === "load_file") {
    return MockLoadCSV(
      setValidParsedData,
      setParsedData,
      parameters[2], //filepath
      parameters[3] //header status
    );
  } else if (command === "view") {
    return mockView(parsedData, validParsedData);
  } else if (command === "search") {
    if (validParsedData === false) {
      return [["No CSV loaded -- please call load_file first!"]];
    }
    if (parameters.length === 3) {
      //No column identifier inputted
      return MockREPLSearchNoCol(parsedData, parameters[2]);
    } else if (parameters.length === 5) {
      //column identifier inputted
      return MockREPLSearchWithCol(
        parsedData,
        parameters[2], //column identifier flag
        parameters[3], //column identifier
        parameters[4] //search target
      );
    }
    return [["Invalid Format of Parameters"]];
  } else if (command === "broadband") {
    //requires 4 parameters in commandstring -- mock, broadband, state, county
    if (parameters.length != 4) {
      return [["Invalid Format of Parameters"]];
    }
    //parameters = state, county
    return MockBroadband(parameters[2], parameters[3]);
  } else if (command === "search_json") {
    setFilterOverlay(mockFilterOverlay);
    return [["Searched for: Mountain Brook Estates and County Club Gardens"]];
  } else {
    return [["Invalid mock command given"]];
  }
}
