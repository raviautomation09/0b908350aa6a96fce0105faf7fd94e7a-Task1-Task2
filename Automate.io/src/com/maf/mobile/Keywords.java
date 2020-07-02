package com.maf.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.maf.core.MainDriver;
import com.maf.core.XMLObjectRepository;


public class Keywords {
	public AppiumDriver<?> driver;
	public Actions action;
	
	XMLObjectRepository or;
	Document dc;
	static Logger logger = Logger.getLogger("devpinoyLogger");
	SoftAssert sa;
	
	public Keywords(AppiumDriver<?> driver) {
		 sa = new SoftAssert();
		this.driver = driver;
		or = new XMLObjectRepository(driver);
		this.dc = MainDriver.ObjectRepoDocument;
		this.action = new Actions(driver);
	}
	
	
	public boolean waitForElement(String Object, String time){
		boolean elementPresence = false;
		logger.info("********** [Command: waitForElement : Element:"+Object+" : waitTime = "+time+"seconds]**********");
		logger.info("Keyword: waitForElement Element:"+Object);
		for (int i = 0; i < Integer.parseInt(time); i++) {			
			try {
				Thread.sleep(1000);
				MobileElement element = or.getLocators(dc, Object);				
					if (element.isDisplayed()) {
						logger.info("element found..!!");
						elementPresence = true;
						return elementPresence;
					}
				
			} catch (Exception e) {
				//logger.info("element NOT found [wait time counter "+i+"/"+time+"] sec.");				
			}		
		}
		
		logger.info("********** [Command: waitForElement ENDS ElementPresence is:"+elementPresence+"]********** ");
		return elementPresence;
	}
	
