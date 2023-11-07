import Map, { Layer, MapLayerMouseEvent, Source, ViewStateChangeEvent} from "react-map-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import React from "react";
import { ACCESS_TOKEN } from "./private/api";
import { useState } from "react";
import { FillLayer } from "react-map-gl";
import { FeatureCollection } from "geojson";
import { useEffect } from "react";
import { overlayData } from "./overlays";
import { geoLayer } from "./overlays";
import { useRef } from "react";

function WrappedMap() {

  const mapRef = useRef<MapRef | null>(null); // Define the type as MapRef or null

    function onMapClick(e: MapLayerMouseEvent) {
        console.log(e.lngLat.lat);
        console.log(e.lngLat.lng);

        // Access the Mapbox component using mapRef
        const map = mapRef.current;

        // Create a bounding box around the click point
        const bbox = [
          [e.point.x - 5, e.point.y - 5], 
          [e.point.x + 5, e.point.y + 5] 
        ];

        // Use the mapRef to call queryRenderedFeatures
        const features = map.queryRenderedFeatures(bbox);

        // Now you can work with the features, for example, log them to the console
        console.log('Clicked features:', features);
      };

    }

    const [viewState, setViewState] = useState({
        longitude: -71.418884,
        latitude: 41.825226,
        zoom: 10,
    });


    const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);


    useEffect(() => {
      setOverlay(overlayData());
    }, []);
    
  return (
    <Map
    
        mapboxAccessToken = {ACCESS_TOKEN}
        {...viewState}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        style={{ width: window.innerWidth, height: window.innerHeight }}
        mapStyle={"mapbox://styles/mapbox/streets-v12"}
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
        >
        <Source id="geo_data" type="geojson" data={overlay}>
        <Layer {...geoLayer} />
      </Source>

    </Map>
    )
}

export default WrappedMap