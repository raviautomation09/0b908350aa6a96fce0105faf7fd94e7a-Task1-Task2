package com.maf.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import automation_home.ddf.constants.ExcelConstants;
import automation_home.ddf.wrapper.Wrapper;
import automation_home.ddf.wrapperimpl.ExcelWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.maf.core.MainDriver;
import com.maf.main.Executor;


public class ServicesUtility {
	
	public static HashMap<String, String> requestObject;
	
	static Logger logger = Logger.getLogger("devpinoyLogger");
	
	public static void loadAllRequests() {
		requestObject = new HashMap<String, String>();
		String folderPath = System.getProperty("user.dir") + File.separator + Executor.CONFIG.getProperty("PayLoadPath");
		try {
			List<Path> pathList = Files.walk(Paths.get(folderPath)).filter(Files::isRegularFile).collect(Collectors.toList());
			for (Path path : pathList) {
				String FileName =  new File(path.toString()).getName();
				//System.out.println(FileName);
				
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(new File(path.toString()));
					String targetFileData = IOUtils.toString(fis, "UTF-8");
					FileName = FileName.split("\\.")[0];
					//System.out.println(FileName);
					requestObject.put(FileName, targetFileData);
					// System.out.println("****************************************************************");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * 
	 * {@link Description} : This method is used to format String to pretty JSON
	 * <p>
	 */
	public static String formatJSONString(String payLoad)
			throws JsonParseException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(payLoad);
		String prettyJsonString = gson.toJson(je);

		return prettyJsonString;
	}
	
	
	
	public static Object[][] getTestDataDataProvider(String SheetName, String TestCaseName) throws Exception {
		Wrapper wrapper = new ExcelWrapper();
		wrapper.setParameter(ExcelConstants.FILE_PATH,	new File("").getAbsolutePath()	+ File.separator+ Executor.CONFIG.getProperty("TestDataPath"));
		wrapper.setParameter(ExcelConstants.SHEET_NAME, SheetName);
		wrapper.setParameter(ExcelConstants.TESTCASE_NAME, TestCaseName);
		wrapper.setParameter(ExcelConstants.TESTCASE_START_ELEMENT, "_Start");
		wrapper.setParameter(ExcelConstants.TESTCASE_END_ELEMENT, "_End");
		
		wrapper.setParameter(ExcelConstants.INCLUDE_TESTDATA_HEADER_NAME, "Execution");
		wrapper.setParameter(ExcelConstants.INCLUDE_TESTDATA_YES,"RUN");
		wrapper.setParameter(ExcelConstants.INCLUDE_TESTDATA_NO,"NO-RUN");
		 
		//wrapper.setParameter(ExcelConstants.INITIAL_POSITION, "4,1");
		
		return wrapper.retrieveTestData();
	}
	
	public static List<Map<String, String>> getTestData(String SheetName, String TestCaseName) throws Exception {
		Wrapper wrapper = new ExcelWrapper();
		wrapper.setParameter(ExcelConstants.FILE_PATH,	new File("").getAbsolutePath()	+ File.separator+ Executor.CONFIG.getProperty("TestDataPath"));
		wrapper.setParameter(ExcelConstants.SHEET_NAME, SheetName);
		wrapper.setParameter(ExcelConstants.TESTCASE_NAME, TestCaseName);
		wrapper.setParameter(ExcelConstants.TESTCASE_START_ELEMENT, "_Start");
		wrapper.setParameter(ExcelConstants.TESTCASE_END_ELEMENT, "_End");
		
		wrapper.setParameter(ExcelConstants.INCLUDE_TESTDATA_HEADER_NAME, "Execution");
		wrapper.setParameter(ExcelConstants.INCLUDE_TESTDATA_YES,"RUN");
		wrapper.setParameter(ExcelConstants.INCLUDE_TESTDATA_NO,"NO-RUN");
		 
		//wrapper.setParameter(ExcelConstants.INITIAL_POSITION, "4,1");
		
		return wrapper.retrieveData();
	}
	
	
	

	// +++++++++++++++++++++ USERS UTILITY SCRIPTS ++++++++++++++++++++++++++++++++++++++++++++=
	/**
	 * 
	 * {@link Description} : This method is used to generate Dynamic time stamp
	 * based on format provided which also
	 * <p>
	 * <b>InputExamples:</b>MM-DD-YYY hh:mm:ss
	 * <p>
	 */
	public static String getTimeStamp(String dateFormat) {
		String DateFormat = null;

		logger.info("Generating Time Stamp");
		try {
			logger.info("Input format received is:" + dateFormat);
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			DateFormat = format.format(date);
			logger.info("Formatted time stamp is :" + DateFormat);
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		return DateFormat;

	}

	/**
	 * 
	 * {@link Description} : This method is used to generate random GUID
	 * <p>
	 */
	public static final String getGuid() {
		// generate random UUIDs
		UUID idOne = UUID.randomUUID();
		logger.info("Guid generated is:" + idOne);
		return String.valueOf(idOne);
	}

	/**
	 * 
	 * {@link Description} : This method is used to generate random integer
	 * value within the range min, max
	 * <p>
	 */
	public static String randomWithRange(List<String> args) throws Exception {

		if (args.size() != 2) {
			throw new Exception(
					"Please specify the arguments properly. [MinimumValue, MaximumValue]");
		}

		int min = 0, max = 0;
		min = Integer.parseInt(args.get(0).trim());
		max = Integer.parseInt(args.get(1).trim());

		int range = (max - min) + 1;
		int finalOutput = (int) (Math.random() * range) + min;
		return String.valueOf(finalOutput);
	}

	/**
	 * 
	 * {@link Description} : This method is used to generate random Decimal
	 * value within the range min, max
	 * <p>
	 */
	public static String randomWithinRangeDecimal(List<String> args)
			throws Exception {
		if (args.size() != 2) {
			throw new Exception(
					"Please specify the arguments properly. [MinimumValue, MaximumValue]");
		}

		double min = 0;
		double max = 0;
		min = Double.parseDouble(args.get(0).trim());
		max = Double.parseDouble(args.get(1).trim());

		Random r = new Random();
		double finalOutput = (r.nextInt((int) ((max - min) * 10 + 1)) + min * 10) / 10.0;
		return String.valueOf(finalOutput);
	}

	/**
	 * 
	 * {@link Description} : This method is used to generate random integer
	 * value within the range with decimal places configurable args:min, max,
	 * decimal places
	 * <p>
	 */
	public static String getRandomDecimalValue(List<String> args)
			throws Exception {

		if (args.size() != 3) {
			throw new Exception(
					"Please specify the arguments properly. [Lower bound, upper bound, decimal places]");
		}

		final int lowerBound = Integer.parseInt(args.get(0).trim());
		final int upperBound = Integer.parseInt(args.get(1).trim());
		final int decimalPlaces = Integer.parseInt(args.get(2).trim());

		final Random random = null;
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			throw new IllegalArgumentException("Put error message here");
		}

		final double dbl = ((random == null ? new Random() : random)
				.nextDouble() //
				* (upperBound - lowerBound))
				+ lowerBound;
		return String.format("%." + decimalPlaces + "f", dbl);
	}

	/**
	 * 
	 * {@link Description} : This method is used to generate random alphabetical
	 * string with input length of string required
	 * <p>
	 * <b>Data</b>:7 (This is number of character required)
	 * <p>
	 */

	public static String getRandomAlphaString(String data) {
		String randomString = "";
		try {

			int size = Integer.parseInt(data);
			String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

			for (int i = 0; i < size; i++) {
				char ch = alphabets.charAt((int) (Math.random() * alphabets
						.length()));
				if ((i == 0) && (ch == '0')) {
					while (ch == '0')
						ch = alphabets.charAt((int) (Math.random() * alphabets
								.length()));
				}
				randomString += ch;
			}

		} catch (Exception e) {
			logger.error(e);
			return null;
		}

		return randomString;
	}

	/**
	 * 
	 * {@link Description} : This method is used to generate random numeric
	 * string with input length of string required
	 * <p>
	 * <b>Data</b>:7 (This can number of character required)
	 * <p>
	 * <p>
	 */
	public static String getRandomNumericString(String data) {
		String randomString = "";
		try {

			int size = Integer.parseInt(data);
			String alphabets = "1234567890";

			for (int i = 0; i < size; i++) {
				char ch = alphabets.charAt((int) (Math.random() * alphabets
						.length()));
				if ((i == 0) && (ch == '0')) {
					while (ch == '0')
						ch = alphabets.charAt((int) (Math.random() * alphabets
								.length()));
				}
				randomString += ch;
			}

		} catch (Exception e) {
			logger.error(e);
			return null;
		}

		return randomString;
	}
}
