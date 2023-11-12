import { Dispatch, SetStateAction } from "react";
import { mockFilterOverlay } from "../../../geodata/mockVariables";
import { REPLFunction } from "../REPL";

/**
 * functionality that calls on the backend to get the filtered search data. then highlights areas where descriptions match
 * @param parameters 
 * @param setFilterOverlay 
 * @returns 
 */
export async function searchJson(
  parameters: string[],
  setFilterOverlay: Dispatch<
    SetStateAction<GeoJSON.FeatureCollection | undefined>
  >
)
: Promise<string[][]> {

  if (parameters.length == 1) {
    return [["Missing Search Keyword"]]
  }
  if (parameters.length != 2) {
    return [["Invalid parameters"]];
  }

  const handleAreaHighlight = async () => {
    const fetched = await fetch("http://localhost:3232/searchjson?search=" + parameters[1]);
    const data = await fetched.json();
    const features = data.result;

    if (!data.result || data.result.length === 0 || data.result === null) {
      return [[`"${parameters[1]}" not in data`]]
    }
    const geojson: GeoJSON.FeatureCollection = {
      type: "FeatureCollection",
      features: features,
    };
    setFilterOverlay(geojson); // this is mocked geojson
    return [[`Successful search for: "${parameters[1]}"`]];
  };

  const result = await handleAreaHighlight();

  return result;
}