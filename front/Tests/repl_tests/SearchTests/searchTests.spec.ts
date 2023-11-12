import { test, expect } from "@playwright/test";

/**
 * SEARCH TESTS
 */

/**
 * Error handling and ill-formed input parameters for search
 */

//no load
test("after typing search command without loading a file, the error output is printed", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //search without loading
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{name}{isaac yi}");
  await page.getByLabel('button').click()

  //check error output
  const isTextPresent = await page.textContent(
    "text=No CSV loaded -- please call load_file first!"
  );

  expect(isTextPresent).not.toBeNull();
});

//wrong number of input parameters
test("after typing search command with wrong number of inputs, the error output is printed", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the illformed search
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {search}{babboey}{get}{rekt}{kid}");
  await button.click();

  //check for error message
  const actualOutput = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr")
    .locator("td");
  await expect(actualOutput).toHaveText("Invalid Format of Parameters");
});

//wrong format of inputs
test("after typing search command without correctly formatted inputs, the error output is printed", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the illformed search
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{name}{isaac yi}");
  await button.click();

  //check for error message
  const actualOutput = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr")
    .locator("td");
  await expect(actualOutput).toHaveText("Invalid Format of Parameters");
});

/**
 * Properly formed search queries with no column identifier
 */

//search for target string that doesn't exist
test("searching for data that doesn't exist in loaded CSV", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{emptyCSV.csv}{false}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{bro}");
  await button.click();

  //check for validity
  const actualOutput = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr")
    .locator("td");

  await expect(actualOutput).toHaveText("No matching rows found");
});

//search for target string that does exist -- no column identifier
test("searching for valid tuple with no col identifier", async ({ page }) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{mixedCSV.csv}{false}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{string}");
  await button.click();

  //check for validity
  const row = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr");

  await expect(row.locator("td").nth(0)).toHaveText("1");
  await expect(row.locator("td").nth(1)).toHaveText("string");
  await expect(row.locator("td").nth(2)).toHaveText("34");
});

//search that returns multiple rows -- no column identifier
test("searching for multiple rows -- no column identifier", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{doubleCSV.csv}{false}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{hi}");
  await button.click();

  //check for validity
  const body = await page.locator(".output1").locator("table").locator("tbody");

  await expect(body.locator("tr").nth(0).locator("td").nth(0)).toHaveText("hi");
  await expect(body.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "san diego"
  );
  await expect(body.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "california"
  );
  await expect(body.locator("tr").nth(1).locator("td").nth(0)).toHaveText("hi");
  await expect(body.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "bruh"
  );
  await expect(body.locator("tr").nth(1).locator("td").nth(2)).toHaveText("in");
});

/**
 * Wellformed searches with index column identifier
 */

//search for target string that does exist, but not in the column given by column identifier
test("searching for valid tuple, but isn't in the column given by index col identifier", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {search}{--colIndex}{2}{isaac yi}");
  await button.click();

  //check for no rows output
  const row = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr");

  await expect(row.locator("td")).toHaveText("No matching rows found");
});

//valid tuple that does exist in column given by index identifier
test("searching for valid tuple with valid col index identifier", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {search}{--colIndex}{0}{isaac yi}");
  await button.click();

  //check for validity
  const row = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr");

  await expect(row.locator("td").nth(0)).toHaveText("isaac yi");
  await expect(row.locator("td").nth(1)).toHaveText("20");
  await expect(row.locator("td").nth(2)).toHaveText("potato");
});

/**
 * Wellformed searching with name column identifier
 */

//search for target string that does exist, but not in the column given by name column identifier
test("searching for valid tuple, but isn't in the column given by index name identifier", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {search}{--colName}{age}{isaac yi}");
  await button.click();

  //check for no rows output
  const row = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr");

  await expect(row.locator("td")).toHaveText("No matching rows found");
});

//valid tuple that does exist in column given by name identifier
test("searching for valid tuple with valid col name identifier", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {search}{--colName}{name}{isaac yi}");
  await button.click();

  //check for validity
  const row = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody")
    .locator("tr");

  await expect(row.locator("td").nth(0)).toHaveText("isaac yi");
  await expect(row.locator("td").nth(1)).toHaveText("20");
  await expect(row.locator("td").nth(2)).toHaveText("potato");
});

//search that returns multiple rows -- with column name identifier
test("searching for multiple rows with valid column name identifier", async ({
  page,
}) => {
  await page.goto("http://localhost:5173/");

  //load successfully
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{doubleCSV.csv}{false}");

  // Click the button
  const button = await page.getByLabel('button')
  await button.click();

  //do the search
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {search}{--colIndex}{0}{hi}");
  await button.click();

  //check for validity
  const body = await page.locator(".output1").locator("table").locator("tbody");

  await expect(body.locator("tr").nth(0).locator("td").nth(0)).toHaveText("hi");
  await expect(body.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "san diego"
  );
  await expect(body.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "california"
  );
  await expect(body.locator("tr").nth(1).locator("td").nth(0)).toHaveText("hi");
  await expect(body.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "bruh"
  );
  await expect(body.locator("tr").nth(1).locator("td").nth(2)).toHaveText("in");
});
