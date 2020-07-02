package com.maf.services;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.google.gson.JsonParseException;
import com.jayway.jsonpath.JsonPath;
import com.maf.core.MainDriver;
import com.rest.services.RestClientAPI;
import com.rest.services.RestClientAPI.RequestMethod;

public class ServicesKeywords {
	static Logger logger = Logger.getLogger("devpinoyLogger");
	
	public static RestClientAPI client;
	public static String url;
	public static String request;
	public static String response;
	public static int serverResponseCode;
	
	private HashMap<String, String> globalVariable;
	
	public ServicesKeywords() {
		globalVariable = new HashMap<String, String>();
	}
	
	
	public static String GetAPIResponse(String URI, String endpoint, String key) {
		RestClientAPI client = new RestClientAPI(URI+endpoint);
        String payLoad= "{}";
        client.AddHeader("Content-Type", "application/json");
        client.AddHeader("apiKey", key);
        

        try {
            client.Execute(RequestMethod.GET,payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = client.getResponse();
        System.out.println(response);
        return response;
	}
	
	
	
	public void user_loads_a_Request(String RequestName, String URL){
		this.url=URL;
		logger.info("Loading URL :"+this.url);
		logger.info("Request is :"+RequestName);
		try {
			if (RequestName.toLowerCase().contains("empty")) {
				request = "{}";
			} else {
				request = ServicesUtility.requestObject.get(RequestName);
				logger.info("Successfully loaded Below request:"+System.lineSeparator()+ServicesUtility.formatJSONString(request));
			}
			
			client = new RestClientAPI(URL);
			logger.info("---------------------------------------------------------------------------------------");
		} catch (JsonParseException e) {
			logger.error(e);
			logger.info("---------------------------------------------------------------------------------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			logger.info("---------------------------------------------------------------------------------------");
		}
		
	}
	
	
	/**
	 * 
	 * {@link Description} :It is used to substitute header using hash map
	 * <p>
	 */
	public void user_addsHeader_details(HashMap<String, String> headerData) {

		for (String data : headerData.keySet()) {
			logger.info("Substituting header:" + data+ ":" + headerData.get(data));
			client.AddHeader(data, headerData.get(data));
			logger.info("---------------------------------------------------------------------------------------");
		}
	}
	public void user_addsHeader(String Key , String Value) {

			logger.info("Substituting header:" + Key+ ":" +Value);
			logger.info("---------------------------------------------------------------------------------------");
			client.AddHeader(Key, Value);
		
	}
	
	public void user_substitute_request_details(HashMap<String, String> bodyData) {
			
			for (String data : bodyData.keySet()) {				
					request = request.replaceAll("\\$\\{" + data + "}",	bodyData.get(data));				
	
			}	
			logger.info("Final Substituted payLoad is:" + request);
			logger.info("---------------------------------------------------------------------------------------");
	}
	
	public void user_substitute_single_value(String Key, String Value) {				
		logger.info("Substituting Key:"+Key+" --> Value:"+Value);
		request = request.replaceAll("\\$\\{" + Key + "}",	Value);			
		logger.info("Substituted payLoad is:" + request);
		logger.info("---------------------------------------------------------------------------------------");
	}
	
	public void user_adds_params_details(HashMap<String, String> paramData) {

		for (String data : paramData.keySet()) {
			logger.info("Adding Params:" + data + ":" + paramData.get(data));
			client.AddParam(data, paramData.get(data));
			logger.info("---------------------------------------------------------------------------------------");
		}
		
	}
	
	
	public boolean user_posts_request(String apiTYPE) {

		try {
			logger.info("---------------------------------------------------------------------------------------");
			logger.info("Posting Request to URL:" + url);
			switch (apiTYPE.trim().toUpperCase()) {
			case "POST":
				client.Execute(RequestMethod.POST, request);
				response = client.getResponse();
				serverResponseCode = client.getResponseCode();
				logger.info("Response Code from the Server is:[" + serverResponseCode
						+ "]");
				logger.info("Response from Server is:\n"
						+ ServicesUtility.formatJSONString(response));
				logger.info("---------------------------------------------------------------------------------------");
				
				break;
			case "PUT":
				client.Execute(RequestMethod.PUT, request);
				response = client.getResponse();
				serverResponseCode = client.getResponseCode();
				logger.info("Response Code from the Server is:[" + serverResponseCode
						+ "]");
				logger.info("Response from Server is:\n"
						+ ServicesUtility.formatJSONString(response));
				logger.info("---------------------------------------------------------------------------------------");
				break;
			case "GET":
				client.Execute(RequestMethod.GET, request);
				response = client.getResponse();
				serverResponseCode = client.getResponseCode();
				logger.info("Response Code from the Server is:[" + serverResponseCode
						+ "]");
				logger.info("Response from Server is:\n" + response);
				logger.info("---------------------------------------------------------------------------------------");

				break;
			case "DELETE":
				client.Execute(RequestMethod.DELETE, "");
				response = client.getResponse();
				serverResponseCode = client.getResponseCode();
				logger.info("Response Code from the Server is:[" + serverResponseCode+ "]");
				logger.info("Response from Server is:\n" + response);
				logger.info("---------------------------------------------------------------------------------------");
				break;

			default:
				break;
			}
			Thread.sleep(3000);
			
			

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	
	public boolean user_verifies_server_code(String code) {
		if (Integer.parseInt(code) == serverResponseCode) {
			logger.info("Servercode Matched");
			return true;
		} else {
			logger.error("Server code did not matched: Expected["
					+ Integer.parseInt(code) + "] ---> Actual[" + serverResponseCode
					+ "]");
			return false;
		}

	}
	
	/**
	 * 
	 * {@link Description} :It is used to verify response data using json xpath ex:  $.x[1].y
	 * <p>
	 */
	public boolean user_verifies_response_value(String responseValue, String xpath) {
		String valvalue = "";
		try {			
			if (JsonPath.read(response, xpath).getClass().equals(Double.class)) {
				valvalue=Double.toString(JsonPath.read(response, xpath));
			}else if(JsonPath.read(response, xpath).getClass().equals(Integer.class)){
				valvalue=Integer.toString(JsonPath.read(response, xpath));
			}else if(JsonPath.read(response, xpath).getClass().equals(Float.class)){
				valvalue=Float.toString(JsonPath.read(response, xpath));
			}else{
				valvalue = JsonPath.read(response, xpath);
			}
			
			logger.info("Response value fetched using Xpath ["+xpath+"] is ["+valvalue+"]");
			
			if (valvalue.toLowerCase().contains(responseValue.toLowerCase())) {				
				logger.info("Response Validation: Matched- Actual[" + valvalue	+ "] ---> Expected[" + responseValue + "]");
				logger.info("---------------------------------------------------------------------------------------");
				return true;
			} else {
				logger.error("Response Validation FAILED: Actual[" + valvalue	+ " ---> Expected[" + responseValue + "]");
				logger.info("---------------------------------------------------------------------------------------");
				return false;
			}
		} catch (Exception e) {
			logger.error("user_verifies_response_value Keyword FAILED to fetch xpath["+xpath+"] data from response:"	+ response);
			logger.error("FAILED:" + e);
			logger.info("---------------------------------------------------------------------------------------");
			return false;
		}

	}
	
	
	
	public boolean user_verifies_response_value_contains(String ExpresponseValue) {
		
		int index = response.indexOf(ExpresponseValue);
		if (index != -1) // -1 means "not found"
		{
			logger.info("Response Validation: Matched- Actual["
					+ ExpresponseValue + " ---> Expected[" + response + "]");
			logger.info("---------------------------------------------------------------------------------------");
			return true;
		}else {
			logger.error("Response Validation FAILED: Actual["
					+ ExpresponseValue + " ---> Expected[" + response + "]");
			logger.info("---------------------------------------------------------------------------------------");
			return false;
		}

	}
	
	
	public boolean user_stores_value_to_globalVar(String xpath, String keyName) {
		String valvalue="";
		try {			/*
			String valvalue = JsonPath.read(response, xpath);*/
			
			
			if (JsonPath.read(response, xpath).getClass().equals(Double.class)) {
				valvalue=Double.toString(JsonPath.read(response, xpath));
			}else if(JsonPath.read(response, xpath).getClass().equals(Integer.class)){
				valvalue=Integer.toString(JsonPath.read(response, xpath));
			}else if(JsonPath.read(response, xpath).getClass().equals(Float.class)){
				valvalue=Float.toString(JsonPath.read(response, xpath));
			}else{
				valvalue = JsonPath.read(response, xpath);
			}
			
			
			if (!valvalue.equals(null) || !valvalue.equalsIgnoreCase("")) {
				logger.info("Storing data with key [" + keyName
						+ "] and value as [" + valvalue + "]");
				MainDriver.GlobalVariableMap.put(keyName, valvalue);
				logger.info("Successfully Stored data with key [" + keyName
						+ "] and value as [" + valvalue + "]");
				logger.info("---------------------------------------------------------------------------------------");

			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("user_verifies_response_value Keyword FAILED to fetch xpath["+xpath+"] data from response:"	+ response);
			logger.error("FAILED:" + e);
			logger.info("---------------------------------------------------------------------------------------");
			return false;
		}
		
		return true;
	}
	
	public String user_gets_response_Header_value(String Key) {
		String Value;
		try {
			Value = client.httpResponse.getFirstHeader(Key).getValue();
			logger.info("Fetched Header is "+Key+":"+Value);
			logger.info("---------------------------------------------------------------------------------------");
		} catch (Exception e) {
			logger.error("Exception Occurred:"+e.getStackTrace());
			logger.error(e.getMessage());
			logger.info("---------------------------------------------------------------------------------------");
			return null;
		}
		
		return Value;
	}
	
	public String generateBasicAuthKey(String usr, String pwd) {
		String authHeader=null;
		try {
			logger.info("---------------------------------------------------------------------------------------");
			logger.info("Generating the encrypted access token using below cred:"+System.lineSeparator());
			logger.info("User:"+usr+" Password:"+pwd);
			String auth = "admin" + ":" + "admin";
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			authHeader = "Basic " + new String(encodedAuth);			
			logger.info("---------------------------------------------------------------------------------------");
		} catch (Exception e) {
			logger.error("Exception Occurred:"+e.getStackTrace());
			logger.info("---------------------------------------------------------------------------------------");
			return null;
		}
		
		return authHeader;
	}
}
