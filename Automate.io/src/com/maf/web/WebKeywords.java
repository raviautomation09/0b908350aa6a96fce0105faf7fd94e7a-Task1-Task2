package com.maf.web;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.maf.core.MainDriver;
import com.maf.main.Executor;
import com.maf.web.GetOSName.OsUtils;

/**
 * =======================================================================
 * <p>
 * 
 * @author Ravindra Kumar
 *         <p>
 *         ==============================================
 *         <p>
 *         Feb 14, 2020
 *         <p>
 *         Description : List of Keywords
 *         <p>
 *         ====================================================================
 *         ===
 */
public class WebKeywords {

	public WebDriver driver;
	public static String FTPFolderLocation = "";
	public static String FolderType = "";
	public Properties CONFIG;
	public static Logger APP_LOGS = Logger.getLogger("devpinoyLogger");
	
	public WebKeywords(WebDriver driver) {
		this.driver = driver;
		this.CONFIG= Executor.CONFIG;
	}
	
	/**
	 * 
	 * {@link Description} : This method is used to launch browser
	 * <p>
	 * <b>BrowserType</b>:~[Can be hard coded or From config or Testdata]
	 * <p>
	 * <b>PossibleValues</b>:IE,Chrome,Mozilla
	 * <p>
	 * 
	 */
	// Browser Action
	public String openBrowser(String BrowserType) throws AWTException {

		// Internet Explorer Path
		if (OsUtils.isWindows()) {
			File file = new File("library/IEDriver/IEDriverServer.exe");
			System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
		}

		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_WINDOWS);
		robot.keyPress(KeyEvent.VK_D);
		robot.keyRelease(KeyEvent.VK_D);
		robot.keyRelease(KeyEvent.VK_WINDOWS);
		System.out.println("Successfully all window minimized....");

		APP_LOGS.debug("Opening browser");
		if (BrowserType.equalsIgnoreCase("Mozilla") || BrowserType.equalsIgnoreCase("firefox")
				|| BrowserType.equalsIgnoreCase("FF")) {
			File file = new File("library/FirefoxDriver/geckodriver.exe");
			System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());
			System.out.println(file.getAbsolutePath());
			if (CONFIG.getProperty("FirefoxCustomProfile").equalsIgnoreCase("true")) {
				FirefoxProfile ffProfile= new FirefoxProfile(new File(CONFIG.getProperty("CustomProfilePath")));
				ffProfile.setAcceptUntrustedCertificates(true);
				ffProfile.setAssumeUntrustedCertificateIssuer(false);
				DesiredCapabilities caps = DesiredCapabilities.firefox();
				caps.setCapability("marionette", false);
				driver = new FirefoxDriver(ffProfile);
			}else {
				/*DesiredCapabilities caps = DesiredCapabilities.firefox();
				caps.setCapability("marionette", false);*/
				driver = new FirefoxDriver();
			}
			
