import { test, expect } from "@playwright/test";

/**
 * INTEGRATION TESTS
 */

// MODE TEST
test("test that we can switch modes", async ({ page }) => {
  await page.goto("http://localhost:8000/");

  // switch to verbose
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  const button = await page.locator("button");
  await button.click();

  // check output
  const mock_input = "Successfully switched mode";
  const tdElement = await page.locator("td");
  const tdElement2 = await page.locator("p").all();
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(mock_input);

  const body1 = await page.locator(".output0").locator("p");

  await expect(body1.nth(0).nth(0)).toHaveText("Inputted Command: mode");

  // switch back to brief
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await button.click();

  // check output
  const mock_input3 = "Successfully switched mode";
  const tdElement3 = await page.locator("td").all();
  const actualOutput3 = await tdElement3[1].textContent();
  // check that we print the correct thing
  expect(actualOutput3).toBe(mock_input3);
});

test("load mode view mode", async ({ page }) => {
  await page.goto("http://localhost:8000/");

  //load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  const button = await page.locator("button");
  await button.click();

  // switch mode
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await button.click();

  const body1 = await page.locator(".output0").locator("p");

  await expect(body1.nth(0).nth(0)).toHaveText(
    "Inputted Command: mock {load_file}{headerCSV.csv}{true}"
  );

  // view contents
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const body2 = await page.locator(".output2").locator("p");

  await expect(body2.nth(0).nth(0)).toHaveText("Inputted Command: mock {view}");

  // change mode again
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  await button.click();

  const body3 = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody")
    .locator("tr")
    .locator("td");

  // check that we print the correct thing
  await expect(body3.nth(0).nth(0)).toHaveText("Successfully switched mode");
});

//Searcher integration test for multiple searches
test("Search multiple times on the same load", async ({ page }) => {
  await page.goto("http://localhost:8000/");

  //load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  const button = await page.locator("button");
  await button.click();

  //view and check validity for first row
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{isaac yi}");
  await button.click();

  const firstBody = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody");

  await expect(firstBody.locator("tr").locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(firstBody.locator("tr").locator("td").nth(1)).toHaveText("20");
  await expect(firstBody.locator("tr").locator("td").nth(2)).toHaveText(
    "potato"
  );

  //do second search and check for validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{18}");
  await button.click();

  const secondBody = await page
    .locator(".output2")
    .locator("table")
    .locator("tbody");

  await expect(secondBody.locator("tr").locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(secondBody.locator("tr").locator("td").nth(1)).toHaveText("18");
  await expect(secondBody.locator("tr").locator("td").nth(2)).toHaveText(
    "garlic"
  );
});

//State Change: Loading CSV with header then CSV with no header
test("state change: load CSV with header then CSV with no header", async ({
  page,
}) => {
  await page.goto("http://localhost:8000/");

  //load CSV with header
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  const button = await page.locator("button");
  await button.click();

  //view and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const firstBody = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody");

  await expect(firstBody.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(firstBody.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(firstBody.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );

  //load CSV with no header
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{mixedCSV.csv}{false}");
  await button.click();

  //view CSV and confirm validity and state change
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const secondBody = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody");

  await expect(secondBody.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "1"
  );
  await expect(secondBody.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "string"
  );
  await expect(secondBody.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "34"
  );
  await expect(secondBody.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "89"
  );
  await expect(secondBody.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "true"
  );
  await expect(secondBody.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "2345"
  );
});

//Doing all 3 commands together --> Load -> View -> Search
test("Using all 3 commands sequentially", async ({ page }) => {
  await page.goto("http://localhost:8000/");

  //load CSV with header
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  const button = await page.locator("button");
  await button.click();

  //view and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const firstBody = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody");

  await expect(firstBody.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(firstBody.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(firstBody.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );

  //search and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{isaac yi}");
  await button.click();

  const secondBody = await page
    .locator(".output2")
    .locator("table")
    .locator("tbody");

  await expect(secondBody.locator("tr").locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(secondBody.locator("tr").locator("td").nth(1)).toHaveText("20");
  await expect(secondBody.locator("tr").locator("td").nth(2)).toHaveText(
    "potato"
  );
});

//Load, Search, then Load a different CSV and Search
test("Loading and searching on 2 different CSVs", async ({ page }) => {
  await page.goto("http://localhost:8000/");

  //load CSV with header
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  const button = await page.locator("button");
  await button.click();

  //search and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{isaac yi}");
  await button.click();

  const firstBody = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody");

  await expect(firstBody.locator("tr").locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(firstBody.locator("tr").locator("td").nth(1)).toHaveText("20");
  await expect(firstBody.locator("tr").locator("td").nth(2)).toHaveText(
    "potato"
  );

  //load new CSV with header
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{mixedCSV.csv}{false}");
  await button.click();

  //search and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {search}{string}");
  await button.click();

  const secondBody = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody");

  await expect(secondBody.locator("tr").locator("td").nth(0)).toHaveText("1");
  await expect(secondBody.locator("tr").locator("td").nth(1)).toHaveText(
    "string"
  );
  await expect(secondBody.locator("tr").locator("td").nth(2)).toHaveText("34");
});

//Test CSV and ACS functionality together
//Load and view, then broadband, then view
test("Testing interactions between CSV and broadband", async ({ page }) => {
  await page.goto("http://localhost:8000/");

  //load CSV with header
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  const button = await page.locator("button");
  await button.click();

  //view and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const firstBody = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody");

  await expect(firstBody.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(firstBody.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(firstBody.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(firstBody.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(firstBody.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );

  //search and check validity
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {broadband}{Virginia}{Virginia Beach City}");
  await button.click();

  const secondBody = await page
    .locator(".output2")
    .locator("table")
    .locator("tbody");

  await expect(secondBody.locator("tr").locator("td")).toHaveText(
    "Broadband percentage for Virginia Beach City, Virginia is: 89.9"
  );

  //view again and check validity
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const thirdBody = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody");

  await expect(thirdBody.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(thirdBody.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(thirdBody.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(thirdBody.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(thirdBody.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(thirdBody.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(thirdBody.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(thirdBody.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(thirdBody.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );
});
