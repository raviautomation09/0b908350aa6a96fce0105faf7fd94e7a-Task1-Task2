package com.maf.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.maf.core.CustomAssertion;
import com.maf.models.CreateBotModel;
import com.maf.web.Constants;
import com.maf.web.WebKeywords;

public class HomePage {
	public WebDriver driver;
	static Logger logger = Logger.getLogger("devpinoyLogger");
	WebKeywords keyword;
	ReusableFunctions reuse;
	
	
	public HomePage(WebDriver driver) {
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
	public boolean CreateBot(CreateBotModel createBot) throws InterruptedException{
		keyword = new WebKeywords(driver);
		keyword.click("lnkCreateBot");
		CustomAssertion.AssertEquals("Verify Gmail App is authenticated", 
				Constants.KEYWORD_PASS, 
				keyword.waitForElement("lnkFromGmail", "30"), 
				"Gmail App is not pre authenticated!!!");
		keyword.click("lnkFromGmail");
		keyword.type("txtTriggerEvent", "New Email");
		keyword.click("spnTriggerEventNewEmail");
		keyword.click("cboFolder");
		keyword.type("txtFolderSearch", createBot.newMailFolderName);
		keyword.waitForElement("spnINBOXList", "30");
		keyword.click("spnINBOXList");
		keyword.waitForElement("cboActionApp", "30");
		keyword.click("cboActionApp");
		keyword.click("spnToGmail");
		keyword.click("spnSendEmail");
		
		keyword.click("btnFromName");
		keyword.click("spnFromNameTrigger");
		keyword.click("btnFromAddresTrigger");
		keyword.click("spnAddressTrigger");
		keyword.type("spnToNameTrigger", createBot.toName);
		keyword.type("spnToAddressTrigger", createBot.toAddress);
		
		keyword.click("btnSubjectAdd");
		keyword.waitForElement("btnSubjectAddTrigger", "15");
		keyword.click("btnSubjectAddTrigger");
		
		keyword.pause("2");
		keyword.click("btnEmailBodyAdd");
		keyword.click("btnEmailBodyAdd");
		keyword.pause("2");
		keyword.KeyPress("", "Body");
		
		keyword.waitForElement("btnEmailBodyTrigger", "15");
		keyword.click("btnEmailBodyTrigger");
		keyword.click("btnSaveBot");
		
		CustomAssertion.AssertEquals("Verify the bot activation text",
				Constants.KEYWORD_PASS,
				keyword.waitForElement("spnOFFBot", "15"),
				"FAILED: Bot not activated");
		keyword.click("spnOFFBot");
		
		CustomAssertion.AssertEquals("Verify the bot activation Im Done", 
				Constants.KEYWORD_PASS,
				keyword.waitForElement("btnImDone", "15"),
				"FAILED: bot activation Im Done not present");
		keyword.click("btnImDone");
		
		CustomAssertion.AssertEquals("Verify the bot activation text", 
				Constants.KEYWORD_PASS, 
				keyword.verifyTextOnPage("Checking for \"New Email\""), 
				"FAILED: Bot not activated");
		
		return false;
		
		
	}
	
	
	
}
