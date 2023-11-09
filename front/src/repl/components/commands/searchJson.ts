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


  if (parameters.length != 2) {
    return [["Invalid parameters"]];
  }

  const handleAreaHighlight = async () => {
    const fetched = await fetch("http://localhost:3232/searchjson?search=" + parameters[1]);
    const data = await fetched.json();
    const features = data.result;
    const geojson: GeoJSON.FeatureCollection = {
      type: "FeatureCollection",
      features: features,
    };
    if (!data.result || data.result.length === 0) {
      return [[`"${parameters[1]} not in data`]]
    }
    setFilterOverlay(geojson); // this is mocked geojson
  };

  handleAreaHighlight();

  return [[`Successful search for: "${parameters[1]}"`]];
}