package com.maf.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.maf.mobile.Keywords;

public class KeywordExecutor {
	public Method method[];
	AppiumDriver<MobileElement> driver;
	Keywords keyword;
	static Logger logger = Logger.getLogger("devpinoyLogger");
	
	public KeywordExecutor() {		
		keyword= new Keywords(driver);
		method = keyword.getClass().getMethods();
	}
	
	public void executeKeyword(String currentKeyword, String Object, String data){
		String keyword_execution_result = "";
		for (int i = 0; i < method.length; i++) {
			if (method[i].getName().equalsIgnoreCase(currentKeyword)) {
				try {
					keyword_execution_result = (String) method[i].invoke(keyword, Object, data);
					if (keyword_execution_result.equalsIgnoreCase("PASS")) {
						
					}else{
						
					}
				} catch (Exception e) {
					logger.error("******* Exception during the Keyword execution *******:" + e);
					logger.error("Message:" + e.getMessage());
					logger.error("LocalizedMessage:" + e.getLocalizedMessage());
					logger.error("Exception during the Keyword execution:" + e);
				}
			}
		}
		
	}
}