			driver.manage().window().maximize();
			
		} else if (BrowserType.equals("IE") || BrowserType.equalsIgnoreCase("iexplore")) {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			driver = new InternetExplorerDriver(capabilities);
			driver.manage().window().maximize();
		} else if (BrowserType.equals("Chrome")) {
			
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("safebrowsing.enabled", true);
			chromePrefs.put("download.default_directory", System.getProperty("user.dir") + "/downloads");
			chromePrefs.put("download.prompt_for_download", false);
			chromePrefs.put("credentials_enable_service", false);
			chromePrefs.put("profile.password_manager_enabled", false);			
			
			// Chrome Driver Path
			System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")	+ "//library//ChromeDriver/chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			//System.out.println(CONFIG.getProperty("isCEFApplication"));
			if (CONFIG.getProperty("isCEFApplication").toLowerCase().trim().equals("true")) {
				options.setBinary(CONFIG.getProperty("CEFApplicationPath"));
			}			
			options.addArguments("--disable-extensions");
			options.addArguments("disable-infobars");
			options.setExperimentalOption("prefs", chromePrefs);
			DesiredCapabilities caps = DesiredCapabilities.chrome();
			caps.setCapability(ChromeOptions.CAPABILITY, options);
			caps.setCapability (CapabilityType.ACCEPT_SSL_CERTS, true);

			driver = new ChromeDriver(options);
			if (!CONFIG.getProperty("isCEFApplication").toLowerCase().trim().equals("true")) {
				driver.manage().window().maximize();
			}
			
		}

		long implicitWaitTime = Long.parseLong(CONFIG.getProperty("implicitwait"));
		driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
		return Constants.KEYWORD_PASS;

	}

	/**
	 * 
	 * {@link Description} : This method is used to navigate to URL
	 * <p>
	 * <b>URL</b>:~[Can be hard coded or From config or Testdata]
	 * <p>
	 * <b>PossibleValues</b>:http://google.co.in
	 * <p>
	 */
	public String navigate(String URL) {
		APP_LOGS.debug("Navigating to URL");
		try {
			driver.navigate().to(URL);
			Thread.sleep(5000);
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " -- Not able to navigate";
		}
		return Constants.KEYWORD_PASS;
	}

	/**
	 * 
	 * {@link Description} : This method is used to navigate to Back
	 * <p>
	 */
	// << Go back one page
	public String goBack() {
		APP_LOGS.debug("Going back one page");
		try {
			driver.navigate().back();
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ "Unable to go back, Check if its open" + e.getMessage();
		}
		return Constants.KEYWORD_PASS;

	}




	/**
	 * 
	 * {@link Description} : This method is used to close browser without
	 * quitting driver
	 * <p>
	 * <p>
	 */
	public String closeBrowser() {
		APP_LOGS.debug("Closing the browser");
		try {
			
			driver.close();
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + "Unable to close browser. Check if its open" + e.getMessage();
		}
		return Constants.KEYWORD_PASS;

	}

	/**
	 * 
	 * {@link Description} : This method is used to delete all cookies of the
	 * browser
	 * <p>
	 */
	public String deleteAllCookies() {
		APP_LOGS.debug("Deleting all the Browser cookies");
		try {
			driver.manage().deleteAllCookies();
			driver.navigate().refresh();
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL	+ "Unable delete all the cookies from Browser"		+ e.getMessage();
		}
		return Constants.KEYWORD_PASS;

	}

	/**
	 * 
	 * {@link Description} : This method is used to quit web driver object
	 * <p>
	 * <p>
	 */
	public String quitBrowser() {
		APP_LOGS.debug("quiting the driver instance");
		APP_LOGS.info(((RemoteWebDriver)driver).getSessionId());
		try {
			if (((RemoteWebDriver)driver).getSessionId() == null) {
				APP_LOGS.info(((RemoteWebDriver)driver).getSessionId());
				APP_LOGS.info("There is no session to close...");
			}else {
				driver.quit();
			}
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ " Unable to close browser. Check if its open"
					+ e.getMessage();
		}
		return Constants.KEYWORD_PASS;

	}

	

	/**
	 * 
	 * {@link Description} : This method is used to click on element
	 * <p>
	 * <b>Command</b>:~click
	 * <p>
	 * <b>Object</b>:~lnkSomeLink
	 * <p>
	 * <b>Note:</b> provide <b>lnkSomeLink</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 */
	public String click(String object) {
		APP_LOGS.debug("Clicking on any element");
		WebDriverWait wait = new WebDriverWait(driver,
				Integer.parseInt(CONFIG.getProperty("GlobalWait")));
		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(XMLObjectRepository_Web
							.getLocators(MainDriver.ObjectRepoDocument,
									object)));
			driver.findElement(XMLObjectRepository_Web.getLocators(MainDriver.ObjectRepoDocument, object)).click();
			APP_LOGS.debug("Clicking Action Success.");
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " Not able to click:"	+ e.getMessage();
		}
		return Constants.KEYWORD_PASS;
	}

	/**
	 * 
	 * {@link Description} : This method is used to type data on text field
	 * <p>
	 * <b>Command</b>:type
	 * <p>
	 * <b>Object</b>:txtSomeLink
	 * <p>
	 * <b>Note:</b> provide <b>txtSomeLink</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:~[Can be hard coded or From config or variable]
	 *
	 */
	
	public String type(String object, String data) {
		APP_LOGS.debug("Writing in text box:" + object);
		WebDriverWait wait = new WebDriverWait(driver,
				Integer.parseInt(CONFIG.getProperty("GlobalWait")));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(XMLObjectRepository_Web.getLocators(MainDriver.ObjectRepoDocument,object)));
			driver.findElement(XMLObjectRepository_Web.getLocators(MainDriver.ObjectRepoDocument, object)).sendKeys(data);

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " Unable to write " + e.getMessage();

		}
		return Constants.KEYWORD_PASS;

	}

	
	/**
	 * 
	 * {@link Description} : This method is used to clear data from text field
	 * <p>
	 * <b>Command</b>:~clearTextBox
	 * <p>
	 * <b>Object</b>:~txtSomeLink
	 * <p>
	 * <b>Note:</b> provide <b>txtSomeLink</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 */
	public String clearTextBox(String object, String data) {
		APP_LOGS.debug("Clearing input text box:" + object);

		try {
			driver.findElement(
					XMLObjectRepository_Web.getLocators(
							MainDriver.ObjectRepoDocument, object)).clear();

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " Unable to clear input text "
					+ e.getMessage();

		}
		return Constants.KEYWORD_PASS;

	}

	/**
	 * 
	 * {@link Description} : This method is used to select random value in
	 * select box and return the selected value for further use/validation
	 * <p>
	 * <b>Command</b>:selectOptionByRandom
	 * <p>
	 * <b>Object</b>:cboElementName
	 * <p>
	 * <b>KeyName</b>: Selected option text will be stored to global variable map with key passed by user
	 * <p>
	 */
	public String selectOptionByRandom(String object,String keyName, String data) {
		APP_LOGS.debug("Selecting Random form list");
		try {
				System.out.println("Locatorname:" + object);
				Select dropdown = new Select(driver.findElement(XMLObjectRepository_Web.getLocators(MainDriver.ObjectRepoDocument, object)));
				List<WebElement> options = dropdown.getOptions();
				int sizeOptions = options.size();
				int index = new Random().nextInt(sizeOptions);
				if (index == 0) {
					index++;
				}
				dropdown.selectByIndex(index);
				WebElement option = dropdown.getFirstSelectedOption();
				MainDriver.GlobalVariableMap.put(keyName,option.getText());

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ " - Could not select from list by value. :"
					+ e.getMessage();
		}

		return Constants.KEYWORD_PASS;
	}

	/**
	 * 
	 * {@link Description} : This method is used to select value in select box
	 * using VALUE
	 * <p>
	 * <b>Command</b>:~selectOptionByValue
	 * <p>
	 * <b>Object</b>:cboElementName
	 * <p>
	 * <b>Note:</b> provide <b>cboElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:~[Can be hard coded or From config or Testdata or variable]
	 * <p>
	 */
	public String selectOptionByValue(String object, String data) {
		APP_LOGS.debug("Selecting from list");
		try {
			Select dropdown = new Select(driver.findElement(XMLObjectRepository_Web
					.getLocators(MainDriver.ObjectRepoDocument, object)));
			dropdown.selectByValue(data);
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ " - Could not select from list by value. :"
					+ e.getMessage();
		}

		return Constants.KEYWORD_PASS;
	}

	/**
	 * 
	 * {@link Description} : This method is used to select value in select box
	 * using INDEX starting from ZERO
	 * <p>
	 * <b>Command</b>:selectOptionByIndex
	 * <p>
	 * <b>Object</b>:cboElementName
	 * <p>
	 * <b>Note:</b> provide <b>cboElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:[Can be hard coded or From config or Testdata or variable]
	 * <p>
	 */
	public String selectOptionByIndex(String object, String data) {
		APP_LOGS.debug("Selecting from list");
		try {
			Select dropdown = new Select(driver.findElement(XMLObjectRepository_Web
					.getLocators(MainDriver.ObjectRepoDocument, object)));
			dropdown.selectByIndex(Integer.parseInt(data.trim()));
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ " - Could not select from list. by Index:"
					+ e.getMessage();

		}

		return Constants.KEYWORD_PASS;
	}

	/**
	 * 
	 * {@link Description} : This method is used to select value in select box
	 * using LABEL (displayed Text)
	 * <p>
	 * <b>Command</b>:selectOptionByLabel
	 * <p>
	 * <b>Object</b>:cboElementName
	 * <p>
	 * <b>Note:</b> provide <b>cboElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>: label value[Can be hard coded or From config or Testdata or variable]
	 * <p>
	 */
	public String selectOptionByLabel(String object, String data) {
		APP_LOGS.debug("Selecting from list");
		try {
			Select dropdown = new Select(driver.findElement(XMLObjectRepository_Web
					.getLocators(MainDriver.ObjectRepoDocument, object)));
			dropdown.selectByVisibleText(data.trim());
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " - Could not select from list. "
					+ e.getMessage();

		}

		return Constants.KEYWORD_PASS;
	}

	

	/**
	 * 
	 * {@link Description} : This method is used to fetch the TEXT of the
	 * element returned 
	 */
	public String getElementText(String object) {
		
		String actual=null;
		try {
				actual = driver
						.findElement(
								XMLObjectRepository_Web.getLocators(
										MainDriver.ObjectRepoDocument,object)).getText().trim();
				
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " Unable Get Text from element:"
					+ e.getMessage();
		}
		return actual;

	}

	/**
	 * 
	 * {@link Description} : This method is used to verify the element text on
	 * web page
	 * <p>
	 * <b>Command</b>:verifyElementText
	 * <p>
	 * <b>Object</b>:spnElementName
	 * <p>
	 * <b>Note:</b> provide <b>spnElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:text to verify
	 * <p>
	 */
	public String verifyElementText(String object, String data) {
		APP_LOGS.debug("Verifying link Text");
		String actual = null;
		try {
			actual = driver
					.findElement(
							XMLObjectRepository_Web.getLocators(
									MainDriver.ObjectRepoDocument, object))
					.getText().trim().toLowerCase();
			String expected = data.toLowerCase();
			APP_LOGS.debug("Actual:" + actual + " Expected:" + expected);
			if (actual.contains(expected))
				return Constants.KEYWORD_PASS;
			else
				return Constants.KEYWORD_FAIL
						+ " -- Link text not verified --->" + "Actual:"
						+ actual + " Expected:" + expected;

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " -- Link text not verified"
					+ e.getMessage();

		}

	}

	
	/**
	 * 
	 * {@link Description} : This method is used to verify text on page
	 * <p>
	 * <b>Command</b>:verifyTextOnPage
	 * <p>
	 * <b>Data</b>:[Can be hard coded  or variable]
	 * PossibleValues:Text to verify
	 * <p>
	 * 
	 */
	public String verifyTextOnPage(String data) {
		APP_LOGS.debug("Verifying the text");
		try {
			String actual = driver.getPageSource();
			String expected = data;

			if (actual.contains(expected))
				return Constants.KEYWORD_PASS;
			else
				return Constants.KEYWORD_FAIL
						+ " -- text not available on the page " + " -- "
						+ expected;
		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " Object not found "
					+ e.getMessage();
		}

	}

	/**
	 * 
	 * {@link Description} : This method is used to verify element is present on the 
	 * page or not by providing true or false
	 * <p>
	 * <b>Command</b>:verifyElementPresent
	 * <p>
	 * <b>Object</b>:txtElementName
	 * <p>
	 * <b>Note:</b> provide <b>txtElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:[Can be hard coded or variable]
	 * PossibleValues:true/false
	 * <p>
	 */
	public String verifyElementPresent(String object, String data) {
		APP_LOGS.debug("Checking existance of element");
		boolean actual = false;
		try {

			if (data.trim().toLowerCase().equals("true")) {
				try {
					actual = driver.findElement(
							XMLObjectRepository_Web.getLocators(
									MainDriver.ObjectRepoDocument, object))
							.isDisplayed();
					APP_LOGS.debug("existance element is:" + actual);
				} catch (NoSuchElementException e) {
					APP_LOGS.debug("existance element is false:");
					actual = false;
				}

				if (actual) {
					return Constants.KEYWORD_PASS;
				} else {
					return Constants.KEYWORD_FAIL + ": " + object
							+ " Element is not present on the page Actual:"
							+ actual + " Expected:" + data;
				}
			} else if (data.trim().toLowerCase().equals("false")) {
				try {
					actual = driver.findElement(
							XMLObjectRepository_Web.getLocators(
									MainDriver.ObjectRepoDocument, object))
							.isDisplayed();
					APP_LOGS.debug("existance element is:" + actual);
				} catch (NoSuchElementException e) {
					APP_LOGS.debug("existance element is false");
					actual = false;
				}

				if (!actual) {
					return Constants.KEYWORD_PASS;
				} else {
					return Constants.KEYWORD_FAIL
							+ " Element is present on the page Actual:"
							+ actual + " Expected:" + data;
				}
			} else {
				return Constants.KEYWORD_FAIL
						+ " Specify the proper data true or false for verifyElementPresent";
			}

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + " Object doest not exist:"
					+ e.getMessage();
		}
	}

	
	/**
	 * 
	 * {@link Description} : This method is used to wait for an element for time
	 * supplied
	 * <p>
	 * <b>Command</b>:waitForElement
	 * <p>
	 * <b>Object</b>:txtElementName
	 * <p>
	 * <b>Note:</b> provide <b>txtElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:[Can be hard coded or From Testdata or variable]
	 * PossibleValues:60 ( in secs) [Optional] is not provided, then it will
	 * take the global wait from config file
	 * <p>
	 */
	public String waitForElement(String object, String data) {
		boolean elementWaitFlag = false;
		int time = 0;
		int counter = 0;
		int ElementCount = 0;		
		
		// System.out.println(start +"-"+time);
		try {

			if (!StringUtils.isEmpty(data) || !data.equals("") || !data.equals(null)) {
				time = (int) Double.parseDouble(data);
			} else {
				APP_LOGS.debug("Taking default time as wait time "
						+ CONFIG.getProperty("GlobalWait")
						+ " Secs, as user not provided element wait time..");
				time = Integer.parseInt(CONFIG.getProperty("GlobalWait"));
			}

			APP_LOGS.debug("Waiting for an element to be visible for " + time
					+ "Secs..");

			while (counter < time) {
				try {
					WebDriverWait wait = new WebDriverWait(driver, 10);
					WebElement element = wait.until(ExpectedConditions
							.visibilityOfElementLocated(XMLObjectRepository_Web
									.getLocators(
											MainDriver.ObjectRepoDocument,
											object)));
					if (element.isDisplayed()) {
						APP_LOGS.info("Element is visible..");
						elementWaitFlag = true;
						break;
					}
				} catch (Exception e) {
					APP_LOGS.info("Element is not visible..");
				}

				counter = counter + 10;
			}

			if (!elementWaitFlag) {
				throw new Exception("Element wait failed.");
			}

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL + ":	Unable to wait for element:"
					+ e.getMessage();
		}
		return Constants.KEYWORD_PASS;
	}

	
	/**
	 * 
	 * {@link Description} : This method is used to wait for text on page
	 * <p>
	 * <b>Command</b>:waitForTextOnPage
	 * <p>
	 * <b>waitTime</b>:60 (60 is time in seconds which is optional)
	 * <p>
	 * <b>Data</b>:[Can be hard coded or From Testdata or variable]
	 * PossibleValues:text to wait
	 * <p>
	 */

	public String waitForTextOnPage(String waitTime, String data) {
		try {
			int start = 0;
			int time = 0;
			if (!StringUtils.isEmpty(waitTime) || !waitTime.equals("") || !waitTime.equals(null)) {
				time = (int) Double.parseDouble(waitTime);
			} else {
				APP_LOGS.debug("Taking default time as wait time "
						+ CONFIG.getProperty("GlobalWait")
						+ " Secs, as user not provided element wait time..");
				time = Integer.parseInt(CONFIG
						.getProperty("GlobalWait"));
			}

			APP_LOGS.debug("Waiting an text to be visible for " + time
					+ "Sec on page..");
			// System.out.println(start +"-"+time);

			while (start <= time) {
				String actual = driver.getPageSource();
				if (actual.contains(data.trim())) {
					APP_LOGS.debug("Text is present on page:" + data);
					return Constants.KEYWORD_PASS;
				}
				Thread.sleep(1000);
				start++;
				;
			}

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ ": Unable to wait for text on page:" + e.getMessage();
		}
		return Constants.KEYWORD_PASS;

	}

	

	/**
	 * 
	 * {@link Description} : This method is used to wait for specific seconds
	 * <p>
	 * <b>Command</b>:pause
	 * <p>
	 * <b>Object</b>:10 (in secs)
	 * <p>
	 */
	public String pause(String object)
			throws NumberFormatException, InterruptedException {
		long time = (long) Double.parseDouble(object);
		Thread.sleep(time * 1000L);
		return Constants.KEYWORD_PASS;
	}

	/**
	 * 
	 * {@link Description} : This method is used to simulate the keyboard actions
	 * <p>
	 * <b>Command</b>:KeyPress
	 * <p>
	 * <b>Object</b>:cboElementName/novalue [optional- if required keypress on
	 * element]
	 * <p>
	 * <b>Note:</b> provide <b>cboElementName</b> in object repository with
	 * respective element type xpath,name,id,css,className, linktext
	 * <p>
	 * <b>Data</b>:Any string / for special key like TAB, Home, enter, Backspace, Escape,delete,end EX:{TAB} key within flower braces
	 * <p>
	 */
	public String KeyPress(String object, String data) {
		try {
			System.out.println("Performing KeyPress....");
			// before opening the new window
			// driver.switchTo().defaultContent();
			// ((JavascriptExecutor) driver).executeScript("window.focus();");
			String parentWindow = driver.getWindowHandle();
			System.out.println(parentWindow);
			// after the new window was closed
			driver.switchTo().window(parentWindow);
			Thread.sleep(2000);
			if (!StringUtils.isEmpty(object)) {
				APP_LOGS.info("Getting focus on element by clicking for key press...");
				driver.findElement(
						XMLObjectRepository_Web.getLocators(
								MainDriver.ObjectRepoDocument, object))
						.click();
				Thread.sleep(1000);
			}

			Robot rb = new Robot();
			if (data.startsWith("{") && data.endsWith("}")) {
				APP_LOGS.info("Performing KeyPress:" + data);
				if (data.toLowerCase().contains("enter")) {
					rb.keyPress(KeyEvent.VK_ENTER);
					rb.keyRelease(KeyEvent.VK_ENTER);
				} else if (data.toLowerCase().contains("tab")) {
					rb.keyPress(KeyEvent.VK_TAB);
					rb.keyRelease(KeyEvent.VK_TAB);
				} else if (data.toLowerCase().contains("home")) {
					rb.keyPress(KeyEvent.VK_HOME);
					rb.keyRelease(KeyEvent.VK_HOME);
				} else if (data.toLowerCase().contains("end")) {
					rb.keyPress(KeyEvent.VK_END);
					rb.keyRelease(KeyEvent.VK_END);
				} else if (data.toLowerCase().contains("escape")) {
					rb.keyPress(KeyEvent.VK_ESCAPE);
					rb.keyRelease(KeyEvent.VK_ESCAPE);
				} else if (data.toLowerCase().contains("delete")) {
					rb.keyPress(KeyEvent.VK_DELETE);
					rb.keyRelease(KeyEvent.VK_DELETE);
				} else if (data.toLowerCase().contains("backspace")) {
					rb.keyPress(KeyEvent.VK_BACK_SPACE);
					rb.keyRelease(KeyEvent.VK_BACK_SPACE);
				}

			} else {
				KeyboardActions kb = new KeyboardActions();
				APP_LOGS.info("Performing KeyPress:" + data);
				kb.type(data);
			}

		} catch (Exception e) {
			APP_LOGS.error(e);
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return Constants.KEYWORD_FAIL
					+ "Unable to KeyPress. Check Stack trace:" + e.getMessage();
		}

		return Constants.KEYWORD_PASS;
	}

	

	/**
	 * 
	 * {@link Description} : This method is used to generate Dynamic locator
	 * <p>
	 * <b>Command</b>:generateDynamicLocator
	 * <p>
	 * <b>Object</b>:txtElementName <b>Note:</b> The name should not be duplicate, pls use unique names
	 * <p>
	 * <b>Locatortype</b>:xpath, css, id, name ..etc
	 * <p>
	 * <b>LocatorValue</b>://input[@id='asasasa']
	 * <p>
	 * <b>Example</b>~getRandomNumericString ~txtElementName
	 * ~xpath^true^//input[@id='#VariableName#]
	 * <p>
	 * <b>Note:</b> If GlobalVariablePresence == true there should be variable
	 * in xpath value else you can give false if variable not there
	 * <p>
	 * <b>Example</b>~getRandomNumericString ~txtElementName
	 * ~xpath^false^//input[@id='asasasa']
	 * <p>
	 */

	public String generateDynamicLocator(String object, String Locatortype, String LocatorValue) {
		try {

			APP_LOGS.debug("Executing Generate dynamic object..");
			APP_LOGS.debug("Locator type:" + Locatortype);
			APP_LOGS.debug("Locator value:" + LocatorValue);


			// lETS VERIFY IF LOCATORS ALREADY EXISTS
			XPathFactory xfactory = XPathFactory.newInstance();
			XPath Vxpath = xfactory.newXPath();
			XPathExpression expr = Vxpath.compile("//Element[@name='" + object
					+ "']");
			Object result = expr.evaluate(MainDriver.ObjectRepoDocument,
					XPathConstants.NODE);

			if (result != null) {
				APP_LOGS.debug("locator already present in Object repository:"
						+ result);
				APP_LOGS.debug("Lets replace the existing Locator with new value....");
				// locate the node(s)
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nodes = (NodeList) xpath
						.evaluate("//Element[@name='" + object + "']/"	+ Locatortype, MainDriver.ObjectRepoDocument,
								XPathConstants.NODESET);

				// make the change
				for (int idx = 0; idx < nodes.getLength(); idx++) {
					nodes.item(idx).setTextContent(LocatorValue);
				}

			} else {
				APP_LOGS.debug("locator is not present in Object repository:"
						+ result);
				APP_LOGS.debug("Lets Create a new Locator with new value....");

				// Create Element and attribute as name
				Element elementMain = MainDriver.ObjectRepoDocument
						.createElement("Element");
				elementMain.setAttribute("name", object);
				// NL.appendChild(dc.createTextNode("xpath"));

				// create child elememt (xpath, css, id, link text etc
				Element locatorType = MainDriver.ObjectRepoDocument
						.createElement(Locatortype);
				locatorType.appendChild(MainDriver.ObjectRepoDocument
						.createTextNode(LocatorValue));
				elementMain.appendChild(locatorType);

				// append the create node list to main document
				MainDriver.ObjectRepoDocument
						.getElementsByTagName("FeatureSet").item(0)
						.appendChild(elementMain);
			}

		} catch (Exception e) {
			APP_LOGS.error(e);
			return Constants.KEYWORD_FAIL
					+ " Unable Create dynamic element in Object repository:"
					+ e.getMessage();
		}
		return Constants.KEYWORD_PASS;

	}

}
