import { test, expect } from '@playwright/test'

// setting before each test
test.beforeEach(async ({page}) => {
    await page.goto('http://localhost:5173/');
  })
  
  // testing for a good broadband call in the backend
test('working broadband produces correct output. mocked test', async ({ page }) => {
    await page.getByLabel('Command input').click();
    await page.getByLabel('Command input').fill('search_json');
    await page.getByRole('button', {name: "Submit"}).click()
    await expect(page.getByLabel('output')).toContainText('Invalid parameters');
  });

