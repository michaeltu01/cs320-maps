import { test, expect } from "@playwright/test";

test.afterEach(async () => {
  let output = { type: "", details: "" };
  await fetch("http://localhost:3232/clearcsv")
    .then((response) => response.json())
    .then((responseJson) => (output = responseJson));
  expect(output.type).toBe("success");
  expect(output.details).toBe("Loaded files cleared.");
});


test("front-end and back-end integration - do everything", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:5173/");

  // load ten-star.csv file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {stars/ten-star.csv}{true}");
  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  // validate load success output
  const output = "success- file loaded successfully";
  const tdElement = page.locator(".repl-history").locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output);

  // view file
  await page.getByLabel("Command input").fill("view");
  await button.click();

  // validate CSV data
  const body1 = page.locator(".output1").locator("table").locator("tbody");
  await expect(body1.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "StarID"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "ProperName"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(2)).toHaveText("X");
  await expect(body1.locator("tr").nth(0).locator("td").nth(3)).toHaveText("Y");
  await expect(body1.locator("tr").nth(0).locator("td").nth(4)).toHaveText("Z");
  await expect(body1.locator("tr").nth(1).locator("td").nth(0)).toHaveText("0");
  await expect(body1.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "Sol"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(2)).toHaveText("0");
  await expect(body1.locator("tr").nth(1).locator("td").nth(3)).toHaveText("0");
  await expect(body1.locator("tr").nth(1).locator("td").nth(4)).toHaveText("0");

  await expect(body1.locator("tr").nth(2).locator("td").nth(0)).toHaveText("1");
  await expect(body1.locator("tr").nth(2).locator("td").nth(1)).toHaveText("");
  await expect(body1.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "282.43485"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(3)).toHaveText(
    "0.00449"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(4)).toHaveText(
    "5.36884"
  );

  await expect(body1.locator("tr").nth(3).locator("td").nth(0)).toHaveText("2");
  await expect(body1.locator("tr").nth(3).locator("td").nth(1)).toHaveText("");
  await expect(body1.locator("tr").nth(3).locator("td").nth(2)).toHaveText(
    "43.04329"
  );
  await expect(body1.locator("tr").nth(3).locator("td").nth(3)).toHaveText(
    "0.00285"
  );
  await expect(body1.locator("tr").nth(3).locator("td").nth(4)).toHaveText(
    "-15.24144"
  );

  await expect(body1.locator("tr").nth(4).locator("td").nth(0)).toHaveText("3");
  await expect(body1.locator("tr").nth(4).locator("td").nth(1)).toHaveText("");
  await expect(body1.locator("tr").nth(4).locator("td").nth(2)).toHaveText(
    "277.11358"
  );
  await expect(body1.locator("tr").nth(4).locator("td").nth(3)).toHaveText(
    "0.02422"
  );
  await expect(body1.locator("tr").nth(4).locator("td").nth(4)).toHaveText(
    "223.27753"
  );

  await expect(body1.locator("tr").nth(5).locator("td").nth(0)).toHaveText(
    "3759"
  );
  await expect(body1.locator("tr").nth(5).locator("td").nth(1)).toHaveText(
    "96 G. Psc"
  );
  await expect(body1.locator("tr").nth(5).locator("td").nth(2)).toHaveText(
    "7.26388"
  );
  await expect(body1.locator("tr").nth(5).locator("td").nth(3)).toHaveText(
    "1.55643"
  );
  await expect(body1.locator("tr").nth(5).locator("td").nth(4)).toHaveText(
    "0.68697"
  );

  await expect(body1.locator("tr").nth(6).locator("td").nth(0)).toHaveText(
    "70667"
  );
  await expect(body1.locator("tr").nth(6).locator("td").nth(1)).toHaveText(
    "Proxima Centauri"
  );
  await expect(body1.locator("tr").nth(6).locator("td").nth(2)).toHaveText(
    "-0.47175"
  );
  await expect(body1.locator("tr").nth(6).locator("td").nth(3)).toHaveText(
    "-0.36132"
  );
  await expect(body1.locator("tr").nth(6).locator("td").nth(4)).toHaveText(
    "-1.15037"
  );

  await expect(body1.locator("tr").nth(7).locator("td").nth(0)).toHaveText(
    "71454"
  );
  await expect(body1.locator("tr").nth(7).locator("td").nth(1)).toHaveText(
    "Rigel Kentaurus B"
  );
  await expect(body1.locator("tr").nth(7).locator("td").nth(2)).toHaveText(
    "-0.50359"
  );
  await expect(body1.locator("tr").nth(7).locator("td").nth(3)).toHaveText(
    "-0.42128"
  );
  await expect(body1.locator("tr").nth(7).locator("td").nth(4)).toHaveText(
    "-1.1767"
  );

  await expect(body1.locator("tr").nth(8).locator("td").nth(0)).toHaveText(
    "71457"
  );
  await expect(body1.locator("tr").nth(8).locator("td").nth(1)).toHaveText(
    "Rigel Kentaurus A"
  );
  await expect(body1.locator("tr").nth(8).locator("td").nth(2)).toHaveText(
    "-0.50362"
  );
  await expect(body1.locator("tr").nth(8).locator("td").nth(3)).toHaveText(
    "-0.42139"
  );
  await expect(body1.locator("tr").nth(8).locator("td").nth(4)).toHaveText(
    "-1.17665"
  );

  await expect(body1.locator("tr").nth(9).locator("td").nth(0)).toHaveText(
    "87666"
  );
  await expect(body1.locator("tr").nth(9).locator("td").nth(1)).toHaveText(
    "Barnard's Star"
  );
  await expect(body1.locator("tr").nth(9).locator("td").nth(2)).toHaveText(
    "-0.01729"
  );
  await expect(body1.locator("tr").nth(9).locator("td").nth(3)).toHaveText(
    "-1.81533"
  );
  await expect(body1.locator("tr").nth(9).locator("td").nth(4)).toHaveText(
    "0.14824"
  );

  await expect(body1.locator("tr").nth(10).locator("td").nth(0)).toHaveText(
    "118721"
  );
  await expect(body1.locator("tr").nth(10).locator("td").nth(1)).toHaveText("");
  await expect(body1.locator("tr").nth(10).locator("td").nth(2)).toHaveText(
    "-2.28262"
  );
  await expect(body1.locator("tr").nth(10).locator("td").nth(3)).toHaveText(
    "0.64697"
  );
  await expect(body1.locator("tr").nth(10).locator("td").nth(4)).toHaveText(
    "0.29354"
  );

  // search for a value
  await page.getByLabel("Command input").fill("search {Sol}");
  await page.keyboard.press("Enter");

  // validate search results
  const body2 = page.locator(".output2").locator("table").locator("tbody");
  await expect(body2.locator("tr").nth(1).locator("td").nth(0)).toHaveText("0"); // skip the header row
  await expect(body2.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "Sol"
  );
  await expect(body2.locator("tr").nth(1).locator("td").nth(2)).toHaveText("0");
  await expect(body2.locator("tr").nth(1).locator("td").nth(3)).toHaveText("0");
  await expect(body2.locator("tr").nth(1).locator("td").nth(4)).toHaveText("0");

  // broadband request for Virginia Beach City, Virginia
  await page
    .getByLabel("Command input")
    .fill("broadband {Virginia}{Virginia Beach City}");
  await page.keyboard.press("Enter");

  // validate broadband result
  const output3 =
    "Broadband percentage for Virginia Beach City, Virginia is: 92";
  const tdElement3 = page.locator(".output3").locator("td");
  const actualOutput3 = await tdElement3.textContent();
  expect(actualOutput3).toBe(output3);

  // load ri-acs-5-year file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {ri-acs-5-year-2017-2021.csv}{true}");
  // Click the button
  await page.getByLabel("button").click();

  // validate load success output
  const output4 = "success- file loaded successfully";
  const tdElement4 = page.locator(".output4").locator("td");
  const actualOutput4 = await tdElement4.textContent();
  expect(actualOutput4).toBe(output4);

  // search for a value
  await page.getByLabel("Command input").fill("search {Rhode Island}");
  await page.keyboard.press("Enter");

  // validate search results
  const body5 = page.locator(".output5").locator("table").locator("tbody");
  await expect(body5.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "Rhode Island"
  ); // skip the header row
  await expect(body5.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "74,489.00"
  );
  await expect(body5.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "95,198.00"
  );
  await expect(body5.locator("tr").nth(1).locator("td").nth(3)).toHaveText(
    "39,603.00"
  );
});

