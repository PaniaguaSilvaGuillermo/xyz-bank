package bases;

import org.testng.annotations.AfterSuite;

public class BaseTest {

	@AfterSuite
	public void tearDown() {
		
		BasePage.quit();
	}
}
