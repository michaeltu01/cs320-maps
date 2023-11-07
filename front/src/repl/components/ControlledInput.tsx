import "../styles/main.css";
import { Dispatch, SetStateAction, useEffect } from "react";

// Remember that parameter names don't necessarily need to overlap;
// I could use different variable names in the actual function.
interface ControlledInputProps {
  value: string;
  // This type comes from React+TypeScript. VSCode can suggest these.
  //   Concretely, this means "a function that sets a state containing a string"
  setValue: Dispatch<SetStateAction<string>>;

  // represents the index currently editing in input history array. this ensures that the input index always resets to the
  // end of the array (the command that the user is currently editing).
  editingIndex: number;

  // setter for the input index that the up-down arrow functionality will increment/decrement
  setInputIndex: Dispatch<SetStateAction<number>>;

  // string[] representing history of user inputs
  inputHistory: string[];

  // setter for inputHistory
  setInputHistory: Dispatch<SetStateAction<string[]>>;
  ariaLabel: string;
  onKeyDown: React.KeyboardEventHandler<HTMLInputElement>;
}

// Input boxes contain state. We want to make sure React is managing that state,
//   so we have a special component that wraps the input box.
export function ControlledInput({
  value,
  setValue,
  inputHistory,
  setInputHistory,
  editingIndex,
  setInputIndex,
  ariaLabel,
  onKeyDown,
}: ControlledInputProps) {
  return (
    <input
      id="input1"
      type="text"
      className="repl-command-box"
      value={value}
      placeholder="Enter command here!"
      onChange={(ev) => {
        setValue(ev.target.value);
        setInputHistory([...inputHistory.slice(0, -1), ev.target.value]);
        setInputIndex(editingIndex);
      }}
      aria-label={ariaLabel}
      onKeyDown={onKeyDown}
      autoFocus
      tabIndex={0}
      aria-describedby="inputDescription"
      aria-live="assertive"
    ></input>
  );
}
