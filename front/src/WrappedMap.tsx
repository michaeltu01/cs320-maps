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

function WrappedMap() {

    function onMapClick(e: MapLayerMouseEvent) {
        console.log(e.lngLat.lat);
        console.log(e.lngLat.lng);
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