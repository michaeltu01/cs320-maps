import { test, expect } from "@playwright/test";

// If you needed to do something before every test case...
test.beforeEach(() => {
  // ... you'd put it here.
  // TODO: Is there something we need to do before every test case to avoid repeating code?
});

test('the "Enter a command:" text is present on the page', async ({ page }) => {
  // Navigate to the page
  await page.goto("http://localhost:5173/");

  // Check if the text "Enter a command:" is present in the page content
  const isTextPresent = await page.textContent("text=Enter a command:");

  // Assert that the text is present
  expect(isTextPresent).not.toBeNull();
});

// making sure that we see the submitted 0 times on initial load
test('the "Submitted 0 times" text is present on the page', async ({
  page,
}) => {
  // Navigate to the page
  await page.goto("http://localhost:5173/"); // Replace with the actual URL

  const isTextPresent = await page.textContent("text=Submitted 0 times");

  expect(isTextPresent).not.toBeNull();
});

test("after I click the button, my command gets pushed", async ({ page }) => {
  // Navigate to the page containing your button
  await page.goto("http://localhost:5173/"); // Replace with the actual URL

  // Find the button element
  const button = await page.getByLabel('button')
  // Get the initial label of the button
  const initialLabel = (await button.textContent()) || "";

  // Click the button
  await button.click();

  // Get the label of the button after the click
  const labelAfterClick = (await button.textContent()) || "";

  // Parse the label values to integers and check if it has incremented
  const initialCount = parseInt(initialLabel, 10);
  const countAfterClick = parseInt(labelAfterClick, 10);

  // Assert that the count has incremented by one
  expect(countAfterClick).toBe(initialCount + 1);
});

test("after I type into the input box, its text changes", async ({ page }) => {
  // Step 1: Navigate to a URL
  await page.goto("http://localhost:5173/");

  // Step 2: Interact with the page
  // Locate the element you are looking for
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");

  // Step 3: Assert something about the page
  // Assertions are done by using the expect() function
  const mock_input = `Awesome command`;
  await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
});
