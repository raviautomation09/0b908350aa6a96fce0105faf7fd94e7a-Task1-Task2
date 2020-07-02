package com.maf.pages;

import java.awt.AWTException;
import java.util.HashMap;
import java.util.List;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import com.maf.mobile.Keywords;
import com.maf.core.MainDriver;
import com.maf.core.XMLObjectRepository;

public class ReusableFunctions {
	static Logger logger = Logger.getLogger("devpinoyLogger");
	public AppiumDriver<MobileElement> driver;
	Keywords keyword;
	static WebDriver driverWeb;
	
	public ReusableFunctions(AppiumDriver<MobileElement> driver) {
		this.driver = driver;
		keyword = new Keywords(driver);
	}
	
	
	public ReusableFunctions(WebDriver driver) {
		this.driverWeb = driver;
	}
	
	public ReusableFunctions() {
	}
	
		
}
