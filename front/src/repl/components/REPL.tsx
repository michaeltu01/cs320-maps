import { Dispatch, SetStateAction, useState } from "react";
import "../styles/main.css";
import { REPLHistory } from "./REPLHistory";
import { REPLInput } from "./REPLInput";
import { changeMode } from "./commands/mode";
import { loadCSV } from "./commands/REPLLoad";
import { viewCSV } from "./commands/view";
import { searchCSV } from "./commands/REPLSearch";
import { broadbandRequest } from "./commands/broadband";

export interface REPLFunction {
  (args: string[]): Promise<string[][]>;
}

interface REPLProps {
  setFilterOverlay: Dispatch<
    SetStateAction<GeoJSON.FeatureCollection | undefined>
  >;
}

export default function REPL(props: REPLProps) {
  //Add some kind of shared state that holds all the outputs submitted.
  const [history, setHistory] = useState<[string, string[][]][]>([]);
  //shared state that lets us know whether the parsed CSV data is wellformed or not
  const [verbose, setVerbose] = useState<boolean>(false);
  //shared state that we use to store and access commands that have been registered
  const [commandMap, setCommandMap] = useState<Map<string, REPLFunction>>(
    //initialize with the necessary functions
    new Map<string, REPLFunction>([
      ["load_file", loadCSV],
      ["view", viewCSV],
      ["search", searchCSV],
      ["broadband", broadbandRequest],
    ])
  );

  /**
   * REACT STATE VARIABLES FOR MOCKING
   */
  //shared state that holds the parsed CSV data
  const [mockParsedData, setMockParsedData] = useState<string[][]>([]);
  //shared state that lets us know whether the parsed CSV data is wellformed or not
  const [mockValidParsedData, setMockValidParsedData] =
    useState<boolean>(false);

  // shared state that stores the history of user inputs for up-down arrow functionality
  // default value contains an empty string because the input box first contains an empty string
  // the last element in this history will update as the user makes edits to the input box
  const [inputHistory, setInputHistory] = useState<string[]>([""]);

  return (
    //descriptions of input given in other files
    <div className="repl">
      <REPLHistory history={history} verbose={verbose} />
      <hr></hr>
      <REPLInput
        history={history}
        setHistory={setHistory}
        verbose={verbose}
        setVerbose={setVerbose}
        commandMap={commandMap}
        setCommandMap={setCommandMap}
        inputHistory={inputHistory}
        setInputHistory={setInputHistory}
        setFilterOverlay={props.setFilterOverlay}
        //React state variables for mock commands
        mockParsedData={mockParsedData}
        setMockParsedData={setMockParsedData}
        mockValidParsedData={mockValidParsedData}
        setMockValidParsedData={setMockValidParsedData}
      />
    </div>
  );
}
