import React, { useEffect, useState } from "react";
import "./App.css";
import WrappedMap from "./WrappedMap";

// REMEMBER TO PUT YOUR API KEY IN A FOLDER THAT IS GITIGNORED!!
// (for instance, /src/private/api_key.tsx)
// import {API_KEY} from "./private/api_key"

function App() {
  return <div className="App">
    {/* TODO: PLACE MAP CONTENT HERE */}
    <WrappedMap/>
  </div>
}

export default App;
