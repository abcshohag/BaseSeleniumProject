package Utils;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DriverUtils {
    private DriverUtils(){}
    public static Properties prop;

    public static WebDriver getChromeDriverIncognito(){
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--incognito");
        DesiredCapabilities d = new DesiredCapabilities();
        d.setCapability(ChromeOptions.CAPABILITY, option);
        return new ChromeDriver(option.merge(d));
    }

    //pass a value between 1 to 100 denoting percentage.
    public static void zoomOutToPercentage(WebDriver driver, double percentage){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("document.body.style.zoom = '" + percentage  + "'");
    }

    public static void scrollToElement(WebDriver driver, WebElement el){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
    }

    public static void scrollToElementAndClick(WebDriver driver, By selector){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(selector));
        driver.findElement(selector).click();
    }

    /*
        Some possible params for downloadThroughput and uploadThroughput are: (slowest to fastest)
                { 23000, 11000 }
                { 30000, 15000 }
                { 40000, 20000 }
                { 50000, 20000 }
                { 75000, 20000 }
                { 100000, 20000 }    */
    public static void setNetworkThrottle(WebDriver driver, int downThroughput, int upThroughput, int additionalLatency) throws IOException {
        if (downThroughput > 0 && upThroughput > 0) {
            CommandExecutor executor = ((ChromeDriver) driver).getCommandExecutor();
            Map map = new HashMap();
            map.put("offline", false);
            map.put("latency", 5 + additionalLatency);

            map.put("download_throughput", downThroughput);
            map.put("upload_throughput", upThroughput);
            Response response = executor.execute(
                    new Command(((ChromeDriver) driver).getSessionId(),
                            "setNetworkConditions",
                            ImmutableMap.of("network_conditions", ImmutableMap.copyOf(map))));
        }
    }


    public static void setTimeout(WebDriver d, int ms){
        //This timeout is used to specify the amount of time the driver
        // should wait while searching for an element if it is not immediately present.
        d.manage().timeouts().implicitlyWait(Duration.ofMillis(ms));

        //This is used to set the amount of time the WebDriver must wait for an asynchronous
        // script to finish execution before throwing an error.
        d.manage().timeouts().scriptTimeout(Duration.ofMillis(ms));

        //This sets the time to wait for a page to load completely before throwing an error.
        // If the timeout is negative, page loads can be indefinite.
        d.manage().timeouts().pageLoadTimeout(Duration.ofMillis(ms));
    }

    public static Boolean isFileExist(String absoluteFilePath){
        File tempFile = new File(absoluteFilePath);
        return tempFile.exists();
    }

    public static Properties initializeProperties(){
        if(prop != null)
            return prop;
        prop = new Properties();
        try {
            FileInputStream ip = new FileInputStream("demoqa.properties");
            prop.load(ip);
        }catch(Exception e){
            System.out.println("Exception occurred during config initialization. " + e.getMessage());
            Reporter.log("Failed to load properties file. Error: " +  e.getMessage());
        }
        return prop;
    }

    public static void clickUsingJS(WebDriver driver, WebElement element){
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public static WebDriver getWebDriver(){
        if(prop == null)
            initializeProperties();
        String browser = prop.getProperty("browser");
        WebDriver driver;
        if(browser == null || browser.equalsIgnoreCase("Chrome")){
            driver = WebDriverManager.chromedriver().create();
        } else if (browser.equalsIgnoreCase("headless")) {
            System.out.println("Setting headless browser");
            var chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--window-size=1280,800");
            chromeOptions.addArguments("--allow-insecure-localhost");
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(chromeOptions);
        }else if (browser.equalsIgnoreCase("safari")) {
            driver = WebDriverManager.safaridriver().create();
        }else if (browser.equalsIgnoreCase("firefox") || browser.equalsIgnoreCase("mozilla")) {
            driver = WebDriverManager.firefoxdriver().create();
        }else{
            driver = WebDriverManager.chromedriver().create();
        }
        return driver;
    }

    public static void waitAndClick(WebDriver driver, By elementSelector, int ms){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ms));
        wait.until(ExpectedConditions.elementToBeClickable(elementSelector));
        DriverUtils.scrollToElementAndClick (driver, elementSelector);
    }

    public static void scrollAndClick(WebDriver driver, String cssSelector){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector(cssSelector)));
        driver.findElement(By.cssSelector(cssSelector)).click();
    }

    //Use this method to click element that are almost impossible to click
    public static void scrollWaitAndClickUsingJs(WebDriver driver, By elementSelector, int ms) {
        //1. Scroll using JS
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(elementSelector));

        //2.Wait for the element to be clickable
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ms));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(elementSelector));

        String currentZoom = (String) ((JavascriptExecutor)driver).executeScript("return document.body.style.zoom;");

        //3. Zoom out to 50%
        DriverUtils.zoomOutToPercentage(driver, .50);

        //4. click using JS
        DriverUtils.clickUsingJS(driver, element);
        //5. Set old Zoom percentage
        if(currentZoom.length() > 0){
            ((JavascriptExecutor)driver).executeScript("document.body.style.zoom=" + currentZoom);
        }
    }
}
