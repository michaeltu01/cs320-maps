import Map, { Layer, MapLayerMouseEvent, Point, PointLike, Source, ViewStateChangeEvent} from "react-map-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import React from "react";
import { ACCESS_TOKEN } from "./private/api";
import { useState } from "react";
import { FillLayer, MapRef } from "react-map-gl";
import { FeatureCollection } from "geojson";
import { useEffect } from "react";
import { overlayData } from "./overlays";
import { geoLayer } from "./overlays";
import { useRef } from "react";

function WrappedMap() {

  const mapRef = useRef<MapRef>(null)

    const [viewState, setViewState] = useState({
        longitude: -71.418884,
        latitude: 41.825226,
        zoom: 10,
    });

    const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);
    // const [logs, setLogs] = useState<string[]>([]); // To store the console logs
    const [lastLog, setLastLog] = useState<string>(""); // To store and display the last console log


    useEffect(() => {
      setOverlay(overlayData());
    }, []);


        function onMapClick(e: MapLayerMouseEvent) {
        console.log(e.lngLat.lat);
        console.log(e.lngLat.lng);

        // Access the Mapbox component using mapRef
        const map = mapRef.current;

        // Create a bounding box around the click point
        const bbox: [PointLike, PointLike] = [
          [e.point.x, e.point.y], 
          [e.point.x, e.point.y] 
        ]

        // Use the mapRef to call queryRenderedFeatures
        if (map === null) {
          console.error(new Error("map is empty"))
        }
        else {
          const features = map.queryRenderedFeatures(bbox);
          const click_feature = features[0];
          const properties = click_feature.properties;
          const logEntry = `state: ${properties.state}, city: ${properties.city}, holc_grade: ${properties.holc_grade}`;

          console.log(logEntry);
          setLastLog(logEntry);

          // console.log("state " + properties.state)
          // console.log("city " + properties.city)
          // console.log("holc_grade " + properties.holc_grade)

          // setLogs((prevLogs) => [...prevLogs, `state: ${properties.state}`, `city: ${properties.city}`, `holc_grade: ${properties.holc_grade}`]);

        }
        
      };

  return (
    <div>

    <Map
    
        mapboxAccessToken = {ACCESS_TOKEN}
        {...viewState}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        style={{ width: window.innerWidth, height: window.innerHeight }}
        mapStyle={"mapbox://styles/mapbox/streets-v12"}
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
        ref={mapRef}
        >
        <Source id="geo_data" type="geojson" data={overlay}>
        <Layer {...geoLayer} />
      </Source>

    </Map>

    <div className="logs">
      <h2>Click</h2>
      <pre>{lastLog}</pre>
    </div>
    </div>
    )
}

export default WrappedMap