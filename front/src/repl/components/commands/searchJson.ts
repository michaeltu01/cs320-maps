import { Dispatch, SetStateAction } from "react";
import { filterOverlay } from "../../../geodata/mockVariables";
import { REPLFunction } from "../REPL";

export async function searchJson(
  parameters: string[],
  setFilterOverlay: Dispatch<
    SetStateAction<GeoJSON.FeatureCollection | undefined>
  >
): Promise<string[][]> {
  if (parameters.length != 2) {
    return [["Invalid parameters"]];
  }
  setFilterOverlay(filterOverlay);
  return [[`Successful search for: "${parameters[1]}"`]];
}
