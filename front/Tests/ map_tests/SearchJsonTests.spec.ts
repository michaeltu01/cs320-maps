import { test, expect } from '@playwright/test'

// setting before each test
test.beforeEach(async ({page}) => {
    await page.goto('http://localhost:5173/');
  })
  
  // testing that typing search json and then no string results in the correct output
test('searching for nothing', async ({ page }) => {
    await page.getByLabel('Command input').click();
    await page.getByLabel('Command input').fill('search_json');
    await page.getByRole('button', {name: "Submit"}).click()
    await expect(page.getByLabel('output')).toContainText('Missing Search Keyword');
  });

  // testing that if we search for something that doesnt exist, the correct thing is outputted
test('searching for a non-existing string produces the correct output', async ({ page }) => {
  await page.getByLabel('Command input').click();
  await page.getByLabel('Command input').fill('search_json {asdfasdf}');
  await page.getByRole('button', {name: "Submit"}).click()
  await expect(page.getByLabel('output')).toContainText('"asdfasdf" not in data');

})

  // testing for a working search
test('testing a working search.', async ({ page }) => {
  await page.getByLabel('Command input').click();
  await page.getByLabel('Command input').fill('search_json {schools}');
  await page.getByRole('button', {name: "Submit"}).click()
  await expect(page.getByLabel('output')).toContainText('Successful search for: "schools"');
})