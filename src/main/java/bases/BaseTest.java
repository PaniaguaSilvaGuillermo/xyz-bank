package bases;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;

public class BaseTest {

	public static WebDriverWait wait;
	
	@AfterSuite
	public void tearDown() {
		
		BasePage.quit();
	}
}
