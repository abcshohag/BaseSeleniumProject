import Utils.BaseMethods;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SampleTest extends BaseMethods {
    WebDriver driver;

    @BeforeClass
    void setup(){
        driver = getWebDriver();
    }

    @Test
    void test1(){
        driver.get("http://www.google.com");
        Assert.assertTrue( driver.getCurrentUrl().contains("google.com") );
    }

    @AfterClass
    void wrapUp(){
        quitWebdriver();
    }
}
