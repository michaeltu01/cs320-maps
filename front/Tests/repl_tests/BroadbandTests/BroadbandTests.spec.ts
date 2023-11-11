import { test, expect } from "@playwright/test";

/**
 *
 * BROADBAND TESTS
 *
 */

// illformed broadband searches
test("Missing parameters in broadband query", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband {mufasa}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 = "Invalid Format of Parameters";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);
});

//Valid form of inputs, but a county or state that doesn't exist
test("State not found", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {broadband}{BadState}{San Diego}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 =
    "error: The state that you have given cannot be found: BadState";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);
});

test("County not found", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {broadband}{California}{BadCounty}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 =
    "error: The county that you have given cannot be found: BadCounty";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);
});

//Valid query -- success
test("Successful broadband request", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:8000/");

  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("mock {broadband}{Virginia}{Virginia Beach City}");

  // Click the button
  const button = await page.locator("button");
  await button.click();

  const output1 =
    "Broadband percentage for Virginia Beach City, Virginia is: 89.9";
  const tdElement = await page.locator("td");
  const actualOutput = await tdElement.textContent();

  expect(actualOutput).toBe(output1);
});
