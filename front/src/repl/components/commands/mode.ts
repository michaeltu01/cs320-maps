import { Dispatch, SetStateAction } from "react";

/**
 *
 * @param verbose - boolean representing whether the current mode is verbose or not (brief)
 * @param setVerbose - setter for the verbosity
 * @returns 2D string array representing the output to be printed in tabular form.
 */
export function changeMode(
  //verbose keeps track of the mode of the history -- verbose or brief
  verbose: boolean,
  setVerbose: Dispatch<SetStateAction<boolean>>
) {
  if (verbose === true) {
    setVerbose(false);
    return [["Successfully switched mode"]];
  } else {
    setVerbose(true);
    return [["Successfully switched mode"]];
  }
}
