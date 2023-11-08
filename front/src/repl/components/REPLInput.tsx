import "../styles/main.css";
import { Dispatch, SetStateAction, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import { changeMode } from "./commands/mode";
import { loadCSV } from "./commands/REPLLoad";
import { viewCSV } from "./commands/view";
import { searchCSV } from "./commands/REPLSearch";
import { broadbandRequest } from "./commands/broadband";
import { REPLFunction } from "./REPL";
import { mock } from "./commands/mock";

//input props
interface REPLInputProps {
  //history stores all the previous commands and outputs
  history: [string, string[][]][];
  setHistory: Dispatch<SetStateAction<[string, string[][]][]>>;
  //verbose keeps track of the mode of the history -- verbose or brief
  verbose: boolean;
  setVerbose: Dispatch<SetStateAction<boolean>>;
  //commandMap keeps track of what commands have been registered
  commandMap: Map<string, REPLFunction>;
  setCommandMap: Dispatch<SetStateAction<Map<string, REPLFunction>>>;
  //inputHistory keeps track of inputs previously entered by the user
  inputHistory: string[];
  setInputHistory: Dispatch<SetStateAction<string[]>>;
  /**
   * MOCK STATE VARIABLES
   */
  mockParsedData: string[][];
  setMockParsedData: Dispatch<SetStateAction<string[][]>>;
  mockValidParsedData: boolean;
  setMockValidParsedData: Dispatch<SetStateAction<boolean>>;
}

export function REPLInput(props: REPLInputProps) {
  //store the entire inputted command
  const [commandString, setCommandString] = useState<string>("");

  //keep track of number of commands submitted
  const [count, setCount] = useState<number>(0);

  //keeps track of the 0-based index in the input history for up-down arrow functionality
  const [inputHistoryIndex, setInputHistoryIndex] = useState<number>(0);

  //on button click
  async function handleSubmit(commandString: string) {
    let strings = commandString.split(" ");
    //process the command
    let command = strings[0];

    let output: string[][] = [["Function failed to give wellformed response"]];
    //get the function from the map
    //call the function and set the output value to the return value of that function
    //mode is exception
    setInputHistoryIndex(inputHistoryIndex + 1);
    if (command === "clear") {
      output = [["History cleared."]];
      props.setHistory([[commandString, output]]);
      setCount(count + 1);
      setCommandString("");

      // finalize the last element of the input history & add a new empty string to track the user's edits for the next input
      props.setInputHistory([
        ...props.inputHistory.slice(0, -1),
        commandString,
        "",
      ]);
      return;
    }
    if (command === "mode") {
      //react state variables used for keeping track of verbosity
      output = changeMode(props.verbose, props.setVerbose);
    } else {
      //retrieve parameters within curly braces
      let parameters = commandString.split("{");
      //parse through and retrieve the parameters
      for (let i = 0; i < parameters.length; i++) {
        parameters[i] = parameters[i].replace("}", "");
      }
      if (command === "mock") {
        //mock command for tests
        output = mock(
          parameters,
          props.mockParsedData,
          props.setMockParsedData,
          props.mockValidParsedData,
          props.setMockValidParsedData
        );
      } else {
        let func = props.commandMap.get(command);
        if (func != undefined) {
          //through narrowing, now func is guaranteed to be a valid REPLFunction
          output = await func(parameters);
        } else {
          //inputted command does not exist
          output = [["Invalid command entered"]];
        }
      }
    }
    props.setHistory([...props.history, [commandString, output]]);
    //increment count and reset command string
    setCount(count + 1);
    setCommandString("");

    // finalize the last element of the input history & add a new empty string to track the user's edits for the next input
    props.setInputHistory([
      ...props.inputHistory.slice(0, -1),
      commandString,
      "",
    ]);

    // reset focus to the input box
    document.getElementById("input1")?.focus();
  }

  /**
   * Function contains the action of the up arrow functionality in the input box.
   * Decrements the index and updates the input box with an input in input history
   * @param index current index of the user input in the input history (currently editing)
   */
  function upArrowAction(index: number) {
    if (index <= 0) {
      // do not continue decrementing index if at the beginning of the array
      setCommandString(props.inputHistory[0]);
      setInputHistoryIndex(0);
    } else {
      // decrement if not at the beginning of the array
      setCommandString(props.inputHistory[index - 1]);
      setInputHistoryIndex(index - 1);
    }
  }

  /**
   * Function contains the action of the down arrow functionality in the input box.
   * Increments the index and updates the input box with an input in input history
   * @param index current index of the user input in the input history (currently editing)
   */
  function downArrowAction(index: number) {
    if (index >= props.inputHistory.length - 1) {
      // do not increment if at the end of the array
      setCommandString(props.inputHistory[props.inputHistory.length - 1]);
      setInputHistoryIndex(props.inputHistory.length - 1);
    } else {
      // increment if not at the end of the array
      setCommandString(props.inputHistory[index + 1]);
      setInputHistoryIndex(index + 1);
    }
  }

  return (
    //input box that user types into and submit button that calls handleSubmit function
    <div className="repl-input" aria-labelledby="input1">
      <fieldset>
        <legend>Enter a command:</legend>
        <ControlledInput
          value={commandString}
          setValue={setCommandString}
          inputHistory={props.inputHistory}
          setInputHistory={props.setInputHistory}
          editingIndex={props.inputHistory.length - 1}
          setInputIndex={setInputHistoryIndex}
          ariaLabel={"Command input"}
          // Enables ENTER to submit command in this component
          onKeyDown={(event: React.KeyboardEvent<HTMLInputElement>) => {
            if (event.key === "Enter") {
              // Prevent the default behavior of the Enter key
              event.preventDefault();

              // Perform the action when the Enter key is pressed
              handleSubmit(commandString);
            } else if (event.key === "ArrowUp") {
              event.preventDefault();
              upArrowAction(inputHistoryIndex);
            } else if (event.key === "ArrowDown") {
              event.preventDefault();
              downArrowAction(inputHistoryIndex);
            }
          }}
        />
      </fieldset>
      <div
        id="inputDescription"
        style={{ display: "none" }}
        aria-live="assertive"
        aria-hidden={false}
      >
        To submit your command, press the ENTER key or tab to the submit button
        and click.
      </div>
      <button onClick={() => handleSubmit(commandString)} tabIndex={1}>
        Submitted {count} times
      </button>
    </div>
  );
}
