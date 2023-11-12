import { test, expect } from '@playwright/test'

// setting before each test
test.beforeEach(async ({page}) => {
    await page.goto('http://localhost:5173/');
  })
  
  // test that when we do not input anything, the default json is loaded
test('testing that the default json loads when we load_json nothing', async ({ page }) => {
    await page.getByLabel('Command input').click();
    await page.getByLabel('Command input').fill('load_json');
    await page.getByLabel('button').click()
    await expect(page.getByLabel('output')).toContainText('success- file loaded successfully');
  });

  // test that when we input a correct json, the json is loaded
test('testing that the correct output message is loaded when we load_json', async ({ page }) => {
    await page.getByLabel('Command input').click();
    await page.getByLabel('Command input').fill('load_json {back/data/geodata/fullDownload.json}');
    await page.getByLabel('button').click()
    await expect(page.getByLabel('output')).toContainText('success- file loaded successfully');
})

// test for the error message
test('testing that the incorrect file path produces the error message', async ({ page }) => {
    await page.getByLabel('Command input').click();
    await page.getByLabel('Command input').fill('load_json {asdfasdfasdf}');
    await page.getByLabel('button').click()
    await expect(page.getByLabel('output')).toContainText('error- asdfasdfasdf (No such file or directory)');
})

// test that we can load two things in a row
test('load two things in a row', async ({ page }) => {
  await page.getByLabel('Command input').click();
  await page.getByLabel('Command input').fill('load_json {back/data/geodata/fullDownload.json}');
  await page.getByLabel('button').click()
  await expect(page.getByLabel('output')).toContainText('success- file loaded successfully');

  await page.getByLabel('Command input').click();
  await page.getByLabel('Command input').fill('load_json');
  await page.getByLabel('button').click()
  await expect(page.getByLabel('output')).toContainText('success- file loaded successfully');
})
