package bases;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import utils.ExcelReader;
import utils.ExtentManager;
import utils.TestUtil;

public class BasePage {

	public static WebDriver driver;
	public static Properties config = new Properties();
	public static Properties OR = new Properties();
	public static FileInputStream fis;
	public static Logger log = Logger.getLogger("devpinoyLogger");
	public static ExcelReader excel = new ExcelReader(
			System.getProperty("user.dir") + "\\src\\main\\resources\\excel-files\\test-data.xlsx");
	public static WebDriverWait wait;
	public ExtentReports rep = ExtentManager.getInstance();
	public static ExtentTest test;
	public static String browser;

	/*
	 * This class initializes the following:
	 * 1. Webdriver
	 * 2. Properties
	 * 3. Logs.
	 * 4. ExtentReports
	 * 5. Excel reader
	 * 
	 */

	public BasePage() {

		if (driver == null) {

			/*
			 * Initializing properties files
			 */

			try {

				fis = new FileInputStream(
						System.getProperty("user.dir") + "\\src\\main\\resources\\properties\\config.properties");
				config.load(fis);
				log.debug("Config file loaded.");

			} catch (FileNotFoundException e1) {

				e1.printStackTrace();

			} catch (IOException e1) {

				e1.printStackTrace();
			}
			try {

				fis = new FileInputStream(
						System.getProperty("user.dir") + "\\src\\main\\resources\\properties\\OR.properties");
				OR.load(fis);
				log.debug("OR file loaded.");

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

			/*
			 * Jenkins browser filter configuration
			 */

			if (System.getenv("browser") != null && !System.getenv("browser").isEmpty()) {

				browser = System.getenv("browser");

			} else {

				browser = config.getProperty("browser");
			}

			config.setProperty("browser", browser);

			/*
			 * Initializing Webdriver
			 */

			if (config.getProperty("browser").equals("firefox")) {

				// This setting is only necessary for the latest version of
				// Gecko Driver.
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + "\\src\\main\\resources\\drivers\\geckodriver.exe");
				driver = new FirefoxDriver();

			} else if (config.getProperty("browser").equals("chrome")) {

				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "\\src\\main\\resources\\drivers\\chromedriver.exe");
				// Chrome driver configurations
				Map<String, Object> prefs = new HashMap<String, Object>();
				prefs.put("profile.default_content_setting_values.notifications", 2);
				prefs.put("credentials_enable_service", false);
				prefs.put("profile_password_manager_enabled", false);
				ChromeOptions options = new ChromeOptions();
				options.setExperimentalOption("prefs", prefs);
				options.addArguments("-disable-extensions");
				options.addArguments("-disable-infobars");
				driver = new ChromeDriver(options);
				log.debug("Chrome launched.");

			} else if (config.getProperty("browser").equals("ie")) {

				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + "\\src\\main\\resources\\drivers\\IEDriverServer.exe");
				driver = new InternetExplorerDriver();

			} else if (config.getProperty("browser").equals("opera")) {

				System.setProperty("webdriver.opera.driver",
						System.getProperty("user.dir") + "\\src\\main\\resources\\drivers\\operadriver.exe");
				driver = new OperaDriver();
			}

			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Integer.parseInt(config.getProperty("implicitWait")),
					TimeUnit.SECONDS);
			driver.get(config.getProperty("siteUrl"));
			log.debug("Navigated to " + config.getProperty("siteUrl") + ".");
			wait = new WebDriverWait(driver, 5);
		}
	}

	/*
	 * Common keywords
	 */

	public boolean isElementPresent(By by) {

		try {

			driver.findElement(by);
			return true;

		} catch (NoSuchElementException e) {

			return false;
		}
	}

	public void click(String locator) {

		if (locator.endsWith("_CSS")) {

			driver.findElement(By.cssSelector(OR.getProperty(locator))).click();
		} else if (locator.endsWith("_XPATH")) {

			driver.findElement(By.xpath(OR.getProperty(locator))).click();

		} else if (locator.endsWith("_ID")) {

			driver.findElement(By.id(OR.getProperty(locator))).click();
		}
		test.log(LogStatus.INFO, "Clicking on " + locator);
	}

	public void type(String locator, String value) {

		if (locator.endsWith("_CSS")) {

			driver.findElement(By.cssSelector(OR.getProperty(locator))).sendKeys(value);
		} else if (locator.endsWith("_XPATH")) {

			driver.findElement(By.xpath(OR.getProperty(locator))).sendKeys(value);

		} else if (locator.endsWith("_ID")) {

			driver.findElement(By.id(OR.getProperty(locator))).sendKeys(value);
		}
		test.log(LogStatus.INFO, "Typing on " + locator + " value: " + value);
	}

	public static void verifyEquals(String expected, String actual) throws IOException {

		/*
		 * This method implements a soft assertion.
		 */

		try {

			Assert.assertEquals(expected, actual);

		} catch (Throwable t) {

			TestUtil.captureScreenshot();
			// ReportNG
			Reporter.log("</br>" + "Verification failure: " + t.getMessage() + "</br>");
			Reporter.log("<a target=\"_blank\" href=" + TestUtil.screenshotName + " height=200 width=200 ></a>");
			Reporter.log("</br>");
			Reporter.log("</br>");
			// Extent Reports
			test.log(LogStatus.FAIL, "Verification failed with exception: " + t.getMessage());
			test.log(LogStatus.FAIL, test.addScreenCapture(TestUtil.screenshotName));
		}
	}

	static WebElement dropdown;

	public void select(String locator, String value) {
		if (locator.endsWith("_CSS")) {

			dropdown = driver.findElement(By.cssSelector(OR.getProperty(locator)));
		} else if (locator.endsWith("_XPATH")) {

			dropdown = driver.findElement(By.xpath(OR.getProperty(locator)));

		} else if (locator.endsWith("_ID")) {

			dropdown = driver.findElement(By.id(OR.getProperty(locator)));
		}
		Select select = new Select(dropdown);
		select.selectByVisibleText(value);
		test.log(LogStatus.INFO, "Selecting from dropdown: " + locator + "; value: " + value);
	}
	
	public void returnHome(){
		
		click("button[ng-click='home()']");
	}
	
	public static void quit() {
		
		driver.quit();
	}
}
