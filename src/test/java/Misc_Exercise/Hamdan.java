package Misc_Exercise;

import Utils.DriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Locale;

public class Hamdan {
    static WebDriver driver;

    @BeforeClass
    void setup() {
        driver = DriverUtils.getWebDriver();
        driver.get("https://demoqa.com");
        DriverUtils.setTimeout(driver, 5000);
    }

    @Test(priority = 1)
    void navigateToElementMenu() throws InterruptedException {
        Thread.sleep(3000);
        driver.findElement(By.cssSelector("#app > div > div > div.home-body > div > div:nth-child(1)")).click();
    }


    @AfterClass
    void quit() {
        driver.quit();}

}
//
//    @Test
//    void findEliments() throws InterruptedException {
//        WebElement el = driver.findElement(By.cssSelector("#item-0 > span"));
//        el.click();
//        Thread.sleep(1000);
//        driver.findElement(By.cssSelector("#userName")).sendKeys(faker.name().fullName());
//        Thread.sleep(1000);
//        String email = faker.name().username() + faker.number().randomDigit() + "@gmail.com";
//        driver.findElement(By.cssSelector("#userEmail")).sendKeys(email);
//        Thread.sleep(1000);
//        String adress = faker.numerify("1") + "wood Ave";
//        driver.findElement(By.cssSelector("#currentAddress")).sendKeys(adress);
//        Thread.sleep(1000);
//        String permanentAdress=faker.numerify("30")+" wood ave";
//        driver.findElement(By.cssSelector("#permanentAddress")).sendKeys(permanentAdress);
//        Thread.sleep(1000);
//    }