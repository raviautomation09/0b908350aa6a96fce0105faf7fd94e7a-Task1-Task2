package com.maf.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.w3c.dom.Document;

import com.maf.main.Executor;
import com.maf.web.GetOSName.OsUtils;
import com.rest.services.RestClientAPI;
import com.rest.services.RestClientAPI.RequestMethod;


public class MainDriver extends FileAppender {
	
	private volatile AppiumDriver<?> driver;
	public static Logger APP_LOGS;
	public static Document ObjectRepoDocument;
	private String strWkgDir = System.getProperty("user.dir");
	public static HashMap<String, String> GlobalVariableMap;
	public static Properties CONFIG;
	public static ConcurrentHashMap<String, AppiumDriver<?>> testNameSessionID;
	public static ConcurrentHashMap<String, WebDriver> testNameSessionIDWeb;
	
	
	
	@BeforeSuite
	public void initSuiteExecution(){		
		String strSuiteConfigPath = strWkgDir	+ File.separator+"Configuration"+File.separator+"config.properties";		
		
		try{			
			CONFIG= Executor.CONFIG;
			
			// Logs Initialization
			PropertyConfigurator.configure(strWkgDir + File.separator+"log4j.properties");
			// PropertyConfigurator.configure("log4j.properties");
			APP_LOGS = Logger.getLogger("devpinoyLogger");
			
			// Object Repository Initialization
			APP_LOGS.info("Creating object repository object....");
			File in = new File(strWkgDir+ File.separator+"ObjectRepository"+File.separator+ CONFIG.getProperty("objectRepository"));
			InputStream is = new FileInputStream(in);
			String ObjRepoObj = IOUtils.toString(is);
			// System.out.println(ObjRepoObj);
			ObjectRepoDocument = XMLObjectRepository.loadXMLFromString(ObjRepoObj);			
			GlobalVariableMap = new HashMap<String, String>();
			testNameSessionID =  new ConcurrentHashMap<String, AppiumDriver<?>>();
			testNameSessionIDWeb =  new ConcurrentHashMap<String, WebDriver>();
			
			
		}catch(Exception e){
			Log.logger.fatal("Exception to load " + strSuiteConfigPath);
			e.printStackTrace();
		}		
	}
	
	public AppiumDriver<?> getDriver() {
        return driver;
	}
	
	public AppiumDriver<?> setDriver(String platform, String TestName){
		
		try{
			//Initialize Driver
			switch (platform.trim().toUpperCase()){
			case "ANDROID":
				driver = initAndroidDriver(TestName);
				break;
				
			case "IOS":			
				driver = initIOSDriver();
				break;
								
			default:
				Log.logger.error("Invalid platform ["+ platform +"] to test. check config file!!");
			}
			
			Log.logger.info("Loading application..");
			
		}catch(Exception e){
			Log.logger.fatal("Exception to launch " + platform + " platform " + e.getMessage());
			e.printStackTrace();
		}		
		return driver;
	}
	
	public WebDriver setWebDriver(String TestName, String BrowserType, String URL){
		WebDriver driver = null;
			try{
				
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
				driver.navigate().to(URL);
				Thread.sleep(5000);
				
				testNameSessionIDWeb.put(TestName,driver);
				
			}catch(Exception e){
				Log.logger.fatal("Exception to launch " + BrowserType + " platform " + e.getMessage());
				e.printStackTrace();
			}
			
			
			return driver;
		}
	