test("search no rows found", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:5173/");

  // load ten-star.csv file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {stars/ten-star.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  // validate load successful
  const output = "success- file loaded successfully";
  const tdElement = page.locator(".repl-history").locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output);

  // no rows found searching entire CSV
  await page.getByLabel("Command input").fill("search {adafasdfasdf}");
  await page.keyboard.press("Enter");

  // validate error output
  const output1 = "error: No matching rows found.";
  const tdElement1 = page.locator(".output1").locator("td");
  const actualOutput1 = await tdElement1.textContent();
  expect(actualOutput1).toBe(output1);

  // no rows found searching by column name
  await page.getByLabel("Command input").fill("search {Sol}{--colName}{X}");
  await page.keyboard.press("Enter");

  // validate error output
  const output2 = "error: No matching rows found.";
  const tdElement2 = page.locator(".output2").locator("td");
  const actualOutput2 = await tdElement2.textContent();
  expect(actualOutput2).toBe(output2);

  // no rows found searching by column index
  await page.getByLabel("Command input").fill("search {Sol}{--colIndex}{3}");
  await page.keyboard.press("Enter");

  // validate error output
  const output3 = "error: No matching rows found.";
  const tdElement3 = page.locator(".output3").locator("td");
  const actualOutput3 = await tdElement3.textContent();
  expect(actualOutput3).toBe(output3);
});

