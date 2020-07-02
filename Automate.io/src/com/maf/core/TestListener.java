package com.maf.core;

import io.appium.java_client.AppiumDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class TestListener implements ITestListener, EventListener {
	public static int counter = 0;
	static Logger logger = Logger.getLogger("devpinoyLogger");

	String filePath = System.getProperty("user.dir") + File.separator
			+ "ScreenShot" + File.separator;

	@Override
	synchronized public void onTestFailure(ITestResult result) {
		logger.error("***** Error " + result.getName()
				+ " test has failed *****");
		// AppiumDriver<?> driver = ((MainDriver) currentClass).getDriver();
		if (MainDriver.CONFIG.getProperty("isSelenium")
				.equalsIgnoreCase("true")) {
			WebDriver driver = MainDriver.testNameSessionIDWeb.get(result
					.getName());

			String fileName = filePath + "_"
					+ Uitils.getCurrentTimeStamp("DDmmYYYYMMss") + ".png";

			String folder_name = "screenshot";
			logger.error("Closing Driver Object:" + driver);
			if (driver != null && ((RemoteWebDriver)driver).getSessionId() != null) {
				try {
					File f = ((TakesScreenshot) driver)
							.getScreenshotAs(OutputType.FILE);
					// Date format fot screenshot file name
					SimpleDateFormat df = new SimpleDateFormat(
							"dd-MMM-yyyy__hh_mm_ssaa");
					// create dir with given folder name
					new File(folder_name).mkdir();
					// Setting file name
					// String file_name=df.format(new Date())+".png";
					// coppy screenshot file into screenshot folder.

					FileUtils.copyFile(f, new File(fileName));
					logger.error("Screen shot taken:" + fileName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				/*
				 * try { fileName = getScreenshot(new
				 * MainDriver().getDriver(),filePath);
				 * logger.info("ScreenShot taken. Location:"+fileName); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
				final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
				System.setProperty(ESCAPE_PROPERTY, "false");
				Reporter.log("FAILED:" + result.getName());
				Reporter.log("<br> <a href=\"" + fileName
						+ "\"><img src=\"file:///" + fileName + "\" alt=\"\""
						+ "height='100' width='100'/> " + "</a> <br/>");
				Reporter.log("******************************************************");

				logger.error("Closing the driver..." + driver);
				driver.quit();

			} else {
				Reporter.log("FAILED:" + result.getName());
				Reporter.log("Driver Object is NULL cannot take screen shot:"
						+ driver);
			}
		} else if (MainDriver.CONFIG.getProperty("isAppium").equalsIgnoreCase(
				"true")) {
			AppiumDriver<?> driver = MainDriver.testNameSessionID.get(result
					.getName());

			String fileName = filePath + "_"
					+ Uitils.getCurrentTimeStamp("DDmmYYYYMMss") + ".png";

			String folder_name = "screenshot";
			logger.error("Closing Driver Object:" + driver);
			if (driver != null) {
				try {
					File f = ((TakesScreenshot) driver)
							.getScreenshotAs(OutputType.FILE);
					// Date format fot screenshot file name
					SimpleDateFormat df = new SimpleDateFormat(
							"dd-MMM-yyyy__hh_mm_ssaa");
					// create dir with given folder name
					new File(folder_name).mkdir();
					// Setting file name
					// String file_name=df.format(new Date())+".png";
					// coppy screenshot file into screenshot folder.

					FileUtils.copyFile(f, new File(fileName));
					logger.error("Screen shot taken:" + fileName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				/*
				 * try { fileName = getScreenshot(new
				 * MainDriver().getDriver(),filePath);
				 * logger.info("ScreenShot taken. Location:"+fileName); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
				final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
				System.setProperty(ESCAPE_PROPERTY, "false");
				Reporter.log("FAILED:" + result.getName());
				Reporter.log("<br> <a href=\"" + fileName
						+ "\"><img src=\"file:///" + fileName + "\" alt=\"\""
						+ "height='100' width='100'/> " + "</a><br/>");
				Reporter.log("******************************************************");

				logger.error("Closing the driver..." + driver);
				driver.quit();

			} else {
				Reporter.log("FAILED:" + result.getName());
				Reporter.log("Driver Object is NULL cannot take screen shot:"
						+ driver);
			}
		} else {

			// To Do API

		}

	}

	public static String getScreenshot(AppiumDriver<?> driver,
			String outputlocation) throws IOException {
		System.out.println("Capturing the snapshot of the page ");
		File srcFiler = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(srcFiler, new File(outputlocation));

		return srcFiler.getAbsolutePath();
	}

	public void onTestSuccess(ITestResult result) {
		Reporter.log("************** " + getTestMethodName(result)
				+ " ENDS **************");

	}

	public void onTestSkipped(ITestResult result) {
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	@Override
	public void onStart(ITestContext context) {
		Reporter.log("TestName:" + context.getName() + "<br>");
		Log.startTest(context.getName());
	}

	@Override
	public void handleEvent(Event evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestStart(ITestResult result) {
		Log.startTest(getTestMethodName(result));
		// Reporter.log("Test Method Name:"+result.getMethod()+"<br>");
		// logger.info("****************** Starting the test ["+result.getName()+"] ************************");

	}

	@Override
	public void onFinish(ITestContext context) {
		logger.info("****************** End of the Suite [" + context.getName()
				+ "] ************************");

	}

	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}

}
