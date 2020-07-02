package com.maf.core;

import org.apache.log4j.Logger;
import org.testng.Reporter;
 
 public class Log {
	 
	 //Initialize Log4j logs
 
	 //public static Logger Log = Logger.getLogger(Log.class.getName());
	 static Logger logger = Logger.getLogger("devpinoyLogger"); 
 
	 // This is to print log for the beginning of the test case, as we usually run so many test cases as a test suite 
	 public static void startTest(String strTestName){ 
		logger.info("****************************************************************************************");	 
		logger.info("****************************************************************************************");	 
		logger.info("$$$$$$$$$$$$$$$$$$$$$                "+ "-S--T--A--R--T "+strTestName+ "       $$$$$$$$$$$$$$$$$$$$$$$$$");	 
		logger.info("****************************************************************************************");	 
		logger.info("****************************************************************************************");
	}
 
	//This is to print log for the ending of the test case 
	 public static void endTest(String strTestName){
		logger.info("XXXXXXXXXXXXXXXXXXXXXXX             "+ "-E---N---D-"+ strTestName +"             XXXXXXXXXXXXXXXXXXXXXX");	 
		logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");	 
		logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");	 
	}

 	public static void reportStatus(String message) {
		logger.info("Info: " +message);
		Reporter.log("Info: " + message);
	}
	 	
 	public static void reportPass(String message) {
		logger.info("Pass: " + message);
		Reporter.log("Pass: " + message);
	}
 
 	public static void reportFail(String message) { 
 		logger.error("Fail: " +message);
 		Reporter.log("Fail: " + message);
	}

}