test("search by column ID", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:5173/");

  // load ten-star.csv file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {stars/ten-star.csv}{true}");
  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  // validate load success output
  const output = "success- file loaded successfully";
  const tdElement = page.locator(".repl-history").locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output);

  // search for a value by column name
  await page
    .getByLabel("Command input")
    .fill("search {Sol}{--colName}{ProperName}");
  await page.keyboard.press("Enter");

  // validate search results
  const body1 = page.locator(".output1").locator("table").locator("tbody");
  await expect(body1.locator("tr").nth(1).locator("td").nth(0)).toHaveText("0"); // skip the header row
  await expect(body1.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "Sol"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(2)).toHaveText("0");
  await expect(body1.locator("tr").nth(1).locator("td").nth(3)).toHaveText("0");
  await expect(body1.locator("tr").nth(1).locator("td").nth(4)).toHaveText("0");

  // search for a value by column name
  await page.getByLabel("Command input").fill("search {Sol}{--colIndex}{1}");
  await page.keyboard.press("Enter");

  // validate search results
  const body2 = page.locator(".output1").locator("table").locator("tbody");
  await expect(body2.locator("tr").nth(1).locator("td").nth(0)).toHaveText("0"); // skip the header row
  await expect(body2.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "Sol"
  );
  await expect(body2.locator("tr").nth(1).locator("td").nth(2)).toHaveText("0");
  await expect(body2.locator("tr").nth(1).locator("td").nth(3)).toHaveText("0");
  await expect(body2.locator("tr").nth(1).locator("td").nth(4)).toHaveText("0");
});

test("load an unknown file", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:5173/");

  // load malicious.csv
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {C:\\Code\\CS320\\malicious-file.csv}{false}");
  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  // validate load error output
  // const output =
  //   "error- File path not found: back/data/C:\\Code\\CS320\\malicious-file.csv";
  const output =
    "error- File path not found: back/data/C:\\Code\\CS320\\malicious-file.csv";
  const tdElement = page.locator(".repl-history").locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output);

  // load malicious.csv
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {stars/ten-star.csv}{idk}");
  // Click the button
  await page.getByLabel("button").click();

  // validate load error output
  const output1 = "error- Invalid header parameter: idk";
  const tdElement1 = page.locator(".output1").locator("td");
  const actualOutput1 = await tdElement1.textContent();
  expect(actualOutput1).toBe(output1);
});

// Proves that frontend can handle backend error responses
test("broadband errors", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:5173/");

  // broadband request for Ontario, Canada
  await page.getByLabel("Command input").fill("broadband {Canada}{Ontario}");
  await page.keyboard.press("Enter");

  // validate broadband error
  const output0 = "error: edu.brown.cs.student.main.server.exceptions.BadJsonException: The state you have given cannot be found: Canada";
  const tdElement = page.locator(".output0").locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output0);
});

test("view errors", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:5173/");

  // view when no file has been loaded
  await page.getByLabel("Command input").fill("view");
  await page.keyboard.press("Enter");

  // validate view error
  const output0 = "error: No file has been loaded.";
  const tdElement = page.locator(".output0").locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output0);
});