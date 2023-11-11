import { test, expect } from "@playwright/test";

test.beforeEach("populate the history to test", async ({ page }) => {
  // go to page
  await page.goto("http://localhost:8000/");

  // switch mode
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode");
  const button = await page.locator("button");
  await button.click();

  // load a file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file {stars/ten-star.csv}{true}");
  await button.click();

  // view the file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await button.click();

  // search the file
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("search {Sol}");
  await button.click();
});

test("up arrow functionality", async ({ page }) => {
  // set focus to the input box
  await page.getByLabel("Command input").click();
  const inputBox = page.getByLabel("Command input");
  expect(inputBox).toHaveValue("");

  await inputBox.press("ArrowUp");
  expect(inputBox).toHaveValue("search {Sol}");

  await inputBox.press("ArrowUp");
  expect(inputBox).toHaveValue("view");

  await page.keyboard.press("ArrowUp");
  expect(inputBox).toHaveValue("load_file {stars/ten-star.csv}{true}");

  await page.keyboard.press("ArrowUp");
  expect(inputBox).toHaveValue("mode");
});