	private AppiumDriver<?> initAndroidDriver(String TestName){		
		DesiredCapabilities capabilities = new DesiredCapabilities();
	        
	    try {
	    	if (CONFIG.getProperty("AppType").toLowerCase().trim().equals("native")) {
	    		
	    		String URL = getSessionFreeURL();
	    		String UDID = Executor.AppiumURLS.get(URL);
	    		
	    		capabilities.setCapability("device", "Android");
	    	    //capabilities.setCapability("deviceName", "ZY223NXJ4S");
	    	    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, UDID);
	    	    capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
	    	    capabilities.setCapability(CapabilityType.VERSION, CONFIG.getProperty("AndroidVersion"));
	    	    capabilities.setCapability(CapabilityType.PLATFORM, CONFIG.getProperty("Platform"));
	    	    //capabilities.setCapability("app", strWkgDir+File.separator+"App"+File.separator+CONFIG.getProperty("ApkFileName"));
	    	    System.out.println("UDID is:"+UDID);
	    	    capabilities.setCapability("app", Uitils.fetchTestDataUsingDeviceID(UDID, "AppPath"));
	    	    capabilities.setCapability("appPackage", CONFIG.getProperty("AppPackage"));
	    	    capabilities.setCapability("appActivity", CONFIG.getProperty("AppActivity"));
	    	    capabilities.setCapability("noReset", Boolean.parseBoolean(CONFIG.getProperty("NoReset")));
	    	    capabilities.setCapability("fullReset" ,Boolean.parseBoolean(CONFIG.getProperty("FullReset")));
	    	    capabilities.setCapability("udid" ,UDID);
	    	    capabilities.setCapability("--session-override",true);
	    	    capabilities.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, "true");
	    		driver = new AndroidDriver<MobileElement>(new URL(URL),capabilities);   		
	    		
	    		//Reporter.getCurrentTestResult().setAttribute(TestName,driver.getSessionId());
	    		testNameSessionID.put(TestName,driver);
	    		
			} else if(CONFIG.getProperty("AppType").toLowerCase().trim().equals("web")) {
				capabilities.setCapability("device", "Android");
	    	    //capabilities.setCapability("deviceName", "ZY223NXJ4S");
	    	    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, CONFIG.getProperty("DeviceName"));
	    	    capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
	    	    capabilities.setCapability(CapabilityType.VERSION, CONFIG.getProperty("AndroidVersion"));
	    	    capabilities.setCapability(CapabilityType.PLATFORM, CONFIG.getProperty("Platform"));
	    	    capabilities.setCapability("app", strWkgDir+File.separator+"App"+File.separator+CONFIG.getProperty("ApkFileName"));
	    	    capabilities.setCapability("app-package", CONFIG.getProperty("AppPackage"));
	    	    capabilities.setCapability("app-activity", CONFIG.getProperty("AppActivity"));
	    	    capabilities.setCapability("noReset", Boolean.parseBoolean(CONFIG.getProperty("NoReset")));
	    	    capabilities.setCapability("fullReset" ,Boolean.parseBoolean(CONFIG.getProperty("FullReset")));
	    	    capabilities.setCapability(MobileCapabilityType.TAKES_SCREENSHOT, "true");
				driver = new AndroidDriver<WebElement>(new URL(CONFIG.getProperty("AppiumServerURL")),capabilities);
			}
			
			Thread.sleep(5000);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return driver;
	}
	
	
	private AppiumDriver<?> initIOSDriver(){		
		DesiredCapabilities capabilities = new DesiredCapabilities();
	    capabilities.setCapability("device", "IOS");
	    /*capabilities.setCapability("deviceName", "F73MT53BFYWF");*/
	    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhoneqM");
	    capabilities.setCapability("deviceid", "F73MT575FYW8");
	    
	    capabilities.setCapability("udid", "72400e3af03e3c29910ee72c8e3c69547558c470");
	    capabilities.setCapability("bundleId", "com.honeywell.Stockwell");
	    capabilities.setCapability("xcodeOrgId", "9J4G8BZA27");
	    capabilities.setCapability("xcodeSigningId", "iPhone Developer");
	    capabilities.setCapability("showXcodeLog", true);
	    capabilities.setCapability("useNewWDA", true);
	    
	    capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
	    capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.3");   
	    
	    
	   /* capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
	    capabilities.setCapability(CapabilityType.VERSION, "4.4");*/
	    //capabilities.setCapability(CapabilityType.PLATFORM, "WINDOWS");
	    capabilities.setCapability("app", "/Users/raghupanithi/Downloads/StockWelliOS-NT.ipa");
	    /*capabilities.setCapability("app-package","com.ducatimobile");*/
	    /*capabilities.setCapability("app-activity", ".MainActivity");*/
	    
	    capabilities.setCapability("noReset", true);
	    capabilities.setCapability("fullReset" ,false);
	    try {
			driver = new IOSDriver<MobileElement>(new URL("http://10.77.67.22:4723/wd/hub"),capabilities);
			Thread.sleep(5000);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return driver;
	}	
	
	
	
	public String getSessionFreeURL(){
		
		try {
			APP_LOGS.info(Executor.AppiumURLS);			

			Set<String> urlList = Executor.AppiumURLS.keySet();
			while (true) {
				
				for(String url : Executor.AppiumURLQueue){
					url = Executor.AppiumURLQueue.remove();
					
					APP_LOGS.info("Checkin Appium URL:"+url);
					RestClientAPI client = new RestClientAPI(url+"/sessions");
					try {
					    client.Execute(RequestMethod.GET,"{}");
					} catch (Exception e) {
					    e.printStackTrace();
					}
					String response = client.getResponse();
					APP_LOGS.info(response);
					if (response.contains("\"value\":[]")) {
						APP_LOGS.info("Free Appium Session is:"+url);
						return url;
					}else{
						APP_LOGS.info("Appium session is not free:"+url);						
								Executor.AppiumURLQueue.add(url);				
						
					}
					
					Thread.sleep(10000);
				}
				
				
				/*for (String url : urlList) {
					APP_LOGS.info("Checkin Appium URL:"+url);
					RestClientAPI client = new RestClientAPI(url+"/sessions");
					try {
					    client.Execute(RequestMethod.GET);
					} catch (Exception e) {
					    e.printStackTrace();
					}
					String response = client.getResponse();
					APP_LOGS.info(response);
					if (response.contains("\"value\":[]")) {
						APP_LOGS.info("Free Appium Session is:"+url);
						return url;
					}else{
						APP_LOGS.info("Appium session is not free:"+url);
					}
				}*/
				
				Thread.sleep(5000);
				System.out.println("No More session is free. Waiting One or more session to get free or reduce Thread count");
				
			}
			
		} catch (Exception e) {
			APP_LOGS.error(e);
		}
		return null;
		
	}
	
	@AfterMethod()
	public void closeSuite(ITestResult result){
		
		try{
			
			/*Object currentClass = result.getInstance();
		      AppiumDriver<?> driverTemp = ((MainDriver) currentClass).getDriver();
			if (driverTemp == null) {
				APP_LOGS.info("There is no session to close...");
			}else {
				APP_LOGS.info("Closing the driver..."+driverTemp);
				driverTemp.quit();
			}*/
			if (CONFIG.getProperty("isAppium").equalsIgnoreCase("true")) {
				Thread.sleep(2000);
				Set<String> urlList = Executor.AppiumURLS.keySet();
				for (String url : urlList) {				
					Executor.AppiumURLQueue.add(url);
				}
			}
			
			if (CONFIG.getProperty("WebTestRecording").equalsIgnoreCase("true")) {
				if (Executor.recorder != null) {
					Executor.recorder.stop();
					APP_LOGS.info("Successfully closed the recording");
				}
				
			}
			
		}catch(Exception e){
			Log.logger.fatal("Exception to close suite");
			e.printStackTrace();
		}
	}
	
}
