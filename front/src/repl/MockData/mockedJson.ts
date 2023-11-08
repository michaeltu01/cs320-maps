import { Dispatch, SetStateAction, useState } from "react";

/**
 * Mock data for REPLLoad!
 */

export const emptyCSV: string[][] = [];
//Mock data for load_file mock command
export const numberCSV = [
  ["1", "2", "3"],
  ["3", "1", "2"],
  ["6", "9", "20"],
  ["102983984", "-1", "3"],
];
export const mixedCSV = [
  ["1", "string", "34"],
  ["89", "true", "2345"],
];
export const headerCSV = [
  ["name", "age", "favorite root vegetable"],
  ["isaac yi", "20", "potato"],
  ["jonathan zhou", "18", "garlic"],
];
export const incomeDataCSV = [
  ["average income", "town", "state"],
  ["100000", "san diego", "california"],
  ["2000000000000", "ridgewood", "new jersey"],
  ["-2", "providence", "rhode island"],
];
export const doubleCSV = [
  ["hi", "san diego", "california"],
  ["hi", "bruh", "in"],
];

//setData function mocks parsing the data from the mock file
export function setData(
  setParsedData: Dispatch<SetStateAction<string[][]>>,
  filePath: string
) {
  if (filePath === "emptyCSV.csv") {
    setParsedData(emptyCSV);
    return true;
  } else if (filePath === "numberCSV.csv") {
    setParsedData(numberCSV);
    return true;
  } else if (filePath === "mixedCSV.csv") {
    setParsedData(mixedCSV);
    return true;
  } else if (filePath === "headerCSV.csv") {
    setParsedData(headerCSV);
    return true;
  } else if (filePath === "incomeDataCSV.csv") {
    setParsedData(incomeDataCSV);
    return true;
  } else if (filePath === "doubleCSV.csv") {
    setParsedData(doubleCSV);
    return true;
  } else {
    return false;
  }
}
