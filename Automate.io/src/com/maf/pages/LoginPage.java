package com.maf.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.maf.models.Credentials;
import com.maf.web.WebKeywords;

public class LoginPage {
	public WebDriver driver;
	static Logger logger = Logger.getLogger("devpinoyLogger");
	WebKeywords keyword;
	ReusableFunctions reuse;
	
	
	public LoginPage(WebDriver driver) {
		this.driver=driver;
		reuse = new ReusableFunctions(driver);
	}
	
	/**
	 * Login to automate.io portal using credentials supplied
	 * @param UserName
	 * @param Password
	 * @return
	 * @throws InterruptedException
	 */
	public boolean LoginToApplication(Credentials cred) throws InterruptedException{
		keyword = new WebKeywords(driver);
		keyword.type("txtEmailID", cred.username);
		keyword.type("txtPassword", cred.password);
		keyword.click("btnLogin");
		if (keyword.waitForElement("lnkCreateBot", "30").equalsIgnoreCase("PASS")) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
}
