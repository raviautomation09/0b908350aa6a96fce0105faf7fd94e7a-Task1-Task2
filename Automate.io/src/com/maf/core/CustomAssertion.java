package com.maf.core;

import org.testng.Assert;
import org.testng.Reporter;

public class CustomAssertion {

	public static void AssertTrue(String AssertionDescription,
			boolean ActualVal, String FailureMessage) {
		Reporter.log("<b>" + AssertionDescription + "<br>");
		Assert.assertTrue(ActualVal, FailureMessage);
	}

	public static void AssertEquals(String AssertionDescription,
			Object ExpectedVal, Object ActualVal, String FailureMessage) {
		Reporter.log("<b>" + AssertionDescription + "<br>");
		Assert.assertEquals(ExpectedVal, ActualVal, FailureMessage);
	}

	public static void AssertFalse(String AssertionDescription,
			boolean ActualVal, String FailureMessage) {
		Reporter.log("<b>" + AssertionDescription + "<br>");
		Assert.assertFalse(ActualVal, FailureMessage);
	}
	
	public static void AssertInfo(String AssertionDescription){
		Reporter.log(AssertionDescription);
	}
}
