import Map, {
  Layer,
  MapLayerMouseEvent,
  Point,
  PointLike,
  Source,
  ViewStateChangeEvent,
} from "react-map-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import React from "react";
import { ACCESS_TOKEN } from "./private/api";
import { useState } from "react";
import { FillLayer, MapRef } from "react-map-gl";
import { FeatureCollection } from "geojson";
import { useEffect } from "react";
import { overlayData } from "./overlays";
import { geoLayer, filterLayer } from "./overlays";
import { useRef } from "react";
import { mockFilterOverlay } from "./geodata/mockVariables";
import { GeoJSON } from 'geojson';
import { Dispatch, SetStateAction } from "react";


interface WrappedMapProps {
  filterOverlay: GeoJSON.FeatureCollection | undefined;
  setFilterOverlay: Dispatch<
    SetStateAction<GeoJSON.FeatureCollection | undefined>
  >;}


  /**
   * creates our wrapped map componenet
   * @param props 
   * @returns 
   */
function WrappedMap(props: WrappedMapProps) {
  const mapRef = useRef<MapRef>(null);

  const [viewState, setViewState] = useState({
    longitude: -71.418884,
    latitude: 41.825226,
    zoom: 10,
  });

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  const [lastLog, setLastLog] = useState<string>(""); // To store and display the last console log

  useEffect(() => {
    async function fetchGeoJson() {
      let geojson = undefined;
      const fetchCall = await overlayData().then(
        (featureCollection) => (geojson = featureCollection)
      );
      setOverlay(geojson);
    }
    fetchGeoJson();
  }, []);

  function onMapClick(e: MapLayerMouseEvent) {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);

    // Access the Mapbox component using mapRef
    const map = mapRef.current;

    // Create a bounding box around the click point
    const bbox: [PointLike, PointLike] = [
      [e.point.x, e.point.y],
      [e.point.x, e.point.y],
    ];

    // Use the mapRef to call queryRenderedFeatures
    if (map === null) {
      console.error(new Error("map is empty"));
    } else {
      const features = map.queryRenderedFeatures(bbox);
      const click_feature = features[0];
      const properties = click_feature.properties;

      if (properties === null) {
        console.log("properties are null");
      } else if (
        properties.city === undefined &&
        properties.state === undefined &&
        properties.name === undefined &&
        properties.holc_grade === undefined
      ) {
        setLastLog("No data defined in click region. Try again");
      } else {
        const logEntry = `state: ${properties.state}, city: ${properties.city}, holc_grade: ${properties.holc_grade} \n name: ${properties.name}`;
        console.log(logEntry);
        setLastLog(logEntry);
      }
    }
  }

  return (
    <div
    aria-label={`Map ${lastLog}`}
    >
      <Map
        id="mapbox"
        mapboxAccessToken={ACCESS_TOKEN}
        {...viewState}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        style={{ width: "100%", height: "100%" }}
        mapStyle={"mapbox://styles/mapbox/streets-v12"}
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
        ref={mapRef}
      >
        <Source id="geo_data" type="geojson" data={overlay}>
          <Layer {...geoLayer} />
        </Source>
        <Source id="filtered_data" type="geojson" data={props.filterOverlay}>
          <Layer {...filterLayer} />
        </Source>
      </Map>
      <div className="logs">
        <h2>Click</h2>
        <pre>{lastLog}</pre>
      </div>
    </div>
  );
}
export default WrappedMap;
