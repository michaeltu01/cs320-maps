import { test, expect } from '@playwright/test'

// setting before each test
test.beforeEach(async ({page}) => {
    await page.goto('http://localhost:5173/');
  })

  // tests that when we click on the map where data is defined, correct click output is shown
  test('test click on valid map', async ({ page }) =>{

    await page.getByRole('region', { name: 'Map' }).click({
      position: {
        x: 269,
        y: 338
      }
    });
    await expect(page.getByText('state: RI, city: Providence, holc_grade: C name: undefined, Broadband: 85.4')).toBeDefined;
    
  });

  // tests that when we click on valid and then invalid part of map, correct clicks are shown

  test('test click on valid map then invalid map', async ({ page }) =>{

    await page.getByRole('region', { name: 'Map' }).click({
      position: {
        x: 269,
        y: 338
      }
    });
    await expect(page.getByText('state: RI, city: Providence, holc_grade: C name: undefined, Broadband: 85.4')).toBeDefined;

    await page.getByRole('region', { name: 'Map' }).click({
      position: {
        x: 68,
        y: 231
      }
    });
    await expect(page.getByText('No data defined in click region. Try again')).toBeDefined;
  });

  // tests when we click on two valid parts of the map
  test('test click on valid map then valid map', async ({ page }) =>{

    await page.getByRole('region', { name: 'Map' }).click({
      position: {
        x: 269,
        y: 338
      }
    });
    await expect(page.getByText('state: RI, city: Providence, holc_grade: C name: undefined, Broadband: 85.4')).toBeDefined;

    await page.getByRole('region', { name: 'Map' }).click({
    position: {
        x: 269,
        y: 338
    }
    });
    await expect(page.getByText('state: RI, city: Providence, holc_grade: C name: undefined, Broadband: 85.4')).toBeDefined;
  });
