package listeners;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.relevantcodes.extentreports.LogStatus;

import bases.BasePage;
import utils.MonitoringMail;
import utils.TestConfig;
import utils.TestUtil;

public class CustomListeners extends BasePage implements ITestListener, ISuiteListener{

	public void onFinish(ITestContext arg0) {
		
	}

	public void onStart(ITestContext arg0) {
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		
	}

	public void onTestFailure(ITestResult arg0) {
		
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			
			TestUtil.captureScreenshot();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		test.log(LogStatus.FAIL, arg0.getName().toUpperCase() + " - FAILED with exception: " +arg0.getThrowable());
		test.log(LogStatus.FAIL, test.addScreenCapture(TestUtil.screenshotName));
		Reporter.log("Click to see screenshot.");
		Reporter.log("<a target=\"_blank\" href=" +TestUtil.screenshotName+ ">Screenshot</a>");
		Reporter.log("</br>");
		Reporter.log("</br>");
		Reporter.log("<a target=\"_blank\" href=" +TestUtil.screenshotName+ " height=200 width=200 ></a>");
		rep.endTest(test);
		rep.flush();
	}

	public void onTestSkipped(ITestResult arg0) {
		
		test.log(LogStatus.SKIP, arg0.getName().toUpperCase() + " - Skipped test as run mode is NO.");
		rep.endTest(test);
		rep.flush();
	}

	public void onTestStart(ITestResult arg0) {
		
		test = rep.startTest(arg0.getName().toUpperCase());
	}

	public void onTestSuccess(ITestResult arg0) {
		
		test.log(LogStatus.PASS, arg0.getName().toUpperCase() + " - PASS");
		rep.endTest(test);
		rep.flush();
	}

	public void onFinish(ISuite arg0){
		
		MonitoringMail mail = new MonitoringMail();
		String messageBody = null;
		try {
			
			messageBody = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/job/Page%20Object%20Model/Extent_Report/";
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}
		try {
			
			mail.sendMail(TestConfig.server, TestConfig.from, TestConfig.to, TestConfig.subject, messageBody);
			
		} catch (AddressException e) {
			
			e.printStackTrace();
			
		} catch (MessagingException e) {
			
			e.printStackTrace();
		}
	}

	public void onStart(ISuite arg0) {
		
	}

}
