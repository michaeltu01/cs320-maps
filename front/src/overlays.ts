import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";

// // Import the raw JSON file
// import rl_data from "./geodata/fullDownload.json";
// // you may need to rename the downloaded .geojson to .json

function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

export async function overlayData(): Promise<
  GeoJSON.FeatureCollection | undefined
> {
  const minlong = -180;
  const maxlong = 180;
  const minlat = -90;
  const maxlat = 90;

  let rl_data = null;

  await fetch("http://localhost:3232/loadjson");
  const fetchGeoJson = await fetch(
    "http://localhost:3232/filterjson?minlong=-180&maxlong=180&minlat=-90&maxlat=90"
  )
    .then((response) => response.json())
    .then((responseJson) => {
      rl_data = responseJson.geojson;
    });

  return isFeatureCollection(rl_data) ? rl_data : undefined;
}

const propertyName = "holc_grade";
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.2,
  },
};

export const filterLayer: FillLayer = {
  id: "filtered_data",
  type: "fill",
  paint: {
    "fill-color": "#db34eb",
    "fill-opacity": 0.5,
  },
};
