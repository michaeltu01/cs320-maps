import { test, expect } from "@playwright/test";

test("after I type into the input box, its text changes", async ({ page }) => {
  // Step 1: Navigate to a URL
  await page.goto("http://localhost:8000/");

  // Step 2: Interact with the page
  // Locate the element you are looking for
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");

  // Step 3: Assert something about the page
  // Assertions are done by using the expect() function
  const mock_input = `Awesome command`;
  await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
});

// tests that a correct file loaded into the input box returns success
test("after I load a correct file into the input box, the correct output is printed", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // load good file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const mock_input = "CSV successfully loaded";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();
  // check that we print the correct thing
  expect(actualOutput).toBe(mock_input);
});

// this tests that an incorrect file loaded into the input box returns an error
test("after I load an incorrect file into the input box, the error output is printed", async ({
  page,
}) => {
  //go to page
  await page.goto("http://localhost:8000/");

  // load a bad csv
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{selrjkrdfj.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output = await page.textContent(
    "text=CSV NOT successfully loaded -- invalid filepath"
  );

  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  // check that we print the correct thing
  expect(actualOutput).toBe(output);
});

// test bad load
test("after I load an empty path into the input box, the error output is printed", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // loading an empty string
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {load_file}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output = await page.textContent(
    "text=CSV NOT successfully loaded -- invalid filepath"
  );
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output);
});

// test good load then good load
test("load one valid CSV then a different valid CSV should print two correct outputs", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // load a good file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  // check output
  const output1 = "CSV successfully loaded";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{doubleCSV.csv}{false}");

  await button.click();

  const output2 = "CSV successfully loaded";
  const tdElements = await page.locator("td").all();
  const actualOutput2 = await tdElements[1].textContent();

  expect(actualOutput2).toBe(output2);
});

// test good load then bad load
test("loading one valid CSV then an invalid CSV should print a success then a error", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 = "CSV successfully loaded";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{asdfadsf.csv}{false}");

  await button.click();

  const output2 = "CSV NOT successfully loaded -- invalid filepath";
  const tdElements = await page.locator("td").all();
  const actualOutput2 = await tdElements[1].textContent();

  expect(actualOutput2).toBe(output2);
});

// test bad load then good load
test("loading one invalid CSV then a valid CSV should print an error then a success", async ({
  page,
}) => {
  // go to apge
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{sdfadsfa.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 = "CSV NOT successfully loaded -- invalid filepath";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");

  await button.click();

  const output2 = "CSV successfully loaded";
  const tdElements = await page.locator("td").all();
  const actualOutput2 = await tdElements[1].textContent();

  expect(actualOutput2).toBe(output2);
});

// test bad load then bad laod
test("loading two invalid CSVs should print two errors", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{sdfadsfa.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 = "CSV NOT successfully loaded -- invalid filepath";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {load_file}");

  await button.click();

  const output2 = "CSV NOT successfully loaded -- invalid filepath";
  const tdElements = await page.locator("td").all();
  const actualOutput2 = await tdElements[1].textContent();

  expect(actualOutput2).toBe(output2);
});
