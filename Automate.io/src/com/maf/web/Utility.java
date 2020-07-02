package com.maf.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * =======================================================================
 * 
 * @Author Ravindra Kumar - H124795
 * ==============================================
 * October 05, 2015 
 * Description : Generic Utility class
 * =======================================================================
 */


public class Utility {
	
	// To generate the formated date Input eg; yyyy_MM_dd_HH_mm_ss
	public static String getCurrentFormattedDate(String DateFormat){
		Date date= new Date();
		SimpleDateFormat format= new SimpleDateFormat(DateFormat);
		String FinalDate=format.format(date);		
		return FinalDate;
	}
	
	public static int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	
	public static double randomWithRangeDecimal(double min, double max) {
	    Random r = new Random();
	    return (r.nextInt((int)((max-min)*10+1))+min*10) / 10.0;
	}
	
	public static String getRandomDecimalValue(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces){

		    if(lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0){
		        throw new IllegalArgumentException("Put error message here");
		    }

		    final double dbl =
		        ((random == null ? new Random() : random).nextDouble() //
		            * (upperBound - lowerBound))
		            + lowerBound;
		    return String.format("%." + decimalPlaces + "f", dbl);

	}
	
	public static void DropFile(File filePath, WebElement target, int offsetX, int offsetY) {
	    if(!filePath.exists())
	        throw new WebDriverException("File not found: " + filePath.toString());

	    WebDriver driver = ((RemoteWebElement) target).getWrappedDriver();
	    JavascriptExecutor jse = (JavascriptExecutor)driver;
	    WebDriverWait wait = new WebDriverWait(driver, 30);

	    String JS_DROP_FILE =
	        "var target = arguments[0]," +
	        "    offsetX = arguments[1]," +
	        "    offsetY = arguments[2]," +
	        "    document = target.ownerDocument || document," +
	        "    window = document.defaultView || window;" +
	        "" +
	        "var input = document.createElement('INPUT');" +
	        "input.type = 'file';" +
	        "input.style.display = 'none';" +
	        "input.onchange = function () {" +
	        "  var rect = target.getBoundingClientRect()," +
	        "      x = rect.left + (offsetX || (rect.width >> 1))," +
	        "      y = rect.top + (offsetY || (rect.height >> 1))," +
	        "      dataTransfer = { files: this.files };" +
	        "" +
	        "  ['dragenter', 'dragover', 'drop'].forEach(function (name) {" +
	        "    var evt = document.createEvent('MouseEvent');" +
	        "    evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);" +
	        "    evt.dataTransfer = dataTransfer;" +
	        "    target.dispatchEvent(evt);" +
	        "  });" +
	        "" +
	        "  setTimeout(function () { document.body.removeChild(input); }, 25);" +
	        "};" +
	        "document.body.appendChild(input);" +
	        "return input;";

	    WebElement input =  (WebElement)jse.executeScript(JS_DROP_FILE, target, offsetX, offsetY);
	    input.sendKeys(filePath.getAbsoluteFile().toString());
	    wait.until(ExpectedConditions.stalenessOf(input));
	}
	
	public static String takeDashBoardScreenShot(String FilePath) throws IOException, InterruptedException{
		String imageFilePath=null;
		
		File file = new File("library/IEDriver/IEDriverServer.exe");
        
		/*DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		capabilities.setCapability("allow-blocked-content", true);
		capabilities.setCapability("allowBlockedContent", true);
				*/
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//library//ChromeDriver/chromedriver.exe");
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("--disable-extensions");
    	WebDriver driver2 = new ChromeDriver(options);
    	driver2.manage().deleteAllCookies();
        driver2.manage().window().maximize();
		/*
		System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
        WebDriver driver2=new InternetExplorerDriver();
        driver2.manage().window().maximize();*/
        driver2.get(FilePath);
        /*driver2.navigate().to(FilePath);*/
        Thread.sleep(5000);
        driver2.findElement(By.xpath("//a[@class='dashboard-view']")).click();
        Thread.sleep(5000);
        imageFilePath= System.getProperty("user.dir") +File.separator+"Reports"+File.separator+"Dashboard.jpg";
        File scrFile = ((TakesScreenshot)driver2).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File(imageFilePath));

        driver2.close();
        driver2.quit();
        
		return imageFilePath;
	}

	/*public static void main(String[] args) {
		System.out.println(getRandomValue(null, 10, 90, 10));
	}*/
}
