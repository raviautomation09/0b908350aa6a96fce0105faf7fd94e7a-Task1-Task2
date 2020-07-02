package com.maf.tests;

import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.maf.core.CustomAssertion;
import com.maf.core.MainDriver;
import com.maf.models.CreateBotModel;
import com.maf.models.Credentials;
import com.maf.pages.HomePage;
import com.maf.pages.LoginPage;
import com.maf.services.ServicesKeywords;
import com.maf.web.WebKeywords;

public class AutomateIOTest extends MainDriver {
	
	static Logger logger = Logger.getLogger("devpinoyLogger");
	
	
	@Parameters({"credentials","CreateBot"})
	@Test(description="Task 1: Selenium Test Automation")
	public void PerformTask1(String credentials, String CreateBot)throws Exception {
		WebDriver driver = setWebDriver(new Object(){}.getClass().getEnclosingMethod().getName(), "Chrome", "https://automate.io/app/login");
		WebKeywords keywords= new WebKeywords(driver);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage =  new HomePage(driver);
		
		logger.info("Fetching the login credentials..");
		Gson gson = new Gson();
		Credentials cred = gson.fromJson(credentials, Credentials.class);
		CreateBotModel createBot = gson.fromJson(CreateBot, CreateBotModel.class);
		
		logger.info("TestStep 1:Login to application");
		CustomAssertion.AssertTrue("Verify login success", loginPage.LoginToApplication(cred), "Failed to login");
		logger.info("TestStep 1: PASSED");
		
		logger.info("TestStep 2:Create a bot that sends an email to another Gmail account (input: to-email)");
		homePage.CreateBot(createBot);
		logger.info("TestStep 2: PASSED");
		
		logger.info("Closing the driver instance..");
		keywords.closeBrowser();
		keywords.quitBrowser();
	}
	
	@Parameters({"credentials"})
	@Test(description="Task 2: Backend API Test Automation")
	public void PerformTask2(String credentials)throws Exception {
		
		WebDriver driver = setWebDriver(new Object(){}.getClass().getEnclosingMethod().getName(), "Chrome", "https://automate.io/app/login");
		LoginPage loginPage = new LoginPage(driver);		
		
		logger.info("Fetching the login credentials..");
		Gson gson = new Gson();
		Credentials cred = gson.fromJson(credentials, Credentials.class);
		
		
		ServicesKeywords keywords = new ServicesKeywords();
		keywords.user_loads_a_Request("Login", CONFIG.getProperty("URI")+"/auth/login");
		
		//Body Substitution
		keywords.user_substitute_single_value("emailID", cred.username);
		keywords.user_substitute_single_value("password", cred.password);
		
		// Header Substitution		
		keywords.user_addsHeader("Content-Type", "application/json");
		keywords.user_addsHeader("Accept", "application/json");
		keywords.user_addsHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
		
		// Request Post
		keywords.user_posts_request("POST");
		
		// Validation
		CustomAssertion.AssertTrue("Verify Server Response Code", 
				keywords.user_verifies_server_code("200")
				, "Server Code Did not matched Actual:"+ServicesKeywords.serverResponseCode +" Expected:200" );
	
		
		CustomAssertion.AssertTrue("Verify the OrganizationId", 
				keywords.user_verifies_response_value("success", "$.status"), 
				"Failed: Status Success is not available");
		System.out.println(keywords.user_gets_response_Header_value("XSRF-TOKEN"));
		logger.info(GlobalVariableMap);
		
		
		
		logger.info("TestStep 1:Login to application");
		CustomAssertion.AssertTrue("Verify login success", loginPage.LoginToApplication(cred), "Failed to login");
		
		
		System.out.println(driver.manage().getCookieNamed("XSRF-TOKEN"));
		Cookie xsrf = driver.manage().getCookieNamed("XSRF-TOKEN");
		
		String Cookie="";
		for(Cookie ck : driver.manage().getCookies())							
        {			
			Cookie = Cookie+";"+ck.getName()+"="+ck.getValue();																									
                         
        }
		System.out.println(Cookie);
		
		GlobalVariableMap.put("xsrfToken", xsrf.getValue());
		driver.close();
		driver.quit();
		
		
		/**  WorkFlow Api Testing **/
		
		keywords.user_loads_a_Request("CreateBot", CONFIG.getProperty("URI") + "/workflows");

		// Body Substitution
		/*
		 * keywords.user_substitute_single_value("emailID", cred.username);
		 * keywords.user_substitute_single_value("password", cred.password);
		 */

		// Header Substitution
		keywords.user_addsHeader("Content-Type", "application/json");
		keywords.user_addsHeader("Accept", "application/json");
		keywords.user_addsHeader("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
		keywords.user_addsHeader("x-xsrf-token", GlobalVariableMap.get("xsrfToken"));
		keywords.user_addsHeader("Cookie", Cookie);
		
		
		// Request Post
		keywords.user_posts_request("POST");

		// Validation
		CustomAssertion.AssertTrue("Verify Server Response Code", keywords.user_verifies_server_code("200"),
				"Server Code Did not matched Actual:" + ServicesKeywords.serverResponseCode + " Expected:200");

		CustomAssertion.AssertTrue("Verify the OrganizationId",
				keywords.user_verifies_response_value("success", "$.status"),
				"Failed: Status Success is not available");
		keywords.user_stores_value_to_globalVar("$.data._id", "workFlowID");
		
		
		/**  WorkFlow Enabled Api Testing **/
		
		keywords.user_loads_a_Request("empty", CONFIG.getProperty("URI") + "/workflows/"+GlobalVariableMap.get("workFlowID")+"/enabled?skipPremiumCheck=true");

		// Header Substitution
		keywords.user_addsHeader("Content-Type", "application/json");
		keywords.user_addsHeader("Accept", "application/json");
		keywords.user_addsHeader("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
		keywords.user_addsHeader("x-xsrf-token", GlobalVariableMap.get("xsrfToken"));
		keywords.user_addsHeader("Cookie", Cookie);
		
		
		// Request Post
		keywords.user_posts_request("POST");

		// Validation
		CustomAssertion.AssertTrue("Verify Server Response Code", keywords.user_verifies_server_code("200"),
				"Server Code Did not matched Actual:" + ServicesKeywords.serverResponseCode + " Expected:200");

		CustomAssertion.AssertTrue("Verify the OrganizationId",
				keywords.user_verifies_response_value("success", "$.status"),
				"Failed: Status Success is not available");
		
		
		/**  Logout Api Testing **/
		
		keywords.user_loads_a_Request("empty", CONFIG.getProperty("URI") + "/auth/logout");

		// Header Substitution
		keywords.user_addsHeader("Content-Type", "application/json");
		keywords.user_addsHeader("Accept", "application/json");
		keywords.user_addsHeader("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
		
		// Request Post
		keywords.user_posts_request("DELETE");

		// Validation
		CustomAssertion.AssertTrue("Verify Server Response Code", keywords.user_verifies_server_code("200"),
				"Server Code Did not matched Actual:" + ServicesKeywords.serverResponseCode + " Expected:200");

		CustomAssertion.AssertTrue("Verify the OrganizationId",
				keywords.user_verifies_response_value("success", "$.status"),
				"Failed: Status Success is not available");
		
	}
}
