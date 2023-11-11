import { test, expect } from "@playwright/test";

// test viewing a bad file
test("after typing search command without loading a file, the error output is printed", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // entering view command
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  //output
  const output = await page.textContent(
    "text=No CSV loaded -- please call load_file first!"
  );

  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();
  expect(actualOutput).toBe(output);
});

// test viewing a bad file
test("after typing search command after loading an incorrect file, the error output is printed", async ({
  page,
}) => {
  //go to page
  await page.goto("http://localhost:8000/");

  //load the bad csv
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{sdfasd.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 = "CSV NOT successfully loaded -- invalid filepath";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);

  // type view in
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");

  // click button
  await button.click();

  const output2 = "No CSV loaded -- please call load_file first!";
  const tdElement2 = await page.locator("td").all();
  const actualOutput2 = await tdElement2[1].textContent();

  expect(actualOutput2).toBe(output2);
});

// test that we can actually view table
test("after loading in a correct file, we are able to view its correct contents", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // load good file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{mixedCSV.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  //typing view
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const body = await page.locator(".output1").locator("table").locator("tbody");

  // checking contents of table
  await expect(body.locator("tr").nth(0).locator("td").nth(0)).toHaveText("1");
  await expect(body.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "string"
  );
  await expect(body.locator("tr").nth(0).locator("td").nth(2)).toHaveText("34");
  await expect(body.locator("tr").nth(1).locator("td").nth(0)).toHaveText("89");
  await expect(body.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "true"
  );
  await expect(body.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "2345"
  );
});

// tests what happens when we view tables after each other
test("test that we can see two valid tables loaded one after each other", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // load good file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{mixedCSV.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  //typing view
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const body = await page.locator(".output1").locator("table").locator("tbody");

  await expect(body.locator("tr").nth(0).locator("td").nth(0)).toHaveText("1");
  await expect(body.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "string"
  );
  await expect(body.locator("tr").nth(0).locator("td").nth(2)).toHaveText("34");
  await expect(body.locator("tr").nth(1).locator("td").nth(0)).toHaveText("89");
  await expect(body.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "true"
  );
  await expect(body.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "2345"
  );

  // load another good file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  await button.click();

  // view the file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const body1 = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody");

  // check that contents are accurate
  await expect(body1.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );
});

// tests what happens when we do a bad search then a good one right after
test("tests that we can still view a valid file after loading and viewing a bad one", async ({
  page,
}) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // load a bad file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{bad.csv}{false}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  //typing view
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const output2 = "No CSV loaded -- please call load_file first!";
  const tdElement2 = await page.locator("td").all();
  const actualOutput2 = await tdElement2[1].textContent();

  expect(actualOutput2).toBe(output2);

  // load a good file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{headerCSV.csv}{true}");
  await button.click();

  // view the good file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const body1 = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody");

  await expect(body1.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );
});

// tests what happens when we do a good search then a bad one right after
test("tests that the correct error is printed when viewing a bad file after viewing a good file", async ({
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

  //typing view
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const body1 = await page
    .locator(".output1")
    .locator("table")
    .locator("tbody");

  // test that the contents are the same
  await expect(body1.locator("tr").nth(0).locator("td").nth(0)).toHaveText(
    "name"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(1)).toHaveText(
    "age"
  );
  await expect(body1.locator("tr").nth(0).locator("td").nth(2)).toHaveText(
    "favorite root vegetable"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(0)).toHaveText(
    "isaac yi"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(1)).toHaveText(
    "20"
  );
  await expect(body1.locator("tr").nth(1).locator("td").nth(2)).toHaveText(
    "potato"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(0)).toHaveText(
    "jonathan zhou"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(1)).toHaveText(
    "18"
  );
  await expect(body1.locator("tr").nth(2).locator("td").nth(2)).toHaveText(
    "garlic"
  );

  // load bad file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {load_file}{bad.csv}{false}");
  await button.click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mock {view}");
  await button.click();

  const output2 = "No CSV loaded -- please call load_file first!";
  const body2 = await page
    .locator(".output3")
    .locator("table")
    .locator("tbody")
    .locator("tr");

  await expect(body2.locator("td").nth(0)).toHaveText(output2);
});