	public void type(String Object, String text){
		logger.info("********** [Command: type Object:"+Object+" Text:"+text+" ] ********** ");
		try {
			//waitForElement(Object, "5");
			boolean elePresence= false;		
			
			for (int i = 0; i < 2; i++) {
				//MobileElement element =or.getLocators(dc, Object);
				try {
					MobileElement element2 =or.getLocators(dc, Object);					
					elePresence = element2.isDisplayed();
					if (elePresence) {
						element2.click();
					}
				} catch (Exception e) {
					swipeBottomToTopForElement(Object);
				}
				
				if (elePresence) {				
					Thread.sleep(2000);
					try {
						MobileElement element2 =or.getLocators(dc, Object);	
						elePresence = element2.isDisplayed();
					} catch (Exception e) {
						swipeBottomToTopForElement(Object);
						elePresence=false;						
					}
					
					try {
						MobileElement element3 =or.getLocators(dc, Object);	
						elePresence = element3.isDisplayed();
					}catch(Exception e){
						
					}
					
					if (elePresence) {
						MobileElement element3 =or.getLocators(dc, Object);	
						element3.sendKeys(text);
						try {
							driver.hideKeyboard();
						} catch (Exception e) {
							
						}
						
						Thread.sleep(2000);
						break;
					}else {
						swipeBottomToTopForElement(Object);
					}
				}else {
					swipeBottomToTopForElement(Object);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("********** [Command: type ENDS] ********** ");
			Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
			Reporter.log("********** [FAILED:Command: type | Object:"+Object+" | Text:"+text+" | Exception:+"+e.getMessage()+"] ********** ");			
			
//			org.testng.Assert.fail("********** [FAILED:Command: type | Object:"+Object+" | Text:"+text+" | Exception:+"+e.getMessage()+"] ********** ");
		}
		logger.info("********** [Command: type ENDS] ********** ");
	}
	
	public void click(String Object){
		logger.info("********** [Command: Click Elememt:"+Object+"] ********** ");
		try {
			MobileElement element =or.getLocators(dc, Object);
			logger.info("Clicking element :"+Object);
			waitForElement(Object, "15");
			element.click();
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("********** [Command: Click ENDS] ********** ");
		}
		
		logger.info("********** [Command: Click ENDS] ********** ");
	}
	
	
	
	
	/**
	 * 
	 * {@link Description} : This method is used to swipe from bottom point to top
	 * 
	 * <p>
	 */
	public void swipeBottomToTopForElement(String object) {
		logger.info("********** [Command: swipeBottomToTopForElement Element:"+object+"] ********** ");
		boolean elePresence= false;
		try {
			Dimension size = driver.manage().window().getSize();
			/*(1080, 1920)
			960:540*/
			
			
				System.out.println(size);
			 	int heightHalf = (int) (size.height / 3);	  //		 640	
			 	int widhtHalf = size.width / 3;				//			360
			 	System.out.println(heightHalf +":" + widhtHalf);
			 	
			   
			  //Find swipe start and end point from screen's with and height.
			  //Find starty point which is at bottom side of screen.
			  int starty = (int) (size.height * 0.80);
			  //Find endy point which is at top side of screen.
			  int endHeightHalf = (int) (heightHalf - (heightHalf * 0.50)) ;         //320
			  //Find horizontal point where you wants to swipe. It is in middle of screen width.
			  int startx = (int) (size.width / 1.5);
			  
			//Swipe from Top to Bottom.
			  for (int i = 0; i < 10; i++) {				  //			720		640			720		320
				  
				  Thread.sleep(2000);
					try {
						MobileElement element2 = or.getLocators(dc, object);
						elePresence = element2.isDisplayed();
					} catch (Exception e) {
						logger.info("Element is currently not visible. Lets swip from bottom to top again !!!!");
						continue;
					}
					
					if (elePresence) {
						logger.info("Element is visible. Let Move on !!!!");
						break;
					}
					
					driver.swipe(startx,heightHalf , startx, endHeightHalf, 3000);
			  }
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("********** [Command: swipeBottomToTopForElement ENDS] ********** ");
		}
		logger.info("********** [Command: swipeBottomToTopForElement ENDS] ********** ");
	}
	
	
	/**
	 * 
	 * {@link Description} : This method is used to swipe from top point to bottom
	 * 
	 * <p>
	 */
	public void swipeTopToBottomForElement(String object) {
		logger.info("********** [Command: swipeTopToBottomForElement] ********** ");
		boolean elePresence= false;
		try {
			Dimension size = driver.manage().window().getSize();
			/*(1080, 1920)
			960:540*/		
			
				System.out.println(size);
			 	int heightHalf = (int) (size.height / 2);			 	
			 	int widhtHalf = size.width / 3;
			 	System.out.println(heightHalf +":" + widhtHalf);
			 	
			   
			  //Find swipe start and end point from screen's with and height.
			  //Find starty point which is at bottom side of screen.
			  int starty = (int) (size.height * 0.80);
			  //Find endy point which is at top side of screen.
			  int endHeightHalf = (int) (heightHalf + (heightHalf * 0.30)) ;
			  //Find horizontal point where you wants to swipe. It is in middle of screen width.
			  int startx = (int) (size.width -20);
			  
			 /* //Swipe from Bottom to Top.
			  driver.swipe(startx,heightHalf , startx, endHeightHalf, 3000);
			  Thread.sleep(2000);*/
			  
			//Swipe from Top to Bottom.
			  for (int i = 0; i < 10; i++) {
				  
				  
				  				//1060		960			720		640
				  driver.swipe(startx,heightHalf, startx, endHeightHalf  , 3000);
				  Thread.sleep(2000);
					try {
						MobileElement element2 = or.getLocators(dc, object);
						elePresence = element2.isDisplayed();
					} catch (Exception e) {
						logger.info("Element is currently not visible. Lets swip from top to bottom again !!!!");
						continue;
						
					}
					
					if (elePresence) {
						logger.info("Element is visible. Let Move on !!!!");
						break;
					}
				  
			  }
			  
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("********** [Command: swipeTopToBottomForElement ENDS] ********** ");
		}
		logger.info("********** [Command: swipeTopToBottomForElement ENDS] ********** ");
	}
	
	
	/**
	 * 
	 * {@link Description} : This method is used to swipe from A point to B
	 * 
	 * <p>
	 */
	public void swipeTopToBottom() {
		logger.info("********** [Command: swipeTopToBottom] ********** ");
		boolean elePresence= false;
		Dimension size;
		try {
			 //Get the size of screen.
			  size = driver.manage().window().getSize();
			  System.out.println(size);
			   
			  //Find swipe start and end point from screen's with and height.
			  //Find starty point which is at bottom side of screen.
			  int starty = (int) (size.height * 0.80);
			  //Find endy point which is at top side of screen.
			  int endy = (int) (size.height * 0.20);
			  //Find horizontal point where you wants to swipe. It is in middle of screen width.
			  int startx = size.width / 2;
			  System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
			  
			  
			  //Swipe from Bottom to Top.
			  driver.swipe(startx, starty, startx, endy, 3000);
			  Thread.sleep(2000);
			  /*
			  //Swipe from Top to Bottom.
			  driver.swipe(startx, endy, startx, starty, 3000);
			  Thread.sleep(2000);*/
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("********** [Command: swipeBottomToTopForElement ENDS] ********** ");
		}
		logger.info("********** [Command: swipeBottomToTopForElement ENDS] ********** ");
	}
	
	

	
}
