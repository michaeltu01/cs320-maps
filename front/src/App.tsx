import React, { useEffect, useState } from "react";
import "./App.css";
import WrappedMap from "./WrappedMap";
import REPL from "./repl/components/REPL";

// REMEMBER TO PUT YOUR API KEY IN A FOLDER THAT IS GITIGNORED!!
// (for instance, /src/private/api_key.tsx)
// import {API_KEY} from "./private/api_key"

function App() {
  const [filterOverlay, setFilterOverlay] = useState<
    GeoJSON.FeatureCollection | undefined
  >(undefined);

  return (
    <div className="App">
      <div className="container">
        <div className="component">
          <h1 className="header">Maps</h1>
          <WrappedMap filterOverlay={filterOverlay} />
        </div>
        <div className="component">
          <h1 className="header">REPL</h1>
          <REPL setFilterOverlay={setFilterOverlay} />
        </div>
      </div>
    </div>
  );
}

export default App;
