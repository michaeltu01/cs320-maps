import "../styles/main.css";
import React from "react";

interface REPLHistoryProps {
  //history stores previous commands and outputs
  history: [string, string[][]][];
  //verbose tells us whether to show the command or not
  verbose: boolean;
}

/**
 * goes through the history as a table format
 * @param props 
 * @returns 
 */
export function REPLHistory(props: REPLHistoryProps) {
  return (
    <div
      className="repl-history"
      aria-label="output history"
      aria-live="assertive"
      tabIndex={2}
    >
      {props.history.map((tuple, index) => (
        <div className={"output" + index}>
          {props.verbose && (
            <p className="command">Inputted Command: {tuple[0]}</p>
          )}
          <table>
            <tbody>
              {tuple[1].map((row, rowIndex) => (
                <tr>
                  {row.map((cellData, cellIndex) => (
                    <td className={"cell" + cellIndex}>{cellData}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
}